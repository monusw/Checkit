package xin.monus.checkit.projects.actions

import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.data.source.ActionDataSource


class ActionsPresenter(
        private val actionRepository: ActionDataSource,
        val actionsView: ActionsContract.View,
        val projectId: Int
) : ActionsContract.Presenter {

    init {
        actionsView.presenter = this
    }

    private val actionList = ArrayList<Action>(0)

    override fun start() {
        println("action view start")
        actionRepository.getActionsByProjectId(projectId, object : ActionDataSource.LoadActionsCallback {
            override fun onActionsLoaded(actions: List<Action>) {
                actionList.addAll(actions)
                actionsView.showActions(actions)
            }
            override fun onDataNotAvailable() {
                actionsView.showActions(ArrayList(0))
                println("no actions in project $projectId")
            }

        })
    }

    override fun completeAction(actionId: Int) {
        for (action in actionList) {
            if (action.id == actionId) {
                action.complete = true
                actionRepository.updateAction(action, object : ActionDataSource.OperationCallback {
                    override fun success() {
                        actionsView.showActions(actionList)
                    }

                    override fun fail() {
                        action.complete = false
                        actionsView.showActions(actionList)
                    }
                })
                return
            }
        }
    }

    override fun deleteAction(actionId: Int) {
        actionRepository.deleteAction(actionId, object : ActionDataSource.OperationCallback {
            override fun success() {
                for (action in actionList) {
                    if (action.id == actionId) {
                        actionList.remove(action)
                        break
                    }
                }
                actionsView.showActions(actionList)
            }

            override fun fail() {
                actionsView.showActions(actionList)
            }

        })
    }
}