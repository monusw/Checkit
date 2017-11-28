package xin.monus.checkit.data.source.local

import android.content.ContentValues
import android.content.Context
import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.data.entity.Project
import xin.monus.checkit.data.entity.ProjectType
import xin.monus.checkit.data.source.ActionDataSource
import xin.monus.checkit.data.source.ProjectsDataSource
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.db.LocalTable.ProjectTable


class ProjectsLocalDataSource private constructor(context: Context) : ProjectsDataSource {

    private val dbHelper: LocalDbHelper = LocalDbHelper(context)
    private val actionLocalDataSource = ActionLocalDataSource.getInstance(context)
    companion object {
        private var INSTANCE: ProjectsLocalDataSource? = null
        @JvmStatic fun getInstance(context: Context) =
                INSTANCE ?: ProjectsLocalDataSource(context).apply { INSTANCE = this }
    }

    /**
     * Get all projects from local database
     */
    override fun getProjects(callback: ProjectsDataSource.LoadProjectsCallback) {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(ProjectTable.COLUMN_ID, ProjectTable.COLUMN_USERNAME,
                ProjectTable.COLUMN_CONTENT, ProjectTable.COLUMN_TYPE,
                ProjectTable.COLUMN_DEADLINE, ProjectTable.COLUMN_COMPLETE, ProjectTable.COLUMN_FLAG)

        val cursor = db.query(ProjectTable.TABLE_NAME, projection,
                null, null, null, null, null)

        val projects = ArrayList<Project>()

        with(cursor) {
            while (moveToNext()) {
                val projectId = getInt(getColumnIndexOrThrow(ProjectTable.COLUMN_ID))
                val username = getString(getColumnIndexOrThrow(ProjectTable.COLUMN_USERNAME))
                val content = getString(getColumnIndexOrThrow(ProjectTable.COLUMN_CONTENT))
                val type = when (getInt(getColumnIndexOrThrow(ProjectTable.COLUMN_TYPE))) {
                    1 -> ProjectType.SEQUENCE
                    2 -> ProjectType.SINGLE
                    else -> ProjectType.PARALLEL
                }
                val deadline = getString(getColumnIndexOrThrow(ProjectTable.COLUMN_DEADLINE))
                val complete = getInt(getColumnIndexOrThrow(ProjectTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(ProjectTable.COLUMN_FLAG)) != 0

                actionLocalDataSource.getActionsByProjectId(projectId, object : ActionDataSource.LoadActionsCallback {
                    override fun onActionsLoaded(actions: List<Action>) {
                        val project = Project(projectId, username, content, type, deadline, complete, flag, actions)
                        projects.add(project)
                    }

                    override fun onDataNotAvailable() {
                        val project = Project(projectId, username, content, type, deadline, complete, flag, ArrayList())
                        projects.add(project)
                    }
                })
            }
            close()
        }
        db.close()

        if (projects.isNotEmpty()) {
            callback.onProjectsLoaded(projects)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun getProjectById(id: Int, callback: ProjectsDataSource.GetProjectCallback) {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(ProjectTable.COLUMN_ID, ProjectTable.COLUMN_USERNAME,
                ProjectTable.COLUMN_CONTENT, ProjectTable.COLUMN_TYPE,
                ProjectTable.COLUMN_DEADLINE, ProjectTable.COLUMN_COMPLETE, ProjectTable.COLUMN_FLAG)

        val cursor = db.query(ProjectTable.TABLE_NAME, projection,
                "${ProjectTable.COLUMN_ID} = ?", arrayOf(id.toString()), null, null, null)

        var project: Project? = null

        with(cursor) {
            if (moveToFirst()) {
                val projectId = getInt(getColumnIndexOrThrow(ProjectTable.COLUMN_ID))
                val username = getString(getColumnIndexOrThrow(ProjectTable.COLUMN_USERNAME))
                val content = getString(getColumnIndexOrThrow(ProjectTable.COLUMN_CONTENT))
                val type = when (getInt(getColumnIndexOrThrow(ProjectTable.COLUMN_TYPE))) {
                    1 -> ProjectType.SEQUENCE
                    2 -> ProjectType.SINGLE
                    else -> ProjectType.PARALLEL
                }
                val deadline = getString(getColumnIndexOrThrow(ProjectTable.COLUMN_DEADLINE))
                val complete = getInt(getColumnIndexOrThrow(ProjectTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(ProjectTable.COLUMN_FLAG)) != 0

                actionLocalDataSource.getActionsByProjectId(projectId, object : ActionDataSource.LoadActionsCallback {
                    override fun onActionsLoaded(actions: List<Action>) {
                        project = Project(projectId, username, content, type, deadline, complete, flag, actions)
                    }

                    override fun onDataNotAvailable() {
                        project = Project(projectId, username, content, type, deadline, complete, flag, ArrayList())
                    }
                })
            }
            close()
        }
        db.close()
        project ?. also {
            callback.onProjectLoaded(project!!)
        } ?: callback.onDataNotAvailable()
    }

    override fun addProject(project: Project, callback: ProjectsDataSource.OperationCallback) {
        val type = when (project.type) {
            ProjectType.PARALLEL -> 0
            ProjectType.SEQUENCE -> 1
            ProjectType.SINGLE -> 2
        }
        val values = ContentValues().apply {
            put(ProjectTable.COLUMN_USERNAME, project.username)
            put(ProjectTable.COLUMN_CONTENT, project.content)
            put(ProjectTable.COLUMN_TYPE, type)
            put(ProjectTable.COLUMN_DEADLINE, project.deadline)
            put(ProjectTable.COLUMN_COMPLETE, project.complete)
            put(ProjectTable.COLUMN_FLAG, project.flag)
        }

        with (dbHelper.writableDatabase) {
            if (insert(ProjectTable.TABLE_NAME, null, values) != 0L) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }

    override fun deleteProject(id: Int, callback: ProjectsDataSource.OperationCallback) {
        with (dbHelper.writableDatabase) {
            execSQL(LocalDbHelper.OPEN_FOREIGN_KEYS)
            if (delete(ProjectTable.TABLE_NAME,
                    "${ProjectTable.COLUMN_ID} = ?",
                    arrayOf(id.toString())) != 0) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }

    override fun updateProject(project: Project, callback: ProjectsDataSource.OperationCallback) {
        val type = when (project.type) {
            ProjectType.PARALLEL -> 0
            ProjectType.SEQUENCE -> 1
            ProjectType.SINGLE -> 2
        }
        val values = ContentValues().apply {
            put(ProjectTable.COLUMN_CONTENT, project.content)
            put(ProjectTable.COLUMN_TYPE, type)
            put(ProjectTable.COLUMN_DEADLINE, project.deadline)
            put(ProjectTable.COLUMN_COMPLETE, project.complete)
            put(ProjectTable.COLUMN_FLAG, project.flag)
        }

        with (dbHelper.writableDatabase) {
            if (update(ProjectTable.TABLE_NAME, values,
                    "${ProjectTable.COLUMN_ID} = ?",
                    arrayOf(project.id.toString())) != 0) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }


}