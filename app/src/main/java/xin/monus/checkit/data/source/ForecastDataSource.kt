package xin.monus.checkit.data.source

import xin.monus.checkit.data.entity.Forecast

/**
 * @author wu
 * @date   2018/1/10
 */
interface ForecastDataSource {

    interface GetForecastsCallback {
        fun onForecastsLoaded(forecasts: List<Forecast>)
        fun onDataNotAvailable()
    }

    fun getAllForecast(callback: GetForecastsCallback)

}