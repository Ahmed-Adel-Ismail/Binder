package com.android.binding;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.vodafone.binding.Binder;
import com.vodafone.binding.annotations.SubscriptionsFactory;

import java.lang.reflect.Constructor;

import io.reactivex.functions.Consumer;

/**
 * a function that initializes the Binding operation
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
class BindingInitializer implements Consumer<Object> {

    @Override
    public void accept(Object owner) throws Exception {

        if (isAnnotatedWithSubscriptionFactory(owner)) {
            BindersCache.put(owner, bind(owner));
        }

        if (owner instanceof FragmentActivity) {
            registerFragmentsLifeCycleCallbacks((FragmentActivity) owner);
        }
    }

    private boolean isAnnotatedWithSubscriptionFactory(Object owner) {
        return owner.getClass().isAnnotationPresent(SubscriptionsFactory.class);
    }

    private Binder<Object> bind(Object owner) throws Exception {
        return Binder.bind(owner).to(subscriptionsFactory(owner, subscriptionFactoryClass(owner)));
    }

    private void registerFragmentsLifeCycleCallbacks(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.registerFragmentLifecycleCallbacks(new FragmentsLifeCycleCallbacks(), true);
        }
    }

    @NonNull
    private Object subscriptionsFactory(Object owner, Class<?> factoryClass) throws Exception {
        if (owner instanceof SubscriptionFactoryProvider) {
            return ((SubscriptionFactoryProvider) owner).subscriptionFactory();
        } else if (ViewModel.class.isAssignableFrom(factoryClass)) {
            return createViewModel(owner, factoryClass);
        } else {
            return createNewSubscriptionsFactory(factoryClass);
        }
    }

    @NonNull
    private Class<?> subscriptionFactoryClass(Object owner) {
        return owner.getClass().getAnnotation(SubscriptionsFactory.class).value();
    }


    @NonNull
    private ViewModel createViewModel(Object owner, Class<?> subscriptionFactory) {
        if (owner instanceof FragmentActivity) {
            return activityViewModel(owner, subscriptionFactory);
        } else if (owner instanceof Fragment) {
            return fragmentViewModel(owner, subscriptionFactory);
        } else {
            throw new UnsupportedOperationException("The Annotated Class with " +
                    SubscriptionsFactory.class.getSimpleName() + "must be either a " +
                    "FragmentActivity (which is the parent of AppCompatActivity) " +
                    "or an android.support.v4.app.Fragment");
        }
    }

    private Object createNewSubscriptionsFactory(Class<?> factoryClass) throws Exception {
        Constructor constructor = factoryClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    @NonNull
    private ViewModel activityViewModel(Object owner, Class<?> subscriptionFactory) {
        return ViewModelProviders.of((FragmentActivity) owner)
                .get(viewModelClass(subscriptionFactory));
    }

    @NonNull
    private ViewModel fragmentViewModel(Object owner, Class<?> subscriptionFactory) {
        return ViewModelProviders.of((Fragment) owner)
                .get(viewModelClass(subscriptionFactory));
    }

    @SuppressWarnings("unchecked")
    private Class<? extends ViewModel> viewModelClass(Class<?> subscriptionFactory) {
        return (Class<? extends ViewModel>) subscriptionFactory;
    }
}
