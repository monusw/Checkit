package xin.monus.checkit.data.source

import xin.monus.checkit.data.entity.Daily

interface DailyDataSource {

    interface LoadDailyItemsCallback {
        fun onDailyItemsLoaded(items: List<Daily>)
        fun onDataNotAvailable()
    }

    interface GetDailyItemCallback {
        fun onDailyItemLoaded(item: Daily)
        fun onDataNotAvailable()
    }

    /**
     * Add, delete, modify inbox item callback
     */
    interface OperationCallback {
        fun success()
        fun fail()
    }

    fun getDailyItems(callback: LoadDailyItemsCallback)
    fun getDailyItemById(itemId: Int, callback: GetDailyItemCallback)
    fun addDailyItem(item: Daily, callback: OperationCallback)
    fun deleteDailyItem(itemId: Int, callback: OperationCallback)
    fun updateDailyItem(item: Daily, callback: OperationCallback)
}