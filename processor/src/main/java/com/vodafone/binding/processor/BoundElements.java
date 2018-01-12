package com.vodafone.binding.processor;

import javax.lang.model.element.Element;

/**
 * a subscription between 2 elements
 * <p>
 * Created by Ahmed Adel Ismail on 1/10/2018.
 */
class BoundElements {

    private final Element source;
    private final Element subscriber;


    BoundElements(Element source, Element subscriber) {
        this.source = source;
        this.subscriber = subscriber;
    }

    Element getSource() {
        return source;
    }

    Element getSubscriber() {
        return subscriber;
    }


}
