package com.binder.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binding.annotations.SubscribeTo;
import com.binding.annotations.SubscriptionsFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

@SubscriptionsFactory(ViewModel.class)
public class MainFragment extends Fragment {

    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container);
        textView = view.findViewById(R.id.fr_text);
        return view;
    }


    @SubscribeTo("stringSubject")
    Disposable stringSubscriber(Subject<String> subject) {
        return subject.share()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(textView::setText)
                .subscribe(v -> Log.e("MainFragment", "stringSubject : " + v));
    }

    @SubscribeTo("ownerName")
    void updateOwnerName(Subject<String> ownerName) {
        ownerName.onNext(getClass().getSimpleName());
    }


}
