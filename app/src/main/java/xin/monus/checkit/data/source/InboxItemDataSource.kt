package xin.monus.checkit.data.source

import xin.monus.checkit.data.entity.InboxItem

interface InboxItemDataSource {

    interface GetInboxItemCallBack {

        fun onInboxItemLoaded(item: InboxItem)

        fun onDataNotAvailable()

    }

    fun getInboxItemById (id: Int, callback: GetInboxItemCallBack)

}