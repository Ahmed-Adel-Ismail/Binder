package com.android.binding;

import com.vodafone.binding.Binder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * an annotation that marks Subscription-Factory classes that it is common between multiple
 * {@link Binder} instances
 * <p>
 * Created by Ahmed Adel Ismail on 2/1/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SharedSubscriptionFactory {
}
