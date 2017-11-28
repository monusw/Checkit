package xin.monus.checkit.data.source

import xin.monus.checkit.data.entity.Project


interface ProjectsDataSource {

    interface LoadProjectsCallback {

        fun onProjectsLoaded(projects: List<Project>)

        fun onDataNotAvailable()

    }

    interface GetProjectCallback {

        fun onProjectLoaded(project: Project)

        fun onDataNotAvailable()
    }

    interface OperationCallback {

        fun success()

        fun fail()

    }

    fun getProjects(callback: LoadProjectsCallback)

    fun getProjectById(id: Int, callback: GetProjectCallback)

    fun addProject(project: Project, callback: OperationCallback)

    fun deleteProject(id: Int, callback: OperationCallback)

    fun updateProject(project: Project, callback: OperationCallback)

}