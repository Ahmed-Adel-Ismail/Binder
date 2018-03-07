package com.android.binding;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * a class that handles Activities LifeCycle callbacks
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
class ActivitiesLifeCycleCallback implements Application.ActivityLifecycleCallbacks {


    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        if (activity instanceof FragmentActivity) {
            registerFragmentsLifeCycleCallbacks((FragmentActivity) activity);
        }
    }

    private void registerFragmentsLifeCycleCallbacks(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.registerFragmentLifecycleCallbacks(new FragmentsLifeCycleCallbacks(), true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (!BindersCache.contains(activity)) {
            bind(activity);
        }

    }

    private void bind(Activity activity) {
        try {
            new BindingInitializer().accept(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        new BindingClearer().accept(activity);
    }

}
