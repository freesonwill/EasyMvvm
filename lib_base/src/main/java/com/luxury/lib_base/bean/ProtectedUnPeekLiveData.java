package com.luxury.lib_base.bean;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ProtectedUnPeekLiveData<T> extends LiveData<T> {
    private static final int START_VERSION = -1;
    private final AtomicInteger mCurrentVersion = new AtomicInteger(-1);
    protected boolean isAllowNullValue;

    public ProtectedUnPeekLiveData(T value) {
        super(value);
    }

    public ProtectedUnPeekLiveData() {
    }

    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, this.createObserverWrapper(observer, this.mCurrentVersion.get()));
    }

    public void observeForever(@NonNull Observer<? super T> observer) {
        super.observeForever(this.createObserverWrapper(observer, this.mCurrentVersion.get()));
    }

    public void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        super.observe(owner, this.createObserverWrapper(observer, -1));
    }

    public void observeStickyForever(@NonNull Observer<? super T> observer) {
        super.observeForever(this.createObserverWrapper(observer, -1));
    }

    protected void setValue(T value) {
        this.mCurrentVersion.getAndIncrement();
        super.setValue(value);
    }

    public void removeObserver(@NonNull Observer<? super T> observer) {
        if (observer.getClass().isAssignableFrom(ObserverWrapper.class)) {
            super.removeObserver(observer);
        } else {
            super.removeObserver(this.createObserverWrapper(observer, -1));
        }

    }

    private ProtectedUnPeekLiveData<T>.ObserverWrapper createObserverWrapper(@NonNull Observer<? super T> observer, int version) {
        return new ObserverWrapper(observer, version);
    }

    public void clean() {
        super.setValue((T) null);
    }

    class ObserverWrapper implements Observer<T> {
        private final Observer<? super T> mObserver;
        private int mVersion = -1;

        public ObserverWrapper(@NonNull Observer<? super T> observer, int version) {
            this.mObserver = observer;
            this.mVersion = version;
        }

        public void onChanged(T t) {
            if (ProtectedUnPeekLiveData.this.mCurrentVersion.get() > this.mVersion && (t != null || ProtectedUnPeekLiveData.this.isAllowNullValue)) {
                this.mObserver.onChanged(t);
            }

        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                ProtectedUnPeekLiveData<T>.ObserverWrapper that = (ObserverWrapper) o;
                return Objects.equals(this.mObserver, that.mObserver);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.mObserver});
        }
    }
}
