package xin.monus.checkit.inbox

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ListView
import android.widget.TextView
import com.baoyz.widget.PullRefreshLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem
import java.util.*

class InboxFragment: Fragment(), InboxContract.View {

    override lateinit var presenter: InboxContract.Presenter

    private lateinit var testTxt: TextView

    private val listAdapter = InboxListAdapter(ArrayList(0))

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
            }
            pullRefreshLayout =findViewById(R.id.pullRefreshLayout)
//            pullRefreshLayout.setColorSchemeColors(
//                    ContextCompat.getColor(context, android.R.color.holo_blue_light),
//                    ContextCompat.getColor(context, android.R.color.holo_green_light),
//                    ContextCompat.getColor(context, android.R.color.holo_orange_light),
//                    ContextCompat.getColor(context, android.R.color.holo_red_light)
//            )
            pullRefreshLayout.setOnRefreshListener {
                pullRefreshLayout.setRefreshing(true)
                //doAsync { Thread.sleep(3000) }
                presenter.loadItems()
            }
        }



        val fab = activity.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            view ->
            Snackbar.make(view, "For test", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            presenter.loadItems()
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