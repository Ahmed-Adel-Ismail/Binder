package com.vodafone.binding.processor;


import com.chaining.Chain;

import javax.lang.model.element.ExecutableElement;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

import static com.vodafone.binding.processor.BinderCodeGenerator.VARIABLE_NAME_DISPOSABLES;
import static com.vodafone.binding.processor.BinderCodeGenerator.VARIABLE_NAME_SUBSCRIBER;
import static com.vodafone.binding.processor.BinderCodeGenerator.VARIABLE_NAME_SUBSCRIPTIONS;

class BoundElementsMerger implements Function<BoundElements, String> {

    @Override
    public String apply(BoundElements boundElements) {
        return Chain.let(boundElements)
                .map(this::subscriberMethod)
                .when(this::isReturningDisposable)
                .thenTo(VARIABLE_NAME_DISPOSABLES + ".add(%s)")
                .defaultIfEmpty("%s")
                .call()
                .replace("%s", subscriptionMethodCall(boundElements));
    }

    private String subscriptionMethodCall(BoundElements boundElements) {
        return Chain.let(VARIABLE_NAME_SUBSCRIBER)
                .and(".")
                .and(passSourceToSubscriber(boundElements))
                .reduce((leftString, rightString) -> leftString + rightString)
                .call();
    }

    private String passSourceToSubscriber(BoundElements boundElements) {
        return Chain.let(boundElements)
                .map(this::subscriberMethod)
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

    private ExecutableElement subscriberMethod(BoundElements boundElements) {
        return Chain.let(boundElements)
                .map(BoundElements::getSubscriber)
                .map(element -> (ExecutableElement) element)
                .call();
    }

    private boolean isReturningDisposable(ExecutableElement element) {
        return Disposable.class.getName().equals(element.getReturnType().toString());
    }
}
