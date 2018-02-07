package com.binding.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.reactivex.disposables.Disposable;

/**
 * an annotation that is declared above methods that returns a {@link Disposable}, the {@link #value()}
 * should supply the name of the subscription declared in {@link SubscriptionName}
 * <p>
 * Created by Ahmed Adel Ismail on 1/9/2018.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SubscribeTo {

    /**
     * the name of the subscription declared in {@link SubscriptionName#value()}
     *
     * @return the name of the subscription
     */
    String value();

}
