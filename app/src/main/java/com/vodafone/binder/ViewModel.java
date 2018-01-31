package com.vodafone.binder;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.binding.OnSubscriptionsClosed;
import com.vodafone.binding.annotations.SubscriptionName;

import io.reactivex.properties.Property;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ViewModel extends android.arch.lifecycle.ViewModel {

    @SubscriptionName("stringSubject")
    final Subject<String> stringSubject = PublishSubject.create();

    @SubscriptionName("stringLiveData")
    final MutableLiveData<String> stringLiveData = new MutableLiveData<>();

    private final Subject<Integer> intSubject = PublishSubject.create();
    private final Property<String> stringProperty = new Property<>();

    @SubscriptionName("updateData")
    final Subject<Boolean> updateData = PublishSubject.create();


    public ViewModel() {
        updateData.share()
                .doOnNext(trigger -> updateString())
                .doOnNext(trigger -> updateProperty())
                .doOnNext(trigger -> updateLiveData())
                .subscribe(trigger -> updateNumber());
    }

    @SubscriptionName("intSubject")
    Subject<Integer> getIntSubject() {
        return intSubject;
    }

    @SubscriptionName("stringProperty")
    public Property<String> getStringProperty() {
        return stringProperty;
    }

    private void updateString() {
        stringSubject.onNext(randomString());
    }

    private String randomString() {
        return String.valueOf(randomNumber());
    }

    private int randomNumber() {
        return (int) (Math.random() * 1000);
    }

    private void updateProperty() {
        stringProperty.set(randomString());
    }

    private void updateLiveData() {
        stringLiveData.setValue(randomString());
    }

    private void updateNumber() {
        intSubject.onNext(randomNumber());
    }

    @OnSubscriptionsClosed
    void clear() {
        updateData.onComplete();
        intSubject.onComplete();
        stringSubject.onComplete();
        stringProperty.clear();
        Log.e("ViewModel", "clear() invoked");
    }


}
