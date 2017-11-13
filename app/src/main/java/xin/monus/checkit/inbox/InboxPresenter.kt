package xin.monus.checkit.inbox

import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.data.source.repository.InboxItemRepository

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
//        load()
    }


    // for test
    override fun load() {
        inboxItemRepository.getInboxItemById(1, object : InboxItemDataSource.GetInboxItemCallBack {
            override fun onInboxItemLoaded(item: InboxItem) {
                inboxView.show(item)
            }

            override fun onDataNotAvailable() {
                println("not available")
            }

        })
    }

}