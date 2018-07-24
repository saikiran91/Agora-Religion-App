package app

import android.content.Context
import android.support.multidex.MultiDexApplication
import android.widget.Toast
import app.injection.component.ApplicationComponent
import app.injection.component.DaggerApplicationComponent
import app.injection.module.ApplicationModule
import app.model.ShowToastEvent

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import app.util.regOnce
import io.agora.religionapp.BuildConfig
import net.danlew.android.joda.JodaTimeAndroid

import timber.log.Timber

/**
 * Created by saikiran on 27-06-2018.
 */
class App : MultiDexApplication() {
    private var toast: Toast? = null
    // Needed to replace the component with a test specific one
    var component: ApplicationComponent
        get() {
            if (mApplicationComponent == null) {
                mApplicationComponent = DaggerApplicationComponent.builder()
                        .applicationModule(ApplicationModule(this))
                        .build()
            }
            return mApplicationComponent as ApplicationComponent
        }
        set(applicationComponent) {
            mApplicationComponent = applicationComponent
        }
    private var mApplicationComponent: ApplicationComponent? = null

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        EventBus.getDefault().regOnce(this)
        instance = this
        JodaTimeAndroid.init(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showToastEvent(event: ShowToastEvent) {
        if (toast != null) toast!!.cancel()
        toast = Toast.makeText(this, event.message, Toast.LENGTH_SHORT)
        toast!!.show()
    }

    companion object {
        lateinit var instance: App


        operator fun get(context: Context): App {
            return context.applicationContext as App
        }
    }
}