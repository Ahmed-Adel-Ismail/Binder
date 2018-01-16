package com.vodafone.binder;

import android.arch.lifecycle.MutableLiveData;

import com.vodafone.binding.annotations.SubscriptionName;

import io.reactivex.properties.Property;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Ahmed Adel Ismail on 1/9/2018.
 */

public class ViewModel extends android.arch.lifecycle.ViewModel {

    @SubscriptionName("stringSubject")
    final Subject<String> stringSubject = PublishSubject.create();

    @SubscriptionName("stringLiveData")
    final MutableLiveData<String> stringLiveData = new MutableLiveData<>();

    private final Subject<Integer> intSubject = PublishSubject.create();
    private final Property<String> stringProperty = new Property<>();


    @SubscriptionName("intSubject")
    Subject<Integer> getIntSubject() {
        return intSubject;
    }

    @SubscriptionName("stringProperty")
    public Property<String> getStringProperty() {
        return stringProperty;
    }

    void updateString() {
        stringSubject.onNext(randomString());
    }

    private String randomString() {
        return String.valueOf(randomNumber());
    }

    private int randomNumber() {
        return (int) (Math.random() * 1000);
    }

    void updateProperty() {
        stringProperty.set(randomString());
    }

    void updateLiveData() {
        stringLiveData.setValue(randomString());
    }

    void updateNumber() {
        intSubject.onNext(randomNumber());
    }

    void clear() {
        stringProperty.clear();
    }


}
