package xin.monus.checkit.settings

/**
 * @author wu
 * @date   2018/1/7
 */
class SettingsPresenter(
        val settingsView: SettingsContract.View
) : SettingsContract.Presenter {

    init {
        settingsView.presenter = this
    }

    override fun start() {
        println("settings presenter start")
    }

}