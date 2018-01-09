package xin.monus.checkit.projects

import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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
        doAsync {
            projectsRepository.getProjects(object: ProjectsDataSource.LoadProjectsCallback {
                override fun onProjectsLoaded(projects: List<Project>) {
                    uiThread {
                        projectsView.showProjects(projects)
                    }
                }

                override fun onDataNotAvailable() {
                    Log.w("projects", "no data")
                }

            })
        }
    }

    override fun deleteProject(projectId: Int) {
        projectsRepository.deleteProject(projectId, object : ProjectsDataSource.OperationCallback {
            override fun success() {
                reloadProjects()
            }

            override fun fail() {
                Log.w("projects", "delete fail")
            }
        })
    }


    private fun reloadProjects() {
        println("Reload view")
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