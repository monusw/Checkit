package xin.monus.checkit.inbox

import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.data.source.repository.InboxItemRepository

class InboxPresenter(
        val inboxItemRepository: InboxItemRepository,
        val inboxView: InboxContract.View
) : InboxContract.Presenter {

    lateinit var showItemList: List<InboxItem>

    private var firstLoad = true

    init {
        inboxView.presenter = this
    }

    override fun start() {
        // test
        println("inbox presenter")
        firstLoad = false
        inboxView.setStartRefresh()
        loadItems()
    }


    override fun loadItems() {
        inboxItemRepository.getInboxItems(object :InboxItemDataSource.LoadInboxItemsCallback {
            override fun onInboxItemsLoaded(items: List<InboxItem>) {
                val sourceData : MutableList<InboxItem> = items.toMutableList()
                var interator = 0
                while (interator<sourceData.size) {
                    if (sourceData[interator].complete) {
                        sourceData.removeAt(interator)
                        continue
                    }
                    interator++
                }
                showItemList = sourceData.toList()

                inboxView.setEndRefresh()
                inboxView.showItems(showItemList)
            }

            override fun onDataNotAvailable() {
                inboxView.setEndRefresh()
                inboxView.showItems(ArrayList(0))
                println("No data!")
            }

        })
    }


    override fun completeButtonListener(itemID: Int) {
        inboxItemRepository.getInboxItemById(itemID, object : InboxItemDataSource.GetInboxItemCallback{
            override fun onInboxItemLoaded(item: InboxItem) {
                item.complete = !item.complete //changed in the adapter

                inboxItemRepository.updateInboxItem(item, object : InboxItemDataSource.OperationCallback{
                    override fun success() {
                        println("Change state successfully!")
                    }

                    override fun fail() {
                        println("Fail to change state!")
                    }
                })
            }

            override fun onDataNotAvailable() {
                println("No data!")
            }
        })

    }

    override fun deleteItem(itemID: Int) {
        inboxItemRepository.deleteInboxItem(itemID, object : InboxItemDataSource.OperationCallback{
            override fun success() {
                loadItems()
                println("Delete successfully!")
            }

            override fun fail() {
                println("Delete fail!")
            }
        })
    }

    override fun deleteAll() {
        inboxItemRepository.deleteAllItems(object : InboxItemDataSource.OperationCallback{
            override fun success() {
                loadItems()
                println("Delete all items successfully!")
            }

            override fun fail() {
                println("Items deleting fail!")
            }

        })
    }

    override fun deleteFinished() {
        inboxItemRepository.deleteCompleteItems(object : InboxItemDataSource.OperationCallback{
            override fun success() {
                println("Delete completed item successfully")
            }

            override fun fail() {
                println("Completed item deleteing fail")
            }
        })
    }
}