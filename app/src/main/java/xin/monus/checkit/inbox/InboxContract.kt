package xin.monus.checkit.inbox

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView
import xin.monus.checkit.data.entity.InboxItem

interface InboxContract {

    interface View : BaseView<Presenter> {
        fun show(item: InboxItem)
    }

    interface Presenter : BasePresenter {
        fun load()
    }
}