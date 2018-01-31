package com.android.binding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * a class that handles Activities LifeCycle callbacks
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
class FragmentsLifeCycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {


    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        try {
            new BindingInitializer().accept(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        new BindingClearer().accept(f);
    }
}
