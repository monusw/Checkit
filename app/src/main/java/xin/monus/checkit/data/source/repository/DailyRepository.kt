package xin.monus.checkit.data.source.repository

import xin.monus.checkit.data.entity.Daily
import xin.monus.checkit.data.source.DailyDataSource

class DailyRepository(
        private val dailyLocalDataSource: DailyDataSource
): DailyDataSource {

    companion object {
        private var INSTANCE: DailyRepository? = null

        @JvmStatic fun getInstance(dailyLocalDataSource: DailyDataSource) : DailyRepository {
            return INSTANCE ?: DailyRepository(dailyLocalDataSource).apply {
                INSTANCE = this
            }
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }


    override fun getDailyItems(callback: DailyDataSource.LoadDailyItemsCallback) {
        dailyLocalDataSource.getDailyItems(object : DailyDataSource.LoadDailyItemsCallback {
            override fun onDailyItemsLoaded(items: List<Daily>) {
                callback.onDailyItemsLoaded(items)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun getDailyItemById(itemId: Int, callback: DailyDataSource.GetDailyItemCallback) {
        dailyLocalDataSource.getDailyItemById(itemId, object : DailyDataSource.GetDailyItemCallback {
            override fun onDailyItemLoaded(item: Daily) {
                callback.onDailyItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun addDailyItem(item: Daily, callback: DailyDataSource.OperationCallback) {
        dailyLocalDataSource.addDailyItem(item, object : DailyDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun deleteDailyItem(itemId: Int, callback: DailyDataSource.OperationCallback) {
        dailyLocalDataSource.deleteDailyItem(itemId, object : DailyDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun updateDailyItem(item: Daily, callback: DailyDataSource.OperationCallback) {
        dailyLocalDataSource.updateDailyItem(item, object : DailyDataSource.OperationCallback {
            override fun success() {
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }





}