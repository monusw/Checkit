package xin.monus.checkit.inbox

import android.widget.Toast
import dalvik.system.InMemoryDexClassLoader
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.data.source.repository.InboxItemRepository
import java.util.function.IntBinaryOperator

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
        loadItems()
    }


    // for test
    override fun loadItems() {
        inboxItemRepository.getInboxItems(object :InboxItemDataSource.LoadInboxItemsCallback {
            override fun onInboxItemsLoaded(items: List<InboxItem>) {
//                showItemList = items as MutableList<InboxItem>
//                for (i in items) {
//                    if (!i.complete) {
//                        showItemList!!.add(i)
//                    }
//                }
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
                println("No data!")
            }

        })
    }

    override fun completeButtonListener(itemID: Int) {
        inboxItemRepository.getInboxItemById(itemID, object : InboxItemDataSource.GetInboxItemCallback{
            override fun onInboxItemLoaded(item: InboxItem) {
                val tempInboxItem = item
                tempInboxItem.complete = !tempInboxItem.complete //changed in the adapter

                inboxItemRepository.updateInboxItem(tempInboxItem, object : InboxItemDataSource.OperationCallback{
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
}