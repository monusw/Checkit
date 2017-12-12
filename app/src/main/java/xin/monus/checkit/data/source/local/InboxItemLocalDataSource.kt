package xin.monus.checkit.data.source.local

import android.content.ContentValues
import android.content.Context
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.db.LocalTable.InboxItemTable

class InboxItemLocalDataSource private constructor(context: Context) : InboxItemDataSource {
    /**
     * Singleton
     */
    private val dbHelper: LocalDbHelper = LocalDbHelper(context)
    companion object {
        private var INSTANCE: InboxItemLocalDataSource? = null
        @JvmStatic fun getInstance(context: Context) =
                INSTANCE ?: InboxItemLocalDataSource(context).apply { INSTANCE = this }
    }

    /**
     * Get all inbox items from local database
     */
    override fun getInboxItems(callback: InboxItemDataSource.LoadInboxItemsCallback) {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
                InboxItemTable.COLUMN_ID, InboxItemTable.COLUMN_USERNAME, InboxItemTable.COLUMN_CONTENT,
                InboxItemTable.COLUMN_DEADLINE, InboxItemTable.COLUMN_COMPLETE, InboxItemTable.COLUMN_FLAG)
        val cursor = db.query(InboxItemTable.TABLE_NAME, projection,
                null, null, null, null, null)
        val items = ArrayList<InboxItem>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getInt(getColumnIndexOrThrow(InboxItemTable.COLUMN_ID))
                val username = getString(getColumnIndexOrThrow(InboxItemTable.COLUMN_USERNAME))
                val content = getString(getColumnIndexOrThrow(InboxItemTable.COLUMN_CONTENT))
                val deadline = getString(getColumnIndexOrThrow(InboxItemTable.COLUMN_DEADLINE))
                val complete = getInt(getColumnIndexOrThrow(InboxItemTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(InboxItemTable.COLUMN_FLAG)) != 0
                val inboxItem = InboxItem(itemId, username, content, deadline, complete, flag)
                items.add(inboxItem)
            }
            close()
        }
        db.close()
        if (items.isNotEmpty()) {
            callback.onInboxItemsLoaded(items)
        } else {
            callback.onDataNotAvailable()
        }
    }

    /**
     * Get one item by its id from local database
     */
    override fun getInboxItemById(id: Int, callback: InboxItemDataSource.GetInboxItemCallback) {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
                InboxItemTable.COLUMN_ID, InboxItemTable.COLUMN_USERNAME, InboxItemTable.COLUMN_CONTENT,
                InboxItemTable.COLUMN_DEADLINE, InboxItemTable.COLUMN_COMPLETE, InboxItemTable.COLUMN_FLAG)
        val cursor = db.query(InboxItemTable.TABLE_NAME, projection,
                "${InboxItemTable.COLUMN_ID} = ?",
                arrayOf(id.toString()),null, null, null)
        var inboxItem: InboxItem? = null
        with(cursor) {
            if (moveToFirst()) {
                val itemId = getInt(getColumnIndexOrThrow(InboxItemTable.COLUMN_ID))
                val username = getString(getColumnIndexOrThrow(InboxItemTable.COLUMN_USERNAME))
                val content = getString(getColumnIndexOrThrow(InboxItemTable.COLUMN_CONTENT))
                val deadline = getString(getColumnIndexOrThrow(InboxItemTable.COLUMN_DEADLINE))
                val complete = getInt(getColumnIndexOrThrow(InboxItemTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(InboxItemTable.COLUMN_FLAG)) != 0
                inboxItem = InboxItem(itemId, username, content, deadline, complete, flag)
            }
            close()
        }
        db.close()
        inboxItem?.also {
            callback.onInboxItemLoaded(it)
        } ?: callback.onDataNotAvailable()
    }

    /**
     *  Add a new inbox item to database
     */
    override fun addInboxItem(item: InboxItem, callback: InboxItemDataSource.OperationCallback) {
        val values = ContentValues().apply {
            put(InboxItemTable.COLUMN_USERNAME, item.username)
            put(InboxItemTable.COLUMN_CONTENT, item.content)
            put(InboxItemTable.COLUMN_DEADLINE, item.deadline)
            put(InboxItemTable.COLUMN_COMPLETE, item.complete)
            put(InboxItemTable.COLUMN_FLAG, item.flag)
        }
        with(dbHelper.writableDatabase) {
            if (insert(InboxItemTable.TABLE_NAME, null, values) != 0L) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }

    /**
     * Delete an item by id
     */
    override fun deleteInboxItem(id: Int, callback: InboxItemDataSource.OperationCallback) {
        with(dbHelper.writableDatabase) {
            if (delete(InboxItemTable.TABLE_NAME,
                    "${InboxItemTable.COLUMN_ID} = ?",
                    arrayOf(id.toString())) > 0) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }

    /**
     * Delete all items
     */
    override fun deleteAllItems(callback: InboxItemDataSource.OperationCallback) {
        with(dbHelper.writableDatabase) {
            if (delete(InboxItemTable.TABLE_NAME, null, null) > 0) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }

    /**
     * Delete all complete items
     */
    override fun deleteCompleteItems(callback: InboxItemDataSource.OperationCallback) {
        with(dbHelper.writableDatabase) {
            if (delete(InboxItemTable.TABLE_NAME,
                    "${InboxItemTable.COLUMN_COMPLETE} = ?",
                    arrayOf("1")) > 0) {
                callback.success()
            } else {
                callback.fail()
            }
        }
    }

    /**
     * Update an item
     */
    override fun updateInboxItem(item: InboxItem, callback: InboxItemDataSource.OperationCallback) {
        val values = ContentValues().apply {
            put(InboxItemTable.COLUMN_CONTENT, item.content)
            put(InboxItemTable.COLUMN_DEADLINE, item.deadline)
            put(InboxItemTable.COLUMN_COMPLETE, item.complete)
            put(InboxItemTable.COLUMN_FLAG, item.flag)
        }
        with(dbHelper.writableDatabase) {
            if (update(InboxItemTable.TABLE_NAME, values,
                    "${InboxItemTable.COLUMN_ID} = ?",
                    arrayOf(item.id.toString())) > 0) {
                callback.success()
            } else {
                callback.fail()
            }
        }
    }


}