package com.binder.app;

import android.app.Application;

import com.android.binding.Binding;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Binding.integrate(this);
    }
}
