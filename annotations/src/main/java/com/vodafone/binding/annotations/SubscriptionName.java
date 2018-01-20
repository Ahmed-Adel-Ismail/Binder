package com.vodafone.binding.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * an annotation that marks fields or methods that should be passed as parameter to methods annotated
 * with {@link SubscribeTo} ... the {@link #value()} will supply the subscription name ... this is
 * to avoid obfuscation issues
 * <p>
 * Created by Ahmed Adel Ismail on 1/9/2018.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SubscriptionName {

    /**
     * the name of the subscription that is declared in {@link SubscribeTo#value()}
     *
     * @return the name of the subscription
     */
    String value();

}
