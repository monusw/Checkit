package xin.monus.checkit.forecast

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import xin.monus.checkit.data.entity.Forecast
import xin.monus.checkit.data.source.ForecastDataSource

class ForecastPresenter(
        val forecastRepository: ForecastDataSource,
        val forecastView: ForecastContract.View
) : ForecastContract.Presenter {
    init {
        forecastView.presenter = this
    }

    val forecastList = ArrayList<Forecast>(0)

    override fun start() {
        getForecastList()
        println("forecast presenter start")
    }

    private fun getForecastList() {
        doAsync {
            forecastRepository.getAllForecast(object : ForecastDataSource.GetForecastsCallback {
                override fun onForecastsLoaded(forecasts: List<Forecast>) {
                    uiThread {
                        forecastList.clear()
                        forecastList.addAll(forecasts)
                        forecastView.initData(forecastList)
                        forecastView.showElement(1)
                    }
                    for (item in forecasts) {
                        println(item.content)
                        println(item.deadline)
                    }
                }

                override fun onDataNotAvailable() {
                    println("no forecast")
                    uiThread {
                        forecastView.showElement(1)
                    }
                }

            })
        }
    }

}