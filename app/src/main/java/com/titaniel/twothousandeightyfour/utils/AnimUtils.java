package com.titaniel.twothousandeightyfour.utils;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimUtils {

    public static void animateColorFilter(ImageView image, int color, long duration, long delay) {
        if(image == null) return;

        int startColor = Color.BLACK;
        if(image.getImageTintList() != null) startColor = image.getImageTintList().getDefaultColor();

        ObjectAnimator anim = ObjectAnimator.ofArgb(image, "colorFilter", startColor, color);
        anim.setStartDelay(delay);
        anim.setDuration(duration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    public static void animateTextColor(TextView text, int color, long duration, long delay) {
        if(text == null) return;

        ObjectAnimator anim = ObjectAnimator.ofArgb(text, "textColor", text.getTextColors().getDefaultColor(), color);
        anim.setStartDelay(delay);
        anim.setDuration(duration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    public static void animateAlpha(View view, TimeInterpolator interpolator, float alpha, long duration, long delay) {
        if(view == null) return;

        view.setVisibility(View.VISIBLE);
        view.animate()
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .alpha(alpha)
                .withEndAction(() -> {
                    if(alpha == 0) view.setVisibility(View.INVISIBLE);
                })
                .start();
    }

    public static void animateTranslationY(View view, TimeInterpolator interpolator, float translY, long duration, long delay) {
        if(view == null) return;

        view.animate()
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .translationY(translY)
                .start();
    }

    public static void animateTranslationX(View view, TimeInterpolator interpolator, float translX, long duration, long delay) {
        if(view == null) return;

        view.animate()
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .translationX(translX)
                .start();
    }

    public static void animateScaleX(View view, TimeInterpolator interpolator, float scaleX, long duration, long delay) {
        if(view == null) return;

        view.animate()
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .scaleX(scaleX)
                .start();
    }

    public static void animateScaleY(View view, TimeInterpolator interpolator, float scaleY, long duration, long delay) {
        if(view == null) return;

        view.animate()
                .setStartDelay(delay)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .scaleY(scaleY)
                .start();
    }

    public static void animateTextViewNumber(TextView text, int fromNumber, int toNumber, long duration) {
        ValueAnimator anim = ValueAnimator.ofInt(fromNumber, toNumber);
        anim.addUpdateListener(valueAnimator -> {
            text.setText(String.valueOf((int)valueAnimator.getAnimatedValue()));
        });
        anim.setDuration(duration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

}
