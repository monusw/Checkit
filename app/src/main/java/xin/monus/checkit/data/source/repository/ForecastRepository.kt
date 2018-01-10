package xin.monus.checkit.data.source.repository

import android.content.Context
import xin.monus.checkit.data.entity.Forecast
import xin.monus.checkit.data.entity.ForecastType
import xin.monus.checkit.data.entity.InboxItem
import xin.monus.checkit.data.entity.Project
import xin.monus.checkit.data.source.ForecastDataSource
import xin.monus.checkit.data.source.InboxItemDataSource
import xin.monus.checkit.data.source.ProjectsDataSource
import xin.monus.checkit.util.Injection

class ForecastRepository(context: Context) : ForecastDataSource {

    companion object {
        private var INSTANCE: ForecastRepository? = null

        @JvmStatic fun getInstance(context: Context) : ForecastRepository {
            return INSTANCE ?: ForecastRepository(context).apply {
                INSTANCE = this
            }
        }

        @JvmStatic fun destroyInstance() {
            INSTANCE = null
        }
    }

    val inboxRepository by lazy { Injection.getInboxItemRepository(context) }
    val projectRepository by lazy { Injection.getProjectsRepository(context) }

    val forecastList = ArrayList<Forecast>(0)

    override fun getAllForecast(callback: ForecastDataSource.GetForecastsCallback) {
        inboxRepository.getInboxItems(object : InboxItemDataSource.LoadInboxItemsCallback {
            override fun onInboxItemsLoaded(items: List<InboxItem>) {
                for (item in items) {
                    if (!item.complete) {
                        val forecast = Forecast(
                                type = ForecastType.INBOX,
                                content = item.content,
                                deadline = item.deadline
                        )
                        forecastList.add(forecast)
                    }
                }
                callback.onForecastsLoaded(forecastList)
            }
            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })

        projectRepository.getProjects(object : ProjectsDataSource.LoadProjectsCallback {
            override fun onProjectsLoaded(projects: List<Project>) {
                for (project in projects) {
                    for (action in project.actionList) {
                        if (!action.complete) {
                            val forecast = Forecast(
                                    type = ForecastType.ACTION,
                                    content = action.content,
                                    deadline = action.deadline
                            )
                            forecastList.add(forecast)
                        }
                    }
                }

                callback.onForecastsLoaded(forecastList)
            }

            override fun onDataNotAvailable() {

            }

        })

        if (forecastList.isEmpty()) {
            callback.onDataNotAvailable()
        }
    }

}