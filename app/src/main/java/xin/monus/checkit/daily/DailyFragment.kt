package xin.monus.checkit.daily

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.baoyz.widget.PullRefreshLayout
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import xin.monus.checkit.R
import xin.monus.checkit.daily.dailyEdit.DailyEditActivity
import xin.monus.checkit.data.entity.Daily


/**
 * @author wu
 * @date   2017/12/6
 */
class DailyFragment : Fragment(), DailyContract.View{

    interface ItemClickedListener{
        fun getID(itemID: Int)
        fun itemComplete(itemID: Int)
        fun itemDelete(itemID: Int)
    }

    override lateinit var presenter: DailyContract.Presenter

    lateinit var recycleView: SwipeMenuRecyclerView

    private val itemClickListener = object : DailyFragment.ItemClickedListener{
        override fun getID(itemID: Int) {
            val intent = Intent(context, DailyEditActivity::class.java)
            println(itemID)
            intent.putExtra("ID", itemID)
            startActivity(intent)
        }

        override fun itemComplete(itemID: Int) {
            presenter.completeButtonListener(itemID)
        }

        override fun itemDelete(itemID: Int) {
            println("delete project id: $itemID")

            presenter.deleteItem(itemID)
        }
    }

    private val listAdapter by lazy { DailyListAdapter(context, ArrayList(0), itemClickListener) }

    private lateinit var pullRefreshLayout: PullRefreshLayout

    private var isTitle = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_daily_frag, container, false)

        with(root) {
            recycleView = findViewById(R.id.daily_item_list)
            pullRefreshLayout =findViewById(R.id.dailyPullRefreshLayout)
            pullRefreshLayout.setOnRefreshListener {
                pullRefreshLayout.setRefreshing(true)
                presenter.loadItems()
            }

        }


        val swipeBtnHeight = ViewGroup.LayoutParams.MATCH_PARENT
        val swipeBtnWidth = 200
        val swipeBtnTextSize = 20
        with(recycleView) {
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

            setSwipeMenuItemClickListener {menuBridge: SwipeMenuBridge ->
                menuBridge.closeMenu()
                val adapterPosition = menuBridge.adapterPosition
                val menuPosition = menuBridge.position
                println("click menu, position: $menuPosition")
                when (menuPosition) {
                    0 -> {
                        val projectId = listAdapter.list[adapterPosition].id
                        itemClickListener.itemDelete(projectId)
                    }
                }
            }

            adapter = listAdapter
        }


        setHasOptionsMenu(true)

        return root
    }

    override fun onResume() {
        super.onResume()
        println("daily fragment resume")
        presenter.start()
    }

    override fun showItems(list: List<Daily>) {
        listAdapter.list = list
    }

    override fun setEndRefresh() {
        pullRefreshLayout.setRefreshing(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.daily, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("do nothing")
        return true
    }

    companion object {
        fun newInstance() = DailyFragment()
    }
}