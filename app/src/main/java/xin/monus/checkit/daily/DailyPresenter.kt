package xin.monus.checkit.daily

/**
 * @author wu
 * @date   2017/12/6
 */
class DailyPresenter(
        val dailyView: DailyContract.View
) : DailyContract.Presenter {

    init {
        dailyView.presenter = this
    }

    override fun start() {
        println("daily activity start")
    }
}