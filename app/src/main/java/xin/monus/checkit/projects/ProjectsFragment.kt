package xin.monus.checkit.projects

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.baoyz.widget.PullRefreshLayout
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import xin.monus.checkit.R
import xin.monus.checkit.base.BaseAdapter
import xin.monus.checkit.data.entity.Project

class ProjectsFragment: Fragment(), ProjectsContract.View {

    override lateinit var presenter: ProjectsContract.Presenter

    lateinit var pullRefresh: PullRefreshLayout

    lateinit var recyclerView: SwipeMenuRecyclerView

    private val projectsAdapter  by lazy {ProjectsAdapter(context, ArrayList(0)) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_projects_frag, container, false)

        with(root) {
            recyclerView = findViewById(R.id.projects_list)

            pullRefresh = findViewById(R.id.projects_pull_refresh)
        }

        // set up the recycler view
        val swipeBtnHeight = ViewGroup.LayoutParams.MATCH_PARENT
        val swipeBtnWidth = 200
        val swipeBtnTextSize = 16
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setSwipeMenuCreator { _, swipeRightMenu, _ ->
                val deleteItem = SwipeMenuItem(context)
                        .setText(R.string.projects_swipe_delete)
                        .setTextColor(Color.WHITE)
                        .setTextSize(swipeBtnTextSize)
                        .setBackgroundColor(Color.RED)
                        .setWidth(swipeBtnWidth)
                        .setHeight(swipeBtnHeight)
                swipeRightMenu.addMenuItem(deleteItem)
            }

            adapter = projectsAdapter
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        println("projects fragment resume")
        presenter.start()
    }

    override fun showProjects(projects: List<Project>) {
        println(projects.size)
        projectsAdapter.projects = projects
    }


    class ProjectsAdapter(context: Context, projects: List<Project>) :
            BaseAdapter<ProjectsAdapter.ViewHolder>(context) {

        var projects: List<Project> = projects
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.contentTxt.text = projects[position].content
            holder.deadlineTxt.text = projects[position].deadline
            holder.completeBtn.setOnClickListener {
                println("projects complete btn pressed")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.activity_projects_frag_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = projects.size


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val contentTxt: TextView = itemView.findViewById<TextView>(R.id.content)
            val deadlineTxt: TextView = itemView.findViewById<TextView>(R.id.deadline)
            val completeBtn: ImageButton = itemView.findViewById<ImageButton>(R.id.complete)
        }
    }

    companion object {
        fun newInstance() = ProjectsFragment()
    }

}