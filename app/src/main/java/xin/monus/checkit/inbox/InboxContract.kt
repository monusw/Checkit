package xin.monus.checkit.inbox

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView
import xin.monus.checkit.data.entity.InboxItem

interface InboxContract {

    interface View : BaseView<Presenter> {
        fun showItems(list: List<InboxItem>)
        fun setStartRefresh()
        fun setEndRefresh()
    }

    interface Presenter : BasePresenter {
        fun loadItems()
        fun completeButtonListener(itemID: Int)
        fun deleteItem(itemID: Int)
        fun deleteAll()
        fun deleteFinished()
    }
}