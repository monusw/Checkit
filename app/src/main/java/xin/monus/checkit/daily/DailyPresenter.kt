package xin.monus.checkit.daily

import xin.monus.checkit.data.entity.Daily
import xin.monus.checkit.data.source.DailyDataSource
import xin.monus.checkit.data.source.repository.DailyRepository

/**
 * @author wu
 * @date   2017/12/6
 */
class DailyPresenter(
        val dailyRepository: DailyRepository,
        val dailyView: DailyContract.View
) : DailyContract.Presenter {

    lateinit var showItemList: List<Daily>

    private var firstLoad = true

    init {
        dailyView.presenter = this
    }

    override fun start() {
        println("daily activity start")
        firstLoad = false
        dailyView.setStartRefresh()
        loadItems()
    }

    override fun loadItems() {
        dailyRepository.getDailyItems(object :DailyDataSource.LoadDailyItemsCallback {
            override fun onDailyItemsLoaded(items: List<Daily>) {
                val sourceData : MutableList<Daily> = items.toMutableList()
                var interator = 0
                while (interator<sourceData.size) {
                    if (sourceData[interator].complete) {
                        sourceData.removeAt(interator)
                        continue
                    }
                    interator++
                }
                showItemList = sourceData.toList()

                dailyView.setEndRefresh()
                dailyView.showItems(showItemList)
            }

            override fun onDataNotAvailable() {
                dailyView.setEndRefresh()
                dailyView.showItems(ArrayList(0))
                println("No data!")
            }

        })
    }

    override fun completeButtonListener(itemID: Int) {
        dailyRepository.getDailyItemById(itemID, object : DailyDataSource.GetDailyItemCallback{
            override fun onDailyItemLoaded(item: Daily) {
                val tempDailyItem = item
                tempDailyItem.complete = !tempDailyItem.complete //changed in the adapter

                dailyRepository.updateDailyItem(tempDailyItem, object : DailyDataSource.OperationCallback{
                    override fun success() {
                        println("Change state successfully!")
                        loadItems()
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
        dailyRepository.deleteDailyItem(itemID, object : DailyDataSource.OperationCallback{
            override fun success() {
                loadItems()
                println("Delete successfully!")
            }

            override fun fail() {
                println("Delete fail!")
            }
        })
    }
}