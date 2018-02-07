package com.vodafone.binder;

import com.vodafone.binding.annotations.SubscriptionName;

import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Ahmed Adel Ismail on 2/7/2018.
 */

public class MainRepository {

    @SubscriptionName("dataSource")
    final BehaviorSubject<String> dataSource = BehaviorSubject.create();

}
