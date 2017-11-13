package xin.monus.checkit.data.source.repository

import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import java.util.*

class InboxItemRepository(
        // TODO: add local database and remote database
//        val inboxItemRemoteDataSource: InboxItemDataSource,
        val inboxItemLocalDataSource: InboxItemDataSource
): InboxItemDataSource {

    var cachedInboxItems: LinkedHashMap<Int, InboxItem> = LinkedHashMap()

    override fun getInboxItemById(id: Int, callback: InboxItemDataSource.GetInboxItemCallBack) {
        inboxItemLocalDataSource.getInboxItemById(id, object : InboxItemDataSource.GetInboxItemCallBack {
            override fun onInboxItemLoaded(item: InboxItem) {
                callback.onInboxItemLoaded(item)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })

    }

    private fun getInboxItemWithId(id: Int) = cachedInboxItems[id]


    companion object {
        private var INSTANCE: InboxItemRepository? = null

        @JvmStatic fun getInstance(tasksLocalDataSource: InboxItemDataSource): InboxItemRepository {
            return INSTANCE ?: InboxItemRepository(tasksLocalDataSource)
                    .apply { INSTANCE = this }
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }

}