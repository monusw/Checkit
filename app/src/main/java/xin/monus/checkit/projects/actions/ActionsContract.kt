package xin.monus.checkit.projects.actions

import xin.monus.checkit.base.BasePresenter
import xin.monus.checkit.base.BaseView

interface ActionsContract {
    interface View: BaseView<Presenter> {

    }

    interface Presenter: BasePresenter {

    }

}