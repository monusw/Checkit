package xin.monus.checkit.daily

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xin.monus.checkit.R


/**
 * @author wu
 * @date   2017/12/6
 */
class DailyFragment : Fragment(), DailyContract.View{
    override lateinit var presenter: DailyContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_daily_frag, container, false)

        with(root) {

        }

        return root
    }

    override fun onResume() {
        super.onResume()
        println("daily fragment resume")
        presenter.start()
    }

    companion object {
        fun newInstance() = DailyFragment()
    }
}