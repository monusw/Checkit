package xin.monus.checkit.data.source

import xin.monus.checkit.data.entity.Action


interface ActionDataSource {

    interface LoadActionsCallback {
        fun onActionsLoaded(actions: List<Action>)
        fun onDataNotAvailable()
    }

    interface GetActionCallback {
        fun onActionLoaded(action: Action)
        fun onDataNotAvailable()
    }

    interface OperationCallback {
        fun success()
        fun fail()
    }

    fun getActionsByProjectId(projectId: Int, callback: LoadActionsCallback)

    fun getSubActionsById(actionId: Int, callback: LoadActionsCallback)

    fun getActionById(actionId: Int, callback: GetActionCallback)

    fun addAction(projectId: Int, action: Action, callback: OperationCallback)

    fun addSubAction(parentActionId: Int, action: Action, callback: OperationCallback)

    fun deleteAction(actionId: Int, callback: OperationCallback)

    fun updateAction(action: Action, callback: OperationCallback)
}