package app.injection.component

import android.app.Application
import android.content.Context
import dagger.Component
import app.data.DataManager
import app.injection.ApplicationContext
import app.injection.module.ApplicationModule
import app.injection.module.Bindings
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationModule::class), (Bindings::class)])
interface ApplicationComponent {

    @ApplicationContext
    fun context(): Context

    fun application(): Application

    fun dataManager(): DataManager

}
