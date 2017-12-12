package xin.monus.checkit.projects

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView
import xin.monus.checkit.data.entity.Project


interface ProjectsContract {

    interface View : BaseView<Presenter> {
        fun showProjects(projects: List<Project>)
    }

    interface Presenter : BasePresenter {
        fun deleteProject(projectId: Int)
    }

}