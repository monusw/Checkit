package xin.monus.checkit.forecast

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xin.monus.checkit.R

class ForecastFragment : Fragment(), ForecastContract.View {
    override lateinit var presenter: ForecastContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_forecast_frag, container, false)

        with(root) {

        }
        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    companion object {
        fun newInstance() = ForecastFragment()
    }

}