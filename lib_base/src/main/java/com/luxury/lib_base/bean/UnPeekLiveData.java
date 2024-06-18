package com.luxury.lib_base.bean;

/**
 * Description:
 * author       : brain
 * createTime   : 2024/6/12 16:55
 **/
public class UnPeekLiveData<T> extends ProtectedUnPeekLiveData<T> {
    public UnPeekLiveData(T value) {
        super(value);
    }

    public UnPeekLiveData() {
    }

    public void setValue(T value) {
        super.setValue(value);
    }

    public void postValue(T value) {
        super.postValue(value);
    }

    public static class Builder<T> {
        private boolean isAllowNullValue;

        public Builder() {
        }

        public Builder<T> setAllowNullValue(boolean allowNullValue) {
            this.isAllowNullValue = allowNullValue;
            return this;
        }

        public UnPeekLiveData<T> create() {
            UnPeekLiveData<T> liveData = new UnPeekLiveData<>();
            liveData.isAllowNullValue = this.isAllowNullValue;
            return liveData;
        }
    }
}