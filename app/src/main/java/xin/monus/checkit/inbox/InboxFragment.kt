package xin.monus.checkit.inbox

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.*
import android.widget.TextView
import xin.monus.checkit.R
import xin.monus.checkit.data.entity.InboxItem

class InboxFragment: Fragment(), InboxContract.View {

    override lateinit var presenter: InboxContract.Presenter

    private lateinit var testTxt: TextView


    private var isTitle = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_main_frag, container, false)

        // init the view
        with(root) {
            testTxt = root.findViewById(R.id.test_txt)

        }


        val fab = activity.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "For test", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            presenter.load()
        }

        // set up options menu on the top right
        setHasOptionsMenu(true)
        return root
    }


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun show(item: InboxItem) {
        if (isTitle) {
            testTxt.text = item.id
            isTitle = false
        } else {
            testTxt.text = item.title
            isTitle = true
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