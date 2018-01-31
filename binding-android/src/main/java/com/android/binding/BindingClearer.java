package com.android.binding;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.vodafone.binding.Binder;

import java.lang.reflect.Method;

import io.reactivex.functions.Consumer;

/**
 * a function that cleares the binding operation
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
class BindingClearer implements Consumer<Object> {

    @Override
    public void accept(Object owner) throws Exception {
        Binder<?> binder = BindersCache.remove(owner);
        if (binder != null) {
            unbindAndInvokeClearOnFinishing(owner, binder);
        }
    }


    private void unbindAndInvokeClearOnFinishing(Object owner, Binder<?> binder) throws Exception {
        binder.unbind();
        invokeClearOnFinishing(owner, binder);
    }

    private void invokeClearOnFinishing(Object owner, Binder<?> binder) throws Exception {
        if (isActivityFinishing(owner) || isFragmentFinishing(owner)) {
            invokeClearMethodIfDeclared(binder);
        }
    }

    private boolean isActivityFinishing(Object owner) {
        return owner instanceof Activity && ((Activity) owner).isFinishing();
    }

    private boolean isFragmentFinishing(Object owner) {
        return owner instanceof Fragment && (((Fragment) owner).getActivity() == null
                || ((Fragment) owner).getActivity().isFinishing());
    }

    private void invokeClearMethodIfDeclared(Binder<?> binder) throws Exception {
        Object subscriptionFactory = binder.getSubscriptionsFactory();
        Method[] methods = subscriptionFactory.getClass().getDeclaredMethods();
        if (methods == null) return;
        for (Method method : methods) {
            if (invokeClearMethod(subscriptionFactory, method)) return;
        }
    }

    private boolean invokeClearMethod(Object subscriptionFactory, Method method) throws Exception {
        if (method.isAnnotationPresent(Clear.class)) {
            method.setAccessible(true);
            method.invoke(subscriptionFactory);
            return true;
        }
        return false;
    }
}
