package xin.monus.checkit.projects

import android.util.Log
import xin.monus.checkit.data.entity.Project
import xin.monus.checkit.data.source.ProjectsDataSource
import xin.monus.checkit.data.source.repository.ProjectsRepository

class ProjectsPresenter(
        val projectsRepository: ProjectsRepository,
        val projectsView: ProjectsContract.View
) : ProjectsContract.Presenter {

    init {
        projectsView.presenter = this
    }

    override fun start() {
        println("projects fragment start")
        projectsRepository.getProjects(object: ProjectsDataSource.LoadProjectsCallback {
            override fun onProjectsLoaded(projects: List<Project>) {
                projectsView.showProjects(projects)
            }

            override fun onDataNotAvailable() {
                Log.w("projects", "no data")
            }

        })
    }

}