package com.vodafone.binder;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vodafone.binding.Binder;
import com.vodafone.binding.annotations.SubscribeTo;
import com.vodafone.binding.annotations.SubscriptionsFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.properties.Property;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

@SubscriptionsFactory(ViewModel.class)
public class MainActivity extends AppCompatActivity {

    private Binder<ViewModel> binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewModel viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        this.binder = Binder.bind(this).to(viewModel);

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.binder.getSubscriptionsFactory().updateString();
        this.binder.getSubscriptionsFactory().updateNumber();
        this.binder.getSubscriptionsFactory().updateLiveData();
        this.binder.getSubscriptionsFactory().updateProperty();
    }


    @SubscribeTo("stringSubject")
    Disposable stringSubscriber(Subject<String> subject) {
        return subject.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> Log.e("MainActivity", "stringSubject : " + v));
    }

    @SubscribeTo("intSubject")
    Disposable intSubscriber(Subject<Integer> subject) {
        return subject.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> Log.e("MainActivity", "intSubject : " + v));
    }

    @SubscribeTo("stringLiveData")
    void stringLiveDataSubscriber(MutableLiveData<String> liveData) {
        liveData.observe(this, text -> Log.e("MainActivity", "liveData : " + text));
    }

    @SubscribeTo("stringProperty")
    Disposable stringPropertySubscriber(Property<String> property) {
        return property.asObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> Log.e("MainActivity", "stringProperty : " + v));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binder.unbind();

        if (isFinishing()) {
            binder.getSubscriptionsFactory().clear();
        }
    }
}
