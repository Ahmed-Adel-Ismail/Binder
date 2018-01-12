package com.vodafone.binding.processor;

import com.vodafone.binding.annotations.SubscriptionsFactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

class BoundTypes {

    private final TypeElement elementWithSubscribeToAnnotations;
    private final TypeElement elementWithSubscriptionNameAnnotations;


    BoundTypes(ProcessingEnvironment environment, Element element) {
        this.elementWithSubscribeToAnnotations = TypeElement.class.cast(element);
        this.elementWithSubscriptionNameAnnotations = new AnnotationValueReader(environment)
                .apply(element.getAnnotation(SubscriptionsFactory.class)::value);
    }


    TypeElement getElementWithSubscribeToAnnotations() {
        return elementWithSubscribeToAnnotations;
    }

    TypeElement getElementWithSubscriptionNameAnnotations() {
        return elementWithSubscriptionNameAnnotations;
    }

    @Override
    public String toString() {
        return "BoundTypes{" +
                "elementWithSubscribeToAnnotations=" + elementWithSubscribeToAnnotations +
                ", elementWithSubscriptionNameAnnotations=" + elementWithSubscriptionNameAnnotations +
                '}';
    }
}
