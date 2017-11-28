package xin.monus.checkit.data.source.local

import android.content.ContentValues
import android.content.Context
import xin.monus.checkit.data.entity.Action
import xin.monus.checkit.data.source.ActionDataSource
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.db.LocalTable.ActionTable


class ActionLocalDataSource private constructor(context: Context) : ActionDataSource {

    private val dbHelper: LocalDbHelper = LocalDbHelper(context)
    companion object {
        private var INSTANCE: ActionDataSource? = null
        @JvmStatic fun getInstance(context: Context) =
                INSTANCE ?: ActionLocalDataSource(context).apply { INSTANCE = this }
    }

    override fun getActionsByProjectId(projectId: Int, callback: ActionDataSource.LoadActionsCallback) {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(ActionTable.COLUMN_ID, ActionTable.COLUMN_PROJECT_ID,
                ActionTable.COLUMN_PARENT_ACTION_ID, ActionTable.COLUMN_CONTENT,
                ActionTable.COLUMN_DEADLINE, ActionTable.COLUMN_COMPLETE, ActionTable.COLUMN_FLAG)

        val cursor = db.query(ActionTable.TABLE_NAME, projection,
                "${ActionTable.COLUMN_PROJECT_ID} = ? AND ${ActionTable.COLUMN_PARENT_ACTION_ID} = ? ",
                arrayOf(projectId.toString(), "0"), null, null, null)

        val actions = ArrayList<Action>()
        with(cursor) {
            while (moveToNext()) {
                val actionId = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_ID))
                val id = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_PROJECT_ID))
                val content = getString(getColumnIndexOrThrow(ActionTable.COLUMN_CONTENT))
                val deadline = getString(getColumnIndexOrThrow(ActionTable.COLUMN_DEADLINE))
                val complete = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_FLAG)) != 0

                val item = Action(actionId, id, content, deadline, complete, flag, ArrayList(0))

                getSubActionsById(actionId, object : ActionDataSource.LoadActionsCallback {
                    override fun onActionsLoaded(actions: List<Action>) {
                        item.subActionList = actions
                    }

                    override fun onDataNotAvailable() {
                        println("action id:$actionId has no sub actions")
                    }

                })

                actions.add(item)
            }
            close()
        }
        db.close()
        if (actions.isNotEmpty()) {
            callback.onActionsLoaded(actions)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun getSubActionsById(actionId: Int, callback: ActionDataSource.LoadActionsCallback) {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(ActionTable.COLUMN_ID, ActionTable.COLUMN_PROJECT_ID, ActionTable.COLUMN_CONTENT,
                ActionTable.COLUMN_DEADLINE, ActionTable.COLUMN_COMPLETE, ActionTable.COLUMN_FLAG)

        val cursor = db.query(ActionTable.TABLE_NAME, projection,
                "${ActionTable.COLUMN_PARENT_ACTION_ID} = ?", arrayOf(actionId.toString()), null, null, null)
        val actions = ArrayList<Action>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_ID))
                val projectId = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_PROJECT_ID))
                val content = getString(getColumnIndexOrThrow(ActionTable.COLUMN_CONTENT))
                val deadline = getString(getColumnIndexOrThrow(ActionTable.COLUMN_DEADLINE))
                val complete = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_FLAG)) != 0
                val item = Action(id, projectId, content, deadline, complete, flag, ArrayList(0))
                actions.add(item)
            }
            close()
        }
        db.close()

        if (actions.isNotEmpty()) {
            callback.onActionsLoaded(actions)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun getActionById(actionId: Int, callback: ActionDataSource.GetActionCallback) {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(ActionTable.COLUMN_ID, ActionTable.COLUMN_PROJECT_ID, ActionTable.COLUMN_CONTENT,
                ActionTable.COLUMN_DEADLINE, ActionTable.COLUMN_COMPLETE, ActionTable.COLUMN_FLAG)

        val cursor = db.query(ActionTable.TABLE_NAME, projection,
                "${ActionTable.COLUMN_ID} = ?", arrayOf(actionId.toString()), null, null, null)
        var item: Action? = null
        with(cursor) {
            if (moveToFirst()) {
                val id = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_ID))
                val projectId = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_PROJECT_ID))
                val content = getString(getColumnIndexOrThrow(ActionTable.COLUMN_CONTENT))
                val deadline = getString(getColumnIndexOrThrow(ActionTable.COLUMN_DEADLINE))
                val complete = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(ActionTable.COLUMN_FLAG)) != 0
                item = Action(id, projectId, content, deadline, complete, flag, ArrayList(0))
                getSubActionsById(actionId, object : ActionDataSource.LoadActionsCallback {
                    override fun onActionsLoaded(actions: List<Action>) {
                        item?.subActionList = actions
                    }

                    override fun onDataNotAvailable() {
                        println("action id:$actionId has no sub actions")
                    }

                })
            }
            close()
        }
        db.close()
        item?.also {
            callback.onActionLoaded(item!!)
        } ?: callback.onDataNotAvailable()
    }

    override fun addAction(projectId: Int, action: Action, callback: ActionDataSource.OperationCallback) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ActionTable.COLUMN_PROJECT_ID, projectId)
            put(ActionTable.COLUMN_CONTENT, action.content)
            put(ActionTable.COLUMN_DEADLINE, action.deadline)
            put(ActionTable.COLUMN_COMPLETE, action.complete)
            put(ActionTable.COLUMN_FLAG, action.flag)
        }

        if (db.insert(ActionTable.TABLE_NAME, null, values) != 0L) {
            callback.success()
        } else {
            callback.fail()
        }
        db.close()
    }

    override fun addSubAction(parentActionId: Int, action: Action, callback: ActionDataSource.OperationCallback) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ActionTable.COLUMN_PROJECT_ID, action.projectId)
            put(ActionTable.COLUMN_PARENT_ACTION_ID, parentActionId)
            put(ActionTable.COLUMN_CONTENT, action.content)
            put(ActionTable.COLUMN_DEADLINE, action.deadline)
            put(ActionTable.COLUMN_COMPLETE, action.complete)
            put(ActionTable.COLUMN_FLAG, action.flag)
        }

        if (db.insert(ActionTable.TABLE_NAME, null, values) != 0L) {
            callback.success()
        } else {
            callback.fail()
        }
        db.close()
    }

    override fun deleteAction(actionId: Int, callback: ActionDataSource.OperationCallback) {
        val db = dbHelper.writableDatabase

        with(db) {
            execSQL(LocalDbHelper.OPEN_FOREIGN_KEYS)

            if (delete(ActionTable.TABLE_NAME,
                    "${ActionTable.COLUMN_ID} = ?",
                    arrayOf(actionId.toString())) != 0)  {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }

    override fun updateAction(action: Action, callback: ActionDataSource.OperationCallback) {
        val values = ContentValues().apply {
            put(ActionTable.COLUMN_CONTENT, action.content)
            put(ActionTable.COLUMN_DEADLINE, action.deadline)
            put(ActionTable.COLUMN_COMPLETE, action.complete)
            put(ActionTable.COLUMN_FLAG, action.flag)
        }

        with(dbHelper.writableDatabase) {
            if (update(ActionTable.TABLE_NAME, values,
                    "${ActionTable.COLUMN_ID} = ?",
                    arrayOf(action.id.toString())) != 0) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }
}