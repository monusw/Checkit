package xin.monus.checkit.data.source.repository

import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import java.util.*

class InboxItemRepository(
        // TODO: add remote database
        private val inboxItemLocalDataSource: InboxItemDataSource
//        private val inboxItemRemoteDataSource: InboxItemDataSource,
): InboxItemDataSource {

    /**
     * make it public for test
     */
    var cachedInboxItems: LinkedHashMap<Int, InboxItem> = LinkedHashMap()

    var cacheIsFresh = true

    /**
     * Singleton
     */
    companion object {
        private var INSTANCE: InboxItemRepository? = null

        @JvmStatic fun getInstance(inboxItemLocalDataSource: InboxItemDataSource): InboxItemRepository {
            return INSTANCE ?: InboxItemRepository(inboxItemLocalDataSource)
                    .apply { INSTANCE = this }
        }

        // For test
        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getInboxItems(callback: InboxItemDataSource.LoadInboxItemsCallback) {
        if (cachedInboxItems.isNotEmpty() && cacheIsFresh) {
            callback.onInboxItemsLoaded(ArrayList(cachedInboxItems.values))
            return
        }
        if (cacheIsFresh) {
            inboxItemLocalDataSource.getInboxItems(object : InboxItemDataSource.LoadInboxItemsCallback {
                override fun onInboxItemsLoaded(items: List<InboxItem>) {
                    refreshCache(items)
                    callback.onInboxItemsLoaded(ArrayList(cachedInboxItems.values))
                }

                override fun onDataNotAvailable() {
                    // TODO: Get data from remote
                    callback.onDataNotAvailable()
                }
            })
        } else {
            //TODO: Get data from remote and refresh local
        }
    }

    override fun getInboxItemById(id: Int, callback : InboxItemDataSource.GetInboxItemCallback) {
        val itemInCache = getInboxItemInCacheById(id)
        if (itemInCache != null) {
            callback.onInboxItemLoaded(itemInCache)
            return
        }
        // item not in cache get from local data base and put it in cache
        inboxItemLocalDataSource.getInboxItemById(id, object : InboxItemDataSource.GetInboxItemCallback {
            override fun onInboxItemLoaded(item: InboxItem) {
                cacheAndPerform(item) {
                    callback.onInboxItemLoaded(it)
                }
            }

            override fun onDataNotAvailable() {
                // TODO: Get data from remote
                callback.onDataNotAvailable()
            }
        })
    }

    override fun addInboxItem(item: InboxItem, callback: InboxItemDataSource.OperationCallback) {
        inboxItemLocalDataSource.addInboxItem(item, object : InboxItemDataSource.OperationCallback {
            override fun success() {
                refreshFromDB()
                callback.success()
            }
            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun deleteInboxItem(id: Int, callback: InboxItemDataSource.OperationCallback) {
        inboxItemLocalDataSource.deleteInboxItem(id, object : InboxItemDataSource.OperationCallback {
            override fun success() {
                cachedInboxItems.remove(id)
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun deleteCompleteItems(callback: InboxItemDataSource.OperationCallback) {
        inboxItemLocalDataSource.deleteCompleteItems(object : InboxItemDataSource.OperationCallback {
            override fun success() {
                deleteCompleteItems()
                callback.success()
            }
            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun deleteAllItems(callback: InboxItemDataSource.OperationCallback) {
        inboxItemLocalDataSource.deleteAllItems(object : InboxItemDataSource.OperationCallback {
            override fun success() {
                deleteAllItems()
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }

    override fun updateInboxItem(item: InboxItem, callback: InboxItemDataSource.OperationCallback) {
        inboxItemLocalDataSource.updateInboxItem(item, object : InboxItemDataSource.OperationCallback {
            override fun success() {
                cachedInboxItems[item.id] = item
                callback.success()
            }

            override fun fail() {
                callback.fail()
            }
        })
    }


    /**
     * Private functions
     */
    private fun getInboxItemInCacheById(id: Int) = cachedInboxItems[id]

    private inline fun cacheAndPerform(item: InboxItem, perform: (InboxItem) -> Unit) {
        cachedInboxItems.put(item.id, item)
        perform(item)
    }

    private fun refreshCache(items: List<InboxItem>) {
        cachedInboxItems.clear()
        items.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsFresh = true
    }

    private fun refreshFromDB() {
        inboxItemLocalDataSource.getInboxItems(object : InboxItemDataSource.LoadInboxItemsCallback {
            override fun onInboxItemsLoaded(items: List<InboxItem>) {
                refreshCache(items)
            }

            override fun onDataNotAvailable() {
            }
        })
    }

    private fun deleteCompleteItems() {
        val iterator = cachedInboxItems.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.complete) {
                iterator.remove()
            }
        }
    }

    private fun deleteAllItems() {
        val iterator = cachedInboxItems.entries.iterator()
        while (iterator.hasNext()) {
            iterator.next()
            iterator.remove()
        }
    }


}