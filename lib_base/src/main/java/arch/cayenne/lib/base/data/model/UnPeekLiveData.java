package arch.cayenne.lib.base.data.model;

/**
 * @date: 2025/6/5 11:06
 * @description: UnPeekLiveData(非粘性LiveData)
 * 关键点是observer的时候version对齐（observer.version = liveData.version）
 * @see <a href="https://github.com/KunMinX/UnPeek-LiveData">...</a>
 */
public class UnPeekLiveData<T> extends ProtectedUnPeekLiveData<T> {

    public UnPeekLiveData(T value) {
        super(value);
    }

    public UnPeekLiveData() {
        super();
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    public static class Builder<T> {

        /**
         * 是否允许传入 null value
         */
        private boolean isAllowNullValue;

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
