package xin.monus.checkit.daily

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView

interface DailyContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {

    }
}