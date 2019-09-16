package xin.monus.checkit.projects.actions

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.projects.ProjectsActivity
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.setupActionBar

class ActionsActivity : AppCompatActivity(), ActionsContract.View {

    override lateinit var presenter: ActionsContract.Presenter

    var projectId = 0
    lateinit var btnAdd: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actions)

        setupActionBar(R.id.toolbar) {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            // add
            setTitle(R.string.actions_activity_title)
        }

        initView()

        projectId = intent.getStringExtra("PROJECT_ID")!!.toInt()

        presenter = ActionsPresenter(Injection.getActionRepository(this), this, projectId)
    }

    private fun initView() {
        recyclerView = findViewById(R.id.actions_list)
        // 点击添加按钮
        btnAdd = findViewById(R.id.btn_add)
        btnAdd.setOnClickListener {
            val intent = Intent(this, ActionEditActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId)
            startActivity(intent)
        }

        val swipeBtnHeight = ViewGroup.LayoutParams.MATCH_PARENT
        val swipeBtnWidth = 200
//        val swipeBtnTextSize = 20

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            setSwipeMenuCreator { _, swipeRightMenu, _ ->
                val deleteItem = SwipeMenuItem(context)
                        .setText(R.string.projects_swipe_delete)
                        .setTextColor(Color.WHITE)
                        .setBackgroundColor(Color.RED)
                        .setWidth(swipeBtnWidth)
                        .setHeight(swipeBtnHeight)
                swipeRightMenu.addMenuItem(deleteItem)
            }

            setSwipeMenuItemClickListener { menuBridge, position ->
                menuBridge.closeMenu()
                val menuPosition = menuBridge.position
                println("click delete action, position: $menuPosition")
                when (menuPosition) {
                    0 -> {
                        val actionId = actionsAdapter.actionList[position].id
                        itemClickListener.itemDelete(actionId)
                    }
                }
            }

            adapter = actionsAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                    val layoutManager = view.layoutManager as LinearLayoutManager
                    val count = layoutManager.itemCount
                    val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                    when(scrollState) {
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                            if (lastPosition == count - 1) {
                                btnAdd.hide()
                            }
                            else {
                                btnAdd.show()
                            }
                        }
                        else -> btnAdd.show()
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    interface ItemClickListener {
        fun itemClick(actionId: Int)
        fun itemComplete(actionId: Int)
        fun itemDelete(actionId: Int)
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, ProjectsActivity::class.java)
        startActivity(intent)
        finish()
//        onBackPressed()
        return true
    }

    private val actionsAdapter by lazy { ActionsAdapter(this, ArrayList(0), itemClickListener) }

    private val itemClickListener = object : ItemClickListener {
        override fun itemClick(actionId: Int) {
            println("click actionId $actionId")
            val intent = Intent(this@ActionsActivity, ActionEditActivity::class.java)
            intent.putExtra("PROJECT_ID", projectId)
            intent.putExtra("ACTION_ID", actionId)
            startActivity(intent)
        }
        override fun itemComplete(actionId: Int) {
            println("complete actionId $actionId")
            presenter.completeAction(actionId)
        }

        override fun itemDelete(actionId: Int) {
            println("delete action $actionId")
            presenter.deleteAction(actionId)
        }
    }

    private lateinit var recyclerView: SwipeMenuRecyclerView

    override fun showActions(actions: List<Action>) {
        println(actions.size)
        val list = ArrayList<Action>()
        actions.filterNotTo(list) { it.complete }
        actionsAdapter.actionList = list
    }
}
