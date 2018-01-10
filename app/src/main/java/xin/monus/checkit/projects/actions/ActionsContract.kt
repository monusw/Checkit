package xin.monus.checkit.projects.actions

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView
import xin.monus.checkit.data.entity.Action

interface ActionsContract {
    interface View: BaseView<Presenter> {
        fun showActions(actions: List<Action>)
    }

    interface Presenter: BasePresenter {
        fun completeAction(actionId: Int)
        fun deleteAction(actionId: Int)
    }

}