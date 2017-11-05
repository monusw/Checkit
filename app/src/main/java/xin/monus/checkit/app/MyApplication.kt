package xin.monus.checkit.app

import android.app.Application
import android.content.Context
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

class MyApplication : Application() {
    val refWatcher: RefWatcher by lazy { LeakCanary.install(this) }

    companion object {
        fun getRefWatcher(context: Context): RefWatcher {
            val application = context.applicationContext as MyApplication
            return application.refWatcher
        }
    }
}