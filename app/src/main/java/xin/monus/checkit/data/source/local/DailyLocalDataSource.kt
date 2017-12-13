package xin.monus.checkit.data.source.local

import android.content.ContentValues
import android.content.Context
import xin.monus.checkit.data.entity.Daily
import xin.monus.checkit.data.source.DailyDataSource
import xin.monus.checkit.db.LocalDbHelper
import xin.monus.checkit.db.LocalTable.DailyTable

class DailyLocalDataSource private constructor(context: Context) : DailyDataSource {

    private val dbHelper: LocalDbHelper = LocalDbHelper(context)
    companion object {
        private var INSTANCE: DailyDataSource? = null
        @JvmStatic fun getInstance(context: Context) =
                INSTANCE ?: DailyLocalDataSource(context).apply { INSTANCE = this }
    }

    override fun getDailyItems(callback: DailyDataSource.LoadDailyItemsCallback) {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(DailyTable.COLUMN_ID, DailyTable.COLUMN_USERNAME,
                DailyTable.COLUMN_CONTENT, DailyTable.COLUMN_REMIND_TIME,
                DailyTable.COLUMN_COMPLETE, DailyTable.COLUMN_FLAG)

        val cursor = db.query(DailyTable.TABLE_NAME, projection,
                null, null, null, null, null)

        val items = ArrayList<Daily>()
        with(cursor) {
            while (cursor.moveToNext()) {
                val itemId = getInt(getColumnIndexOrThrow(DailyTable.COLUMN_ID))
                val username = getString(getColumnIndexOrThrow(DailyTable.COLUMN_USERNAME))
                val content = getString(getColumnIndexOrThrow(DailyTable.COLUMN_CONTENT))
                val remindTime = getString(getColumnIndexOrThrow(DailyTable.COLUMN_CONTENT))
                val complete = getInt(getColumnIndexOrThrow(DailyTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(DailyTable.COLUMN_FLAG)) != 0
                val dailyItem = Daily(itemId, username, content, remindTime, complete, flag)
                items.add(dailyItem)
            }
            close()
        }
        db.close()
        if (items.isNotEmpty()) {
            callback.onDailyItemsLoaded(items)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun getDailyItemById(itemId: Int, callback: DailyDataSource.GetDailyItemCallback) {
        val db = dbHelper.readableDatabase

        val projection = arrayOf(DailyTable.COLUMN_ID, DailyTable.COLUMN_USERNAME,
                DailyTable.COLUMN_CONTENT, DailyTable.COLUMN_REMIND_TIME,
                DailyTable.COLUMN_COMPLETE, DailyTable.COLUMN_FLAG)

        val cursor = db.query(DailyTable.TABLE_NAME, projection,
                "${DailyTable.COLUMN_ID}  = ?",
                arrayOf(itemId.toString()), null, null, null)

        var dailyItem: Daily? = null
        with(cursor) {
            if (moveToFirst()) {
                val username = getString(getColumnIndexOrThrow(DailyTable.COLUMN_USERNAME))
                val content = getString(getColumnIndexOrThrow(DailyTable.COLUMN_CONTENT))
                val remindTime = getString(getColumnIndexOrThrow(DailyTable.COLUMN_CONTENT))
                val complete = getInt(getColumnIndexOrThrow(DailyTable.COLUMN_COMPLETE)) != 0
                val flag = getInt(getColumnIndexOrThrow(DailyTable.COLUMN_FLAG)) != 0
                dailyItem = Daily(itemId, username, content, remindTime, complete, flag)
            }
            close()
        }
        db.close()

        dailyItem?.also {
            callback.onDailyItemLoaded(it)
        } ?: callback.onDataNotAvailable()
    }

    override fun addDailyItem(item: Daily, callback: DailyDataSource.OperationCallback) {
        val values = ContentValues().apply {
            put(DailyTable.COLUMN_USERNAME, item.username)
            put(DailyTable.COLUMN_CONTENT, item.content)
            put(DailyTable.COLUMN_REMIND_TIME, item.remindTime)
            put(DailyTable.COLUMN_COMPLETE, item.complete)
            put(DailyTable.COLUMN_FLAG, item.flag)
        }
        with(dbHelper.writableDatabase) {
            if (insert(DailyTable.TABLE_NAME, null, values) != 0L) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }

    override fun deleteDailyItem(itemId: Int, callback: DailyDataSource.OperationCallback) {
        with(dbHelper.writableDatabase) {
            if (delete(DailyTable.TABLE_NAME,
                    "${DailyTable.COLUMN_ID} = ?",
                    arrayOf(itemId.toString())) > 0) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }

    override fun updateDailyItem(item: Daily, callback: DailyDataSource.OperationCallback) {
        val values = ContentValues().apply {
            put(DailyTable.COLUMN_CONTENT, item.content)
            put(DailyTable.COLUMN_REMIND_TIME, item.remindTime)
            put(DailyTable.COLUMN_COMPLETE, item.complete)
            put(DailyTable.COLUMN_FLAG, item.flag)
        }
        with(dbHelper.writableDatabase) {
            if (update(DailyTable.TABLE_NAME, values,
                    "${DailyTable.COLUMN_ID} = ?",
                    arrayOf(item.id.toString())) > 0) {
                callback.success()
            } else {
                callback.fail()
            }
            close()
        }
    }


}