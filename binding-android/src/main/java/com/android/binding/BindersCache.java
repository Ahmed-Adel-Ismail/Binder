package com.android.binding;

import com.vodafone.binding.Binder;

import java.util.LinkedHashMap;

/**
 * a cache that stores all binders
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
class BindersCache {

    private static final LinkedHashMap<Object, Binder<?>> binders = new LinkedHashMap<>();

    static void put(Object o, Binder<?> binder) {
        binders.put(o, binder);
    }

    static Binder<?> remove(Object o) {
        return binders.remove(o);
    }
}
