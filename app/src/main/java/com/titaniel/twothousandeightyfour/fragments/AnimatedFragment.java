package com.titaniel.twothousandeightyfour.fragments;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

public abstract class AnimatedFragment extends Fragment {

    protected boolean isActive = false;

    protected final Handler handler = new Handler();

    public final void show(long delay) {
        isActive = true;
        animateShow(delay);
    }

    public final long hide(long delay) {
        isActive = false;
        return animateHide(delay);
    }

    protected abstract void animateShow(long delay);
    protected abstract long animateHide(long delay);

//    public View[] ignoredViewsVisibility() {
//        return null;
//    }

}
