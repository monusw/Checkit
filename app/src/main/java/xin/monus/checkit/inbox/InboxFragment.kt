package xin.monus.checkit.inbox

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
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
import java.util.*

class InboxFragment: Fragment(), InboxContract.View {

    interface ItemClickedListener{
        fun getID(itemID: Int)
    }

    override lateinit var presenter: InboxContract.Presenter

    private lateinit var floatingBtn: FloatingActionButton

    private val listAdapter = InboxListAdapter(ArrayList(0), object : InboxFragment.ItemClickedListener{
        override fun getID(itemID: Int) {
            val intent = Intent(context, InboxEditActivity::class.java)
            intent.putExtra("ID",itemID)
            startActivity(intent)
        }
    })

    private lateinit var pullRefreshLayout: PullRefreshLayout

    private var isTitle = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_inbox_frag, container, false)

        // init the view
        with(root) {
//            testTxt = findViewById(R.id.test_txt)
            findViewById<ListView>(R.id.item_list).apply {
                adapter = listAdapter

                setOnScrollListener(object : AbsListView.OnScrollListener {
                    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                    }

                    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                        when(scrollState) {
                            AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> {
                                if (lastVisiblePosition == count - 1) {
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
            pullRefreshLayout =findViewById(R.id.pullRefreshLayout)
            pullRefreshLayout.setOnRefreshListener {
                pullRefreshLayout.setRefreshing(true)
                presenter.loadItems()
            }
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
        listAdapter.list = list
    }

    override fun setEndRefresh() {
        doAsync {
            Thread.sleep(5000)
            uiThread {
                pullRefreshLayout.setRefreshing(false)
            }
        }
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