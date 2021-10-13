package com.cc.toastcompatlibrary;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.StringRes;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.BaseTransientBottomBar;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
public  class ToastCompat {
    private static Typeface toastTypeFace;
    private final Queue<ToastInfo> mQueue = new ConcurrentLinkedQueue<>();
    private ToastWrapper toastWrapper;
    private static ToastCompat toastCompat;

    public static void setToastTypeFace(Typeface toastTypeFace) {
        ToastCompat.toastTypeFace = toastTypeFace;
    }

    private ToastCompat() {
    }

    public static ToastCompat with() {
        if (toastCompat == null) {
            toastCompat = new ToastCompat();
        }
        return toastCompat;
    }

    public boolean cancel(ToastInfo info) {
        if (!mQueue.remove(info)) {
            if (Objects.equals(info, toastWrapper.info)) {
                return toastWrapper.cancel(info);
            }
            return false;
        }
        return true;
    }

    private ToastWrapper getToastWrapper(Context context) {
        if (toastWrapper == null) {
            toastWrapper = new ToastWrapper(context);
        }
        return toastWrapper;
    }

    public static IToast makeText(Context context, CharSequence charSequence, @IntRange(from = 0, to = 1) int duration) {
        ToastCompat toastCompat = ToastCompat.with();
        ToastInfo toastInfo = new ToastInfo(charSequence, duration);
        return new ToastCompat.IToast(toastCompat, toastInfo, () -> {
            toastCompat.mQueue.add(toastInfo);
            toastCompat.enqueueToast(context);
        });
    }

    public static IToast makeText(Context applicationContext, @StringRes int resource, @IntRange(from = 0, to = 1) int duration) {
        return makeText(applicationContext, applicationContext.getString(resource), duration);
    }

    public static class IToast {
        ToastCompat compat;
        ToastInfo toastInfo;
        Runnable runnable;

        public IToast(ToastCompat compat, ToastInfo toastInfo, Runnable runnable) {
            this.compat = compat;
            this.toastInfo = toastInfo;
            this.runnable = runnable;
        }

        public void show() {
            runnable.run();
        }


        public boolean cancel() {
            return compat.cancel(toastInfo);
        }
    }

    public static class ToastWrapper {
        MutableLiveData<ToastState> messenger = new MutableLiveData<>();

        public ToastWrapper(Context context) {
            messenger.setValue(ToastState.NONE);
            View toastview = LayoutInflater.from(context).inflate(R.layout.toast_text, new FrameLayout(context));
            toastview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            globalToast = new Toast(context);
            globalToast.setGravity(Gravity.TOP | Gravity.FILL_VERTICAL, 0, 0);
            globalToast.setView(toastview);
            toastview.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    messenger.setValue(ToastState.ATTACH);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    messenger.postValue(ToastState.DETACHED);
                }
            });
        }

        public void resetState() {
            messenger.setValue(ToastWrapper.ToastState.NONE);
        }

        public boolean isNone() {
            return (messenger.getValue() == ToastState.NONE);
        }

        Toast globalToast;

        public void showToast(ToastInfo info) {
            this.info = info;
            TextView textView = globalToast.getView().findViewById(R.id.text);
            textView.setText(info.info);
            if (ToastCompat.toastTypeFace != null){
               textView.setTypeface(ToastCompat.toastTypeFace);
            }
            globalToast.setDuration(info.duration);
            messenger.setValue(ToastState.INIT);
            globalToast.show();
        }

        public boolean cancel(ToastInfo info) {
            if (Objects.equals(info, this.info) && (messenger.getValue() == ToastState.ATTACH || messenger.getValue() == ToastState.INIT)) {
                globalToast.cancel();
                return true;
            }
            return false;
        }

        ToastInfo info;

        public enum ToastState {
            INIT, ATTACH, DETACHED, NONE
        }
    }


    private void enqueueToast(Context context) {
        if (getToastWrapper(context).isNone()) {
            if (mQueue.size() != 0) {
                ToastInfo info = mQueue.poll();
                assert info != null;
                getToastWrapper(context).messenger.observeForever(new Observer<ToastWrapper.ToastState>() {
                    @Override
                    public void onChanged(ToastWrapper.ToastState toastState) {
                        if (toastState == ToastWrapper.ToastState.DETACHED) {
                            getToastWrapper(context).messenger.removeObserver(this);
                            getToastWrapper(context).messenger.observeForever(new Observer<ToastWrapper.ToastState>() {
                                @Override
                                public void onChanged(ToastWrapper.ToastState toastState) {
                                    if (toastState == ToastWrapper.ToastState.NONE) {
                                        getToastWrapper(context).messenger.removeObserver(this);
                                        enqueueToast(context);
                                    }
                                }
                            });
                            getToastWrapper(context).messenger.postValue(ToastWrapper.ToastState.NONE);
                        }
                    }
                });
                getToastWrapper(context).showToast(info);
            }
        }

    }

    public static class ToastInfo {
        public ToastInfo(CharSequence info, int duration) {
            this.info = info;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return "ToastInfo{" +
                    "info=" + info +
                    ", duration=" + duration +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ToastInfo)) return false;
            ToastInfo toastInfo = (ToastInfo) o;
            return duration == toastInfo.duration &&
                    Objects.equals(info, toastInfo.info);
        }

        CharSequence info;
        int duration;
    }
}
