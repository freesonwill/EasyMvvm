package com.xcjh.app.component.listener;

public class StatusObserver {

    private static volatile StatusObserver instance = null;

    public static StatusObserver getInstance() {
        if (instance == null) {
            synchronized (StatusObserver.class) {
                instance = new StatusObserver();
            }
        }
        return instance;
    }

    private OnStatusListener onStatusListener;

    public void addListener(OnStatusListener listener) {
        onStatusListener = listener;
    }

    public void removeListener(){
        onStatusListener = null;
    }

    public OnStatusListener getListener() {
        return onStatusListener;
    }

}
