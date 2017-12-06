package xin.monus.checkit.forecast

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView

interface ForecastContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {

    }
}