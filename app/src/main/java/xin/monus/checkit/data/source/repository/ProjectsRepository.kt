package xin.monus.checkit.data.source.repository

import xin.monus.checkit.data.entity.Project
import xin.monus.checkit.data.source.ProjectsDataSource

class ProjectsRepository (
        private val projectsLocalDataSource: ProjectsDataSource
) : ProjectsDataSource {

    /**
     * Singleton
     */
    companion object {
        private var INSTANCE: ProjectsRepository? = null

        @JvmStatic fun getInstance(projectsLocalDataSource: ProjectsDataSource) : ProjectsRepository {
            return INSTANCE ?: ProjectsRepository(projectsLocalDataSource).apply {
                INSTANCE = this
            }
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }


    override fun getProjects(callback: ProjectsDataSource.LoadProjectsCallback) {
        projectsLocalDataSource.getProjects(object : ProjectsDataSource.LoadProjectsCallback {
            override fun onProjectsLoaded(projects: List<Project>) {
                callback.onProjectsLoaded(projects)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getProjectById(id: Int, callback: ProjectsDataSource.GetProjectCallback) {
        projectsLocalDataSource.getProjectById(id, object : ProjectsDataSource.GetProjectCallback {
            override fun onProjectLoaded(project: Project) {
                callback.onProjectLoaded(project)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun addProject(project: Project, callback: ProjectsDataSource.OperationCallback) {
        projectsLocalDataSource.addProject(project, object : ProjectsDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun deleteProject(id: Int, callback: ProjectsDataSource.OperationCallback) {
        projectsLocalDataSource.deleteProject(id, object : ProjectsDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun updateProject(project: Project, callback: ProjectsDataSource.OperationCallback) {
        projectsLocalDataSource.updateProject(project, object : ProjectsDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }


}