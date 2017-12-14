package xin.monus.checkit.projects.actions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.util.Injection
import xin.monus.checkit.util.setupActionBar

class ActionsActivity : AppCompatActivity(), ActionsContract.View {

    override lateinit var presenter: ActionsContract.Presenter

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

        val projectId = intent.getStringExtra("PROJECT_ID").toInt()

        presenter = ActionsPresenter(Injection.getActionRepository(this), this, projectId)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private val actionsAdapter by lazy { ActionsAdapter(this, ArrayList(0)) }

    lateinit var recyclerView: SwipeMenuRecyclerView

    private fun initView() {
        recyclerView = findViewById(R.id.actions_list) as SwipeMenuRecyclerView

        with(recyclerView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = actionsAdapter
        }
    }

    override fun showActions(actions: List<Action>) {
        println(actions.size)
        val list = ArrayList<Action>()
        actions.filterNotTo(list) { it.complete }
        actionsAdapter.actionList = list
    }
}
