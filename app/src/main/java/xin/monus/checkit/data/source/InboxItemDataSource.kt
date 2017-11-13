package xin.monus.checkit.data.source

import xin.monus.checkit.data.entity.InboxItem

interface InboxItemDataSource {

    interface LoadInboxItemsCallback {

        fun onInboxItemsLoaded(items: List<InboxItem>)

        fun onDataNotAvailable()

    }

    interface GetInboxItemCallback {

        fun onInboxItemLoaded(item: InboxItem)

        fun onDataNotAvailable()

    }

    /**
     * Add, delete, modify inbox item callback
     */
    interface OperationCallback {

        fun success()

        fun fail()

    }

    fun getInboxItems(callback: LoadInboxItemsCallback)

    fun getInboxItemById (id: Int, callback: GetInboxItemCallback)

    fun addInboxItem(item: InboxItem, callback: OperationCallback)

}