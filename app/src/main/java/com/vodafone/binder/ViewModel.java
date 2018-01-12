package com.vodafone.binder;

import com.vodafone.binding.annotations.SubscriptionName;

import java.util.Random;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Ahmed Adel Ismail on 1/9/2018.
 */

public class ViewModel {

    @SubscriptionName("stringSubject")
    final Subject<String> stringSubject = BehaviorSubject.createDefault("");
    private final Subject<Integer> intSubject = BehaviorSubject.createDefault(0);

    void updateString() {
        stringSubject.onNext(randomString());
    }

    private String randomString() {
        return String.valueOf(randomNumber());
    }

    private int randomNumber() {
        return new Random(10).nextInt();
    }

    void updateNumber(){
        intSubject.onNext(randomNumber());
    }

    @SubscriptionName("intSubject")
    Subject<Integer> getIntSubject(){
        return intSubject;
    }

}
