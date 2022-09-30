package com.seed.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.ConcurrentHashMap;

public class NonStickyLiveData<T> extends LiveData<T> {
    private ConcurrentHashMap<Observer, ObserverWrapper> observerMapping = new ConcurrentHashMap<>();

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, wrap(observer));
    }

    @Override
    public void observeForever(@NonNull Observer<? super T> observer) {
        super.observeForever(wrap(observer));
    }

    @Override
    public void removeObserver(@NonNull Observer<? super T> observer) {
        ObserverWrapper wrapper = observerMapping.remove(observer);
        if (wrapper != null) {
            super.removeObserver(wrapper);
        } else {
            super.removeObserver(observer);
        }
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    private Observer wrap(Observer<? super T> observer) {
        ObserverWrapper proxyObserver = observerMapping.get(observer);
        if (proxyObserver == null) {
            proxyObserver = new ObserverWrapper(observer);
            observerMapping.put(observer, proxyObserver);
        }
        return proxyObserver;
    }

    /**
     * 包装observer。可以忽略第一次的onChanged回调
     * @param <T>
     */
    private class ObserverWrapper<T> implements Observer<T> {
        private final Observer targetObserver;
        private boolean ignoreOnce;

        public ObserverWrapper(Observer targetObserver) {
            this.targetObserver = targetObserver;
            this.ignoreOnce = getValue() != null;
        }

        @Override
        public void onChanged(T t) {
            if (ignoreOnce) {
                ignoreOnce = false;
            } else {
                targetObserver.onChanged(t);
            }
        }
    }
}
