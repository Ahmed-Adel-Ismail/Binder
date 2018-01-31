package com.android.binding;

import android.arch.lifecycle.ViewModel;

import com.vodafone.binding.Binder;
import com.vodafone.binding.annotations.SubscriptionsFactory;

/**
 * an interface implemented by classes that provides a custom {@link SubscriptionsFactory} Object,
 * which is Objects other than {@link ViewModel} sub-classes, or objects that will be initialized
 * by the {@link Binder} it-self
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
public interface SubscriptionFactoryProvider {

    /**
     * provide the Object that is considered your {@link SubscriptionsFactory} through this method
     *
     * @return the instance of the {@link SubscriptionsFactory} value
     */
    Object subscriptionFactory();

}
