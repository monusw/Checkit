package xin.monus.checkit.data.source.repository

import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import java.util.*

class InboxItemRepository(
        // TODO: add local database and remote database
//        val inboxItemRemoteDataSource: InboxItemDataSource,
//        val inboxItemLocalDataSource: InboxItemDataSource
): InboxItemDataSource {

    var cachedInboxItems: LinkedHashMap<String, InboxItem> = LinkedHashMap()

    override fun getInboxItem(itemId: String, callback: InboxItemDataSource.GetInboxItemCallBack) {

        val inboxItemInCache = getInboxItemWithId(itemId)
        if (inboxItemInCache != null) {
            callback.onInboxItemLoaded(inboxItemInCache)
            return
        }
        else {
            // for test
            val newItem = InboxItem(1, "test", "hello", "2017-12", true, false)
            callback.onInboxItemLoaded(newItem)
            return
        }
    }

    private fun getInboxItemWithId(id: String) = cachedInboxItems[id]

}