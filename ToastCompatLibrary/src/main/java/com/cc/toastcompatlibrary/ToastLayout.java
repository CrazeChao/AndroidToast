package com.cc.toastcompatlibrary;

import android.content.Context;
 import android.util.AttributeSet;
 import android.view.View;
 import android.view.ViewTreeObserver;
 import android.view.animation.OvershootInterpolator;
 import android.widget.FrameLayout;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;

 /**
 * Created by lizhichao on 6/4/21
 */
public   class ToastLayout extends FrameLayout {
     public ToastLayout(@NonNull Context context) {
         super(context);
     }

     public ToastLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
         super(context, attrs);
     }

     public ToastLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
         super(context, attrs, defStyleAttr);
     }

     public ToastLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
         super(context, attrs, defStyleAttr, defStyleRes);
     }

     View mChildView;
     @Override
     protected void onFinishInflate() {
         super.onFinishInflate();
         mChildView = getChildAt(0);
     }

     @Override
     protected void onAttachedToWindow() {
         super.onAttachedToWindow();
         mChildView.setTranslationY(-(64+82));
         mChildView.animate().translationY(0).setInterpolator(new OvershootInterpolator()).start();
     }
 }