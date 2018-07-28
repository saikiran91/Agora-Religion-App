package app.injection.component

import app.injection.PerActivity
import app.injection.module.ActivityModule
import app.mvpbase.MvpBaseActivity
import app.ui.addevent.AddEventActivity
import app.ui.liveboradcaster.LiveBroadcasterActivity
import app.ui.splash.SplashActivity
import app.ui.userselection.UserSelectionActivity
import app.ui.viewer.home.HomeActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [(ActivityModule::class)])
interface ActivityComponent {
    fun inject(mvpBaseActivity: MvpBaseActivity)
    fun inject(splashActivity: SplashActivity)
    fun inject(UserSelectionActivity: UserSelectionActivity)
    fun inject(homeActivity: HomeActivity)
    fun inject(homeActivity: app.ui.home.HomeActivity)
    fun inject(addEventActivity: AddEventActivity)
    fun inject(liveBroadcasterActivity: LiveBroadcasterActivity)

}
