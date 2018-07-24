package app.injection.component

import app.injection.PerFragment
import app.injection.module.FragmentModule
import app.mvpbase.MvpBaseFragment
import app.ui.ongoingbroadcaster.OngoingBroadcasterFragment
import app.ui.upcomingbroadcaster.UpcomingBroadcasterFragment
import dagger.Subcomponent

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = [(FragmentModule::class)])
interface FragmentComponent {
    fun inject(mvpBaseFragment: MvpBaseFragment)
    fun inject(upcomingBroadcasterFragment: UpcomingBroadcasterFragment)
    fun inject(ongoingBroadcasterFragment: OngoingBroadcasterFragment)
}