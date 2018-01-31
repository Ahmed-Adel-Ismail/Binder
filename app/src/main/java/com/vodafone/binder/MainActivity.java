package com.vodafone.binder;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.LiteCycle;
import com.vodafone.binding.annotations.SubscribeTo;
import com.vodafone.binding.annotations.SubscriptionsFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.properties.Property;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

@SubscriptionsFactory(ViewModel.class)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SubscribeTo("updateData")
    void updateAll(Subject<Boolean> updateData) {
        LiteCycle.with(updateData)
                .forLifeCycle(this)
                .onResumeInvoke(subject -> subject.onNext(true))
                .observe();
    }


    @SubscribeTo("stringSubject")
    Disposable stringSubscriber(Subject<String> subject) {
        return subject.share()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> Log.e("MainActivity", "stringSubject : " + v));
    }

    @SubscribeTo("intSubject")
    Disposable intSubscriber(Subject<Integer> subject) {
        return subject.share()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> Log.e("MainActivity", "intSubject : " + v));
    }

    @SubscribeTo("stringLiveData")
    void stringLiveDataSubscriber(MutableLiveData<String> liveData) {
        liveData.observe(this,
                text -> Log.e("MainActivity", "liveData : " + text));
    }

    @SubscribeTo("stringProperty")
    Disposable stringPropertySubscriber(Property<String> property) {
        return property.asObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> Log.e("MainActivity", "stringProperty : " + v));
    }

}
