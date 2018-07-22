package app.injection.component

import dagger.Subcomponent
import app.injection.PerActivity
import app.injection.module.ActivityModule
import app.mvpbase.MvpBaseActivity
import app.ui.splash.SplashActivity
import app.ui.userselection.UserSelectionActivity

@PerActivity
@Subcomponent(modules = [(ActivityModule::class)])
interface ActivityComponent {
    fun inject(mvpBaseActivity: MvpBaseActivity)
    fun inject(splashActivity: SplashActivity)
    fun inject(UserSelectionActivity: UserSelectionActivity)
}
