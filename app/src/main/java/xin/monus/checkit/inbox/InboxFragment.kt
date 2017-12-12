package xin.monus.checkit.inbox

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.baoyz.widget.PullRefreshLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.inbox.edit.InboxEditActivity
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import java.util.*
import kotlin.collections.ArrayList

class InboxFragment: Fragment(), InboxContract.View {

    interface ItemClickedListener{
        fun getID(itemID: Int)
        fun itemComplete(itemID: Int)
    }

    override lateinit var presenter: InboxContract.Presenter

    private lateinit var floatingBtn: FloatingActionButton

    lateinit var recycleView: SwipeMenuRecyclerView

    private val listAdapter by lazy {
        InboxListAdapter(context, ArrayList(0), object : InboxFragment.ItemClickedListener {
            override fun getID(itemID: Int) {
                val intent = Intent(context, InboxEditActivity::class.java)
                intent.putExtra("ID", itemID)
                startActivity(intent)
            }

            override fun itemComplete(itemID: Int) {
                presenter.completeButtonListener(itemID)
            }
        })
    }

    private lateinit var pullRefreshLayout: PullRefreshLayout

    private var isTitle = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_inbox_frag, container, false)

        // init the view
        with(root) {
//            testTxt = findViewById(R.id.test_txt)
            recycleView = findViewById(R.id.item_list)

            pullRefreshLayout =findViewById(R.id.pullRefreshLayout)
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

            adapter = listAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(view: RecyclerView, scrollState: Int) {
                    val layoutManager = view.layoutManager as LinearLayoutManager
                    val count = layoutManager.itemCount
                    val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                    when(scrollState) {
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                            if (lastPosition == count - 1) {
                                floatingBtn.hide()
                            } else {
                                floatingBtn.show()
                            }
                        }
                        else -> floatingBtn.show()
                    }
                }
            })


        }



        floatingBtn = activity.findViewById<FloatingActionButton>(R.id.fab)
        floatingBtn.setOnClickListener {
            _ ->
//            Snackbar.make(view, "For test", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
//            presenter.loadItems()
            val intent = Intent(context, InboxEditActivity::class.java)
            startActivity(intent)
        }


        // set up options menu on the top right
        setHasOptionsMenu(true)

        return root
    }


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun showItems(list: List<InboxItem>) {
        listAdapter.list = list as MutableList<InboxItem>
    }

    override fun setEndRefresh() {
                pullRefreshLayout.setRefreshing(false)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.inbox, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {

            true
        } else super.onOptionsItemSelected(item)

    }


    companion object {
        fun newInstance() = InboxFragment()
    }
}