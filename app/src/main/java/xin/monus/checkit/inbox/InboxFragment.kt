package xin.monus.checkit.inbox

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import android.widget.ListView
import android.widget.TextView
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource

class InboxFragment: Fragment(), InboxContract.View {

    override lateinit var presenter: InboxContract.Presenter

    private lateinit var testTxt: TextView

    private val listAdapter = InboxListAdapter(ArrayList(0))

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
            swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(context, android.R.color.holo_blue_light),
                    ContextCompat.getColor(context, android.R.color.holo_green_light),
                    ContextCompat.getColor(context, android.R.color.holo_orange_light),
                    ContextCompat.getColor(context, android.R.color.holo_red_light)
            )
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
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
        swipeRefreshLayout.isRefreshing = false
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