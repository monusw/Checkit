package xin.monus.checkit.projects.actions

import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.data.source.ActionDataSource


class ActionsPresenter(
        val actionRepository: ActionDataSource,
        val actionsView: ActionsContract.View,
        val projectId: Int
) : ActionsContract.Presenter {

    init {
        actionsView.presenter = this
    }

    override fun start() {
        println("action view start")
        actionRepository.getActionsByProjectId(projectId, object : ActionDataSource.LoadActionsCallback {
            override fun onActionsLoaded(actions: List<Action>) {
                for (action in actions) {
                    println(action.content)
                }
            }

            override fun onDataNotAvailable() {
                println("get data failure")
            }

        })
    }
}