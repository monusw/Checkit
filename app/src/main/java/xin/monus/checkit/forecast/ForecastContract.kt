package xin.monus.checkit.forecast

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView
import xin.monus.checkit.data.entity.Forecast

interface ForecastContract {

    interface View : BaseView<Presenter> {
        fun initData(forecastList: List<Forecast>)
        fun showElement(index: Int)
    }

    interface Presenter : BasePresenter {

    }
}