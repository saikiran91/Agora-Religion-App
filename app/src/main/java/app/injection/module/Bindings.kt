package app.injection.module

import app.data.DataManager
import app.data.DataManagerImpl
import dagger.Binds
import dagger.Module

@Module
abstract class Bindings {

  @Binds
  internal abstract fun bindDataManger(manager: DataManagerImpl): DataManager

}