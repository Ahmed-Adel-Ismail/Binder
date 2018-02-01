package com.android.binding;

import android.app.Application;
import android.support.v4.app.FragmentManager;

/**
 * a class that integrates the Binding process to your android application
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
public class Binding extends FragmentManager.FragmentLifecycleCallbacks {

    private Binding() {

    }

    public static void integrate(Application application) {
        application.registerActivityLifecycleCallbacks(new ActivitiesLifeCycleCallback());
    }

}
