package com.vodafone.binding.processor;


import com.chaining.Chain;

import javax.lang.model.element.ExecutableElement;

import io.reactivex.functions.Function;

import static com.vodafone.binding.processor.BinderCodeGenerator.VARIABLE_NAME_DISPOSABLES;
import static com.vodafone.binding.processor.BinderCodeGenerator.VARIABLE_NAME_SUBSCRIBER;
import static com.vodafone.binding.processor.BinderCodeGenerator.VARIABLE_NAME_SUBSCRIPTIONS;

class BoundElementsMerger implements Function<BoundElements, String> {

    @Override
    public String apply(BoundElements boundElements) {
        return Chain.let(VARIABLE_NAME_DISPOSABLES)
                .and(".add(")
                .and(VARIABLE_NAME_SUBSCRIBER)
                .and(".")
                .and(callPassingSourceToSubscriberMethod(boundElements))
                .and(")")
                .reduce((leftString, rightString) -> leftString + rightString)
                .call();
    }

    private String callPassingSourceToSubscriberMethod(BoundElements boundElements) {
        return Chain.let(boundElements)
                .map(BoundElements::getSubscriber)
                .map(element -> (ExecutableElement) element)
                .map(ExecutableElement::getSimpleName)
                .map(Object::toString)
                .and("(")
                .and(VARIABLE_NAME_SUBSCRIPTIONS)
                .and(".")
                .and(sourceNameOrCrash(boundElements))
                .and(")")
                .reduce((leftString, rightString) -> leftString + rightString)
                .call();
    }

    private String sourceNameOrCrash(BoundElements boundElements) {
        return Chain.optional(boundElements)
                .map(BoundElements::getSource)
                .map(Object::toString)
                .defaultIfEmpty(null)
                .call();
    }
}
