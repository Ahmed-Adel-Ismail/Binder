package com.android.binding;

import com.binding.Binder;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * a cache that stores all binders
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
class BindersCache {

    private static final LinkedHashMap<Object, Binder> binders = new LinkedHashMap<>(10);
    private static final LinkedHashSet<Integer> commonFactoriesHashCodes = new LinkedHashSet<>(10);

    static void put(Object o, Binder binder) {
        updateCommonFactories(binder);
        binders.put(o, binder);

    }

    private static void updateCommonFactories(Binder binder) {
        int hashCode = binder.getSubscriptionsFactory().hashCode();
        for (Binder currentBinder : binders.values()) {
            if (currentBinder.getSubscriptionsFactory().hashCode() == hashCode) {
                commonFactoriesHashCodes.add(hashCode);
                return;
            }
        }
    }

    static Object getSubscriptionsFactoryOrNull(Class<?> factoryClass) {
        String name = factoryClass.getName();
        for (Binder binder : binders.values()) {
            if (binder.getSubscriptionsFactory().getClass().getName().equals(name)) {
                return binder.getSubscriptionsFactory();
            }
        }
        return null;
    }

    static boolean isCommonSubscriptionsFactory(Binder binder) {
        return binder != null
                && commonFactoriesHashCodes.contains(binder.getSubscriptionsFactory().hashCode());
    }

    static Binder remove(Object o) {
        return binders.remove(o);
    }

    static boolean contains(Object owner) {
        return binders.containsKey(owner);
    }
}
