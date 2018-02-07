package com.android.binding;

import com.binding.Binder;
import com.binding.annotations.SubscriptionsFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * in your class that is considered {@link SubscriptionsFactory} (your View-Model for example),
 * mark your clean-up method with this annotation so that the {@link Binder} will call it
 * when the Activity of Fragment are totally destroyed
 * <p>
 * Created by Ahmed Adel Ismail on 1/31/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnSubscriptionsClosed {
}
