package com.binding.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * an annotation that declares where the {@link SubscriptionName} annotations are declared, in other
 * words, which class that this Object should bind to
 * <p>
 * Created by Ahmed Adel Ismail on 1/9/2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubscriptionsFactory {

    /**
     * the Class that holds the members that declares the {@link SubscriptionName} annotations
     *
     * @return the {@link Class} that declares the {@link SubscriptionName} annotation on it's
     * members
     */
    Class<?> value();

}
