package com.mounacheikhna.competeapp.application;

import android.app.Application;
import android.content.Context;
import com.mounacheikhna.competeapp.annotation.MobileScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by cheikhnamouna on 3/18/16.
 */
@Module
public class CompeteAppModule {
  private final Application application;

  @Provides @MobileScope public Application provideApplication() {
    return application;
  }

  @Provides @ApplicationContext @MobileScope public Context provideApplicationContext() {
    return application.getApplicationContext();
  }

  public CompeteAppModule(Application application) {
    this.application = application;
  }


}
