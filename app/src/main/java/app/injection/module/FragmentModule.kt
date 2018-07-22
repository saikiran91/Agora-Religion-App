package app.injection.module

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import app.injection.ActivityContext

import dagger.Module
import dagger.Provides

@Module
class FragmentModule(private val mFragment: Fragment) {

    @Provides
    internal fun providesFragment(): Fragment {
        return mFragment
    }

    @Provides
    internal fun provideActivity(): Activity {
        return mFragment.activity!!
    }

    @Provides
    @ActivityContext
    internal fun providesContext(): Context {
        return mFragment.activity!!
    }

}