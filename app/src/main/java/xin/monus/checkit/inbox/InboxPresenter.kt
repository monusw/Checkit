package xin.monus.checkit.inbox

import dalvik.system.InMemoryDexClassLoader
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.data.source.repository.InboxItemRepository
import java.util.function.IntBinaryOperator

class InboxPresenter(
        val inboxItemRepository: InboxItemRepository,
        val inboxView: InboxContract.View
) : InboxContract.Presenter {

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
                inboxView.setEndRefresh()
                inboxView.showItems(items)
            }

            override fun onDataNotAvailable() {
                println("No data!")
            }

        })
    }

}