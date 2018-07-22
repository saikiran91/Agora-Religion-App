package app.injection.component

import app.injection.PerFragment
import app.injection.module.FragmentModule
import app.mvpbase.MvpBaseFragment
import dagger.Subcomponent

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = [(FragmentModule::class)])
interface FragmentComponent {
    fun inject(mvpBaseFragment: MvpBaseFragment)
}