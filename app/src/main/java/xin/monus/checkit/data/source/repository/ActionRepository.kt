package xin.monus.checkit.data.source.repository

import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.data.source.ActionDataSource

class ActionRepository(
        //todo add remote
        private val actionLocalDataSource: ActionDataSource
) : ActionDataSource {

    /**
     * Singleton
     */
    companion object {
        private var INSTANCE: ActionRepository? = null

        @JvmStatic fun getInstance(actionLocalDataSource: ActionDataSource) : ActionRepository {
            return INSTANCE ?: ActionRepository(actionLocalDataSource).apply {
                INSTANCE = this
            }
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getActionsByProjectId(projectId: Int, callback: ActionDataSource.LoadActionsCallback) {
        actionLocalDataSource.getActionsByProjectId(projectId, object : ActionDataSource.LoadActionsCallback {
            override fun onActionsLoaded(actions: List<Action>) {
                callback.onActionsLoaded(actions)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }

    override fun getSubActionsById(actionId: Int, callback: ActionDataSource.LoadActionsCallback) {
        actionLocalDataSource.getSubActionsById(actionId, object : ActionDataSource.LoadActionsCallback {
            override fun onActionsLoaded(actions: List<Action>) {
                callback.onActionsLoaded(actions)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }

    override fun getActionById(actionId: Int, callback: ActionDataSource.GetActionCallback) {
        actionLocalDataSource.getActionById(actionId, object : ActionDataSource.GetActionCallback {
            override fun onActionLoaded(action: Action) {
                callback.onActionLoaded(action)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun addAction(projectId: Int, action: Action, callback: ActionDataSource.OperationCallback) {
        actionLocalDataSource.addAction(projectId, action, object : ActionDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun addSubAction(parentActionId: Int, action: Action, callback: ActionDataSource.OperationCallback) {
        actionLocalDataSource.addSubAction(parentActionId, action, object : ActionDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun deleteAction(actionId: Int, callback: ActionDataSource.OperationCallback) {
        actionLocalDataSource.deleteAction(actionId, object : ActionDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun updateAction(action: Action, callback: ActionDataSource.OperationCallback) {
        actionLocalDataSource.updateAction(action, object : ActionDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

}