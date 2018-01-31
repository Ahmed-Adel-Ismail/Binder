package com.android.binding;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * a class that handles Activities LifeCycle callbacks
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
class ActivitiesLifeCycleCallback implements Application.ActivityLifecycleCallbacks {


    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        try {
            new BindingInitializer().accept(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        try {
            new BindingClearer().accept(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
