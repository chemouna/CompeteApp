package com.mounacheikhna.competeapp.application;

import com.mounacheikhna.competeapp.annotation.MobileScope;
import dagger.Component;

@MobileScope
@Component(
    modules = {
        CompeteAppModule.class
    })
public interface AppComponent {
  void injectApplication(CompeteApp application);

  final class Initializer {
    private Initializer() {
    }

    static AppComponent init(CompeteApp app) {
      return DaggerAppComponent.builder()
          .competeAppModule(new CompeteAppModule(app))
          .build();
    }
  }
}
