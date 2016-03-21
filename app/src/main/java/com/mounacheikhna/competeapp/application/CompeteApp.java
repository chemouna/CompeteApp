package com.mounacheikhna.competeapp.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.mounacheikhna.competeapp.R;
import io.fabric.sdk.android.Fabric;
import java.util.HashMap;
import javax.inject.Inject;
import rx.plugins.DebugHook;
import rx.plugins.DebugNotification;
import rx.plugins.DebugNotificationListener;
import rx.plugins.RxJavaPlugins;
import timber.log.Timber;

/**
 * Created by cheikhnamouna on 3/18/16.
 */
public class CompeteApp extends Application {

  private static final String FIRST_TIME = "FIRST_TIME";
  private static final String TAG = "MrGabrielApp";

  @Inject Fabric mFabric;

  HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();
  private AppComponent mComponent;

  public static CompeteApp get(Context context) {
    return (CompeteApp) context.getApplicationContext();
  }

  public AppComponent getComponent() {
    return mComponent;
  }

  synchronized public Tracker getTracker(TrackerName trackerId) {
    final GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(this);
    if (BuildConfig.DEBUG) {
      googleAnalytics.setDryRun(true);
      googleAnalytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
    }
    if (!mTrackers.containsKey(trackerId)) {
      Tracker tracker = googleAnalytics.newTracker(R.xml.analytics);
      mTrackers.put(trackerId, tracker);
    }
    return mTrackers.get(trackerId);
  }

  @Override public void onCreate() {
    super.onCreate();
    mComponent = AppComponent.Initializer.init(this);
    mComponent.injectApplication(this);
    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    final boolean first_time = sharedPreferences.getBoolean(FIRST_TIME, true);
    if (first_time) {
      final SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putBoolean(FIRST_TIME, false);
      editor.apply();
    }
    Fabric.with(mFabric);

    Stetho.initializeWithDefaults(this);

    setupRxJavaDebug();

    initTimber();
    //LeakCanary.install(this);
  }

  private void initTimber() {
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    } else {
      Timber.plant(new CrashlyticsTree());
    }
  }

  @SuppressWarnings("unchecked") private void setupRxJavaDebug() {
    RxJavaPlugins.getInstance()
        .registerObservableExecutionHook(new DebugHook(new DebugNotificationListener() {
          @Override public Object onNext(DebugNotification n) {
            Log.v(TAG, "DebugHook - onNext with value " + n.getValue() + " from op : " + n.getFrom());
            return super.onNext(n);
          }

          @Override public Object start(DebugNotification n) {
            Log.v(TAG, "DebugHook - start of " + n.getFrom());
            return super.start(n);
          }

          @Override public void complete(Object context) {
            Log.v(TAG, "DebugHook - complete event ");
            super.complete(context);
          }

          @Override public void error(Object context, Throwable e) {
            Log.v(TAG, "DebugHook - error event e : " + e.getCause());
            super.error(context, e);
          }
        }));
  }

  public enum TrackerName {
    APP_TRACKER
  }

}
