package com.binder.app;


import com.binding.annotations.SubscriptionName;

import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Ahmed Adel Ismail on 1/9/2018.
 */

public class SubViewModel {

    @SubscriptionName("stringSubject")
    final Subject<String> stringSubject = BehaviorSubject.createDefault("");

    @SubscriptionName("stringSubject2")
    final Subject<String> stringSubject2 = BehaviorSubject.createDefault("");
}
