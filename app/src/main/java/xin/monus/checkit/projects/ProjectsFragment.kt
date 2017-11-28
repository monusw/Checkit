package xin.monus.checkit.projects

import android.support.v4.app.Fragment

class ProjectsFragment: Fragment(), ProjectsContract.View {

    override lateinit var presenter: ProjectsContract.Presenter

}