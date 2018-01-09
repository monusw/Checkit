package xin.monus.checkit.daily

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView
import xin.monus.checkit.data.entity.Daily

interface DailyContract {

    interface View : BaseView<Presenter> {
        fun showItems(list: List<Daily>)
        fun setEndRefresh()
    }

    interface Presenter : BasePresenter {
        fun loadItems()
        fun completeButtonListener(itemID: Int)
        fun deleteItem(itemID: Int)
    }
}