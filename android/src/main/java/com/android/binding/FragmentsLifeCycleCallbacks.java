package com.android.binding;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * a class that handles Activities LifeCycle callbacks
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
class FragmentsLifeCycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {


    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        if (!BindersCache.contains(f)) {
            bind(f);
        }
    }

    private void bind(Fragment activity) {
        try {
            new BindingInitializer().accept(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        new BindingClearer().accept(f);
    }
}
