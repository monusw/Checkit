package xin.monus.checkit.data.source.local

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

    override fun getInboxItemById(id: Int, callback: InboxItemDataSource.GetInboxItemCallBack) {
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



}