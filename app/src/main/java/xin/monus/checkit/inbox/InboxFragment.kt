package xin.monus.checkit.inbox

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baoyz.widget.PullRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.inbox.edit.InboxEditActivity

class InboxFragment: Fragment(), InboxContract.View {

    interface ItemClickedListener{
        fun getID(itemID: Int)
        fun itemComplete(itemID: Int)
        fun itemDelete(itemID: Int)
    }

    override lateinit var presenter: InboxContract.Presenter

    private lateinit var floatingBtn: FloatingActionButton

    private lateinit var recycleView: SwipeMenuRecyclerView

    private val itemClickListener = object : ItemClickedListener{
        override fun getID(itemID: Int) {
            val intent = Intent(context, InboxEditActivity::class.java)
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

    private val listAdapter by lazy { InboxListAdapter(requireContext(), ArrayList(0), itemClickListener) }

    private lateinit var pullRefreshLayout: PullRefreshLayout

//    private var isTitle = false

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

            setSwipeMenuItemClickListener { menuBridge, position ->
                menuBridge.closeMenu()
                val menuPosition = menuBridge.position
                println("click menu, position: $menuPosition")
                when (menuPosition) {
                    0 -> {
                        val projectId = listAdapter.list[position].id
                        itemClickListener.itemDelete(projectId)
                    }
                }
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
                            }
                            else {
                                floatingBtn.show()
                            }
                        }
                        else -> floatingBtn.show()
                    }
                }
            })


        }


        floatingBtn = activity!!.findViewById(R.id.fab)
        floatingBtn.setOnClickListener {
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
        listAdapter.list = list
    }

    override fun setEndRefresh() {
        pullRefreshLayout.setRefreshing(false)
    }

    override fun setStartRefresh() {
        pullRefreshLayout.setRefreshing(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.inbox, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all -> {
                context!!.alert(R.string.delete_confirm) {
                    yesButton {
                        presenter.deleteAll()
                    }
                    noButton {
                        println("do nothing")
                    }
                }.show()

                true
            }
            R.id.delete_finished -> {
                context!!.alert(R.string.delete_confirm) {
                    yesButton {
                        presenter.deleteFinished()
                    }
                    noButton {
                        println("do nothing")
                    }
                }.show()


                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }


    companion object {
        fun newInstance() = InboxFragment()
    }
}