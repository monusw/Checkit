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

    override fun getInboxItemById(itemId: Int, callback: InboxItemDataSource.GetInboxItemCallBack) {


    }

    private fun getInboxItemWithId(id: String) = cachedInboxItems[id]

}