package xin.monus.checkit.projects

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.baoyz.widget.PullRefreshLayout
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import xin.monus.checkit.R
import xin.monus.checkit.base.BaseAdapter
import xin.monus.checkit.data.entity.Project
import xin.monus.checkit.projects.actions.ActionsActivity
import xin.monus.checkit.projects.edit.ProjectEditActivity

class ProjectsFragment: Fragment(), ProjectsContract.View {

    override lateinit var presenter: ProjectsContract.Presenter

    lateinit var pullRefresh: PullRefreshLayout
    lateinit var recyclerView: SwipeMenuRecyclerView
    lateinit var floatingBtn: FloatingActionButton

    private val projectsAdapter  by lazy {ProjectsAdapter(context, ArrayList(0), itemClickListener) }

    private val itemClickListener = object : ItemClickListener {
        override fun onClickDelete(projectId: Int) {
            println("delete project id: $projectId")
        }

        override fun onClickEdit(projectId: Int) {
            println("edit project id: $projectId")
        }

        override fun onClickProject(projectId: Int) {
            println("click project id: $projectId")
            val intent = Intent(context, ActionsActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId.toString())
            startActivity(intent)
        }

    }

    interface ItemClickListener {
        fun onClickDelete(projectId: Int)
        fun onClickEdit(projectId: Int)
        fun onClickProject(projectId: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_projects_frag, container, false)

        with(root) {
            recyclerView = findViewById(R.id.projects_list)

            pullRefresh = findViewById(R.id.projects_pull_refresh)
        }

        pullRefresh.setOnRefreshListener {
            pullRefresh.setRefreshing(false)
        }

        floatingBtn = activity.findViewById(R.id.fab)

        floatingBtn.setOnClickListener {
            onClickAddBtn()
        }


        setupRecyclerView()

        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun showProjects(projects: List<Project>) {
        projectsAdapter.projects = projects
    }

    private fun onClickAddBtn() {
        val intent = Intent(activity, ProjectEditActivity::class.java)
        startActivity(intent)
    }

    /**
     * Set up the recycler view
     */
    private fun setupRecyclerView() {
        val swipeBtnHeight = ViewGroup.LayoutParams.MATCH_PARENT
        val swipeBtnWidth = 200
        val swipeBtnTextSize = 16
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            // set up right swipe menu
            setSwipeMenuCreator { _, swipeRightMenu, _ ->
                val deleteItem = SwipeMenuItem(context)
                        .setText(R.string.projects_swipe_delete)
                        .setTextColor(Color.WHITE)
                        .setTextSize(swipeBtnTextSize)
                        .setBackgroundColor(Color.RED)
                        .setWidth(swipeBtnWidth)
                        .setHeight(swipeBtnHeight)
                val editItem = SwipeMenuItem(context)
                        .setText(R.string.projects_swipe_edit)
                        .setTextColor(Color.WHITE)
                        .setTextSize(swipeBtnTextSize)
                        .setBackgroundColor(Color.GRAY)
                        .setWidth(swipeBtnWidth)
                        .setHeight(swipeBtnHeight)

                swipeRightMenu.addMenuItem(editItem)
                swipeRightMenu.addMenuItem(deleteItem)
            }

            setSwipeMenuItemClickListener {menuBridge: SwipeMenuBridge ->
                menuBridge.closeMenu()
                val adapterPosition = menuBridge.adapterPosition
                val menuPosition = menuBridge.position
                println("click menu, position: $menuPosition")
                when (menuPosition) {
                    0 -> {
                        val projectId = projectsAdapter.projects[adapterPosition].id
                        println(projectId)
                        itemClickListener.onClickEdit(projectId)
                    }
                    1 -> {
                        val projectId = projectsAdapter.projects[adapterPosition].id
                        itemClickListener.onClickDelete(projectId)
                    }
                }
            }

            adapter = projectsAdapter
        }
    }

    class ProjectsAdapter(context: Context, projects: List<Project>, val itemClickListener: ItemClickListener) :
            BaseAdapter<ProjectsAdapter.ViewHolder>(context) {

        var projects: List<Project> = projects
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.contentTxt.text = projects[position].content

            // 显示项目进度的list view
            val listView = holder.checkStateView
            val actionList = projects[position].actionList
            listView.removeAllViews()
            for (action in actionList) {
                val item = inflater.inflate(R.layout.img_check_state_item, listView, false)
                val img = item.findViewById<ImageView>(R.id.image_view)
                if (action.complete) {
                    img.setImageResource(R.drawable.img_did_check)
                } else {
                    img.setImageResource(R.drawable.img_to_check)
                }
                listView.addView(item)
            }

            // Item 点击事件
            holder.itemView.setOnClickListener {
                itemClickListener.onClickProject(projects[position].id)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.activity_projects_frag_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = projects.size


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val contentTxt: TextView = itemView.findViewById(R.id.content)
            val checkStateView: LinearLayout = itemView.findViewById(R.id.check_state_view)
        }
    }

    companion object {
        fun newInstance() = ProjectsFragment()
    }

}