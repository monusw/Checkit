package xin.monus.checkit.forecast

class ForecastPresenter(
        val forecastView: ForecastContract.View
) : ForecastContract.Presenter {
    init {
        forecastView.presenter = this
    }

    override fun start() {
        println("forecast presenter start")
    }
}