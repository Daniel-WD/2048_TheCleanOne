package com.titaniel.twothousandeightyfour.fragments;

import android.animation.TimeInterpolator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.titaniel.twothousandeightyfour.MainActivity;
import com.titaniel.twothousandeightyfour.R;
import com.titaniel.twothousandeightyfour.database.Database;
import com.titaniel.twothousandeightyfour.utils.AnimUtils;

import jp.wasabeef.blurry.Blurry;

public class Home extends AnimatedFragment {

    private int mChevDisabledColor = Color.parseColor("#CBCBCB");

    private View mRoot;
    private Button mBtnPlay, mBtnResume, mBtnRate;
    private TextView mTvMode, mTvModeBest;
    private ImageView mIvLogo;
    private ImageView mBtnPlus, mBtnMinus;
    private LinearLayout mLyMode;
    private ImageView mIvBtnsShadow;
    private LinearLayout mLyButtons;
    private ImageView mIvCut;

    private View mVDivOne, mVDivTwo;

    private MainActivity mActivity;

    private boolean mBlocking = false;

    private Runnable mDeblocker = () -> mBlocking = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //moneylistener
        //Database.addMoneyListener((oldValue, newValue) -> mTvMoney.setText(String.valueOf(newValue)));

        //init
        mRoot = getView();
        mBtnPlay = mRoot.findViewById(R.id.btnPlay);
        mBtnResume = mRoot.findViewById(R.id.btnResume);
        mIvLogo = mRoot.findViewById(R.id.ivLogo);
        mBtnPlus = mRoot.findViewById(R.id.btnModeNext);
        mBtnMinus = mRoot.findViewById(R.id.btnModePrevious);
        mLyMode = mRoot.findViewById(R.id.lyMode);
        mBtnRate = mRoot.findViewById(R.id.btnRate);
        mBtnRate = mRoot.findViewById(R.id.btnRate);
        mTvMode = mRoot.findViewById(R.id.tvMode);
        mTvModeBest = mRoot.findViewById(R.id.tvModeBest);
        mIvBtnsShadow = mRoot.findViewById(R.id.ivBtnsShadow);
        mLyButtons = mRoot.findViewById(R.id.lyButtons);
        mIvCut = mRoot.findViewById(R.id.ivCut);
        mVDivOne = mRoot.findViewById(R.id.vDivOne);
        mVDivTwo = mRoot.findViewById(R.id.vDivTwo);

        //achi
//        mBtnAchievements.setOnClickListener(v -> {
//            mActivity.showAchievements(0, this);
//        });

        //rate
        mBtnRate.setOnClickListener(v -> {
            if(mBlocking) return;
            Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
            }
        });

        //resume
        mBtnResume.setOnClickListener(v -> {
            if(mBlocking) return;
            mActivity.game.loadGame = true;
            mActivity.showGame(0, this);
        });

        //play
        mBtnPlay.setOnClickListener(v -> {
            if(mBlocking) return;
            Database.currentMode.points = 0;
            Database.currentMode.backs = Database.START_BACK_VALUE;
            mActivity.game.loadGame = false;
            mActivity.showGame(0, this);
        });

        //money
//        mTvMoney.setText(String.valueOf(Database.getMoney()));

        //shadow play
        mIvBtnsShadow.setScaleX(1);
        mIvBtnsShadow.setScaleY(1);
        mIvBtnsShadow.setImageResource(R.drawable.bg_solid_rect);
        mIvBtnsShadow.post(() -> {
            Blurry.with(getContext())
                    .radius(6)
                    .sampling(5)
                    .capture(mIvBtnsShadow)
                    .into(mIvBtnsShadow);
            mIvBtnsShadow.setTranslationY(2.5f);
            mIvBtnsShadow.setScaleX(2f);
            mIvBtnsShadow.setScaleY(1.5f);
        });

        //shadow resume
//        mIvResumeShadow.post(() -> {
//            Blurry.with(getContext())
//                    .radius(3)
//                    .color(Color.BLACK)
//                    .sampling(7)
//                    .async()
//                    .capture(mBtnResume)
//                    .into(mIvResumeShadow);
//        });

        //shadow achdot
//        mIvAchDotShadow.post(() -> {
//            Blurry.with(getContext())
//                    .radius(3)
//                    .color(Color.BLACK)
//                    .sampling(6)
//                    .async()
//                    .capture(mIvAchDot)
//                    .into(mIvAchDotShadow);
//        });

        //Button changeMode
        mBtnPlus.setOnClickListener(view -> {
            if(mBlocking) return;
            changeMode(false);
        });

        //Button previousMode
        mBtnMinus.setOnClickListener(view -> {
            if(mBlocking) return;
            changeMode(true);
        });

    }

    private void setMode(Database.Mode mode) {
        if(mode == null) return;
        mTvMode.setText(mode.representative);
        mTvModeBest.setText(getString(R.string.temp_best, mode.record));
    }

    private void changeMode(boolean previous) {
        if(mBlocking || previous ? !Database.hasPreviousMode() : !Database.hasNextMode()) return;

        block(350);

        Database.Mode mode = previous ? Database.previousMode() : Database.nextMode();
        Database.currentMode = mode;
        checkPeriph();

        long delay = 0;
        float dist = -20;

        float realDist = previous ? dist : -dist;

        AnimUtils.animateAlpha(mLyMode, new AccelerateDecelerateInterpolator(), 0, 150, delay);
        AnimUtils.animateTranslationY(mLyMode, new AccelerateInterpolator(), realDist, 150, delay);

        delay += 200;

        handler.postDelayed(() -> {
            setMode(mode);

            mLyMode.setTranslationY(-realDist);

            AnimUtils.animateAlpha(mLyMode, new AccelerateDecelerateInterpolator(), 1, 150, 0);
            AnimUtils.animateTranslationY(mLyMode, new DecelerateInterpolator(), 0, 150, 0);

        }, delay);

    }

    private void checkPeriph() {
        if(Database.hasPreviousMode()) {
            AnimUtils.animateColorFilter(mBtnMinus, Color.BLACK, 150, 0);
        } else {
            AnimUtils.animateColorFilter(mBtnMinus, mChevDisabledColor, 150, 0);
        }
        if(Database.hasNextMode()) {
            AnimUtils.animateColorFilter(mBtnPlus, Color.BLACK, 150, 0);
        } else {
            AnimUtils.animateColorFilter(mBtnPlus, mChevDisabledColor, 150, 0);
        }
        if(Database.currentMode.saved != null) {
            AnimUtils.animateAlpha(mBtnResume, new AccelerateDecelerateInterpolator(), 1, 250, 0);
            mBtnResume.setEnabled(true);
//            AnimUtils.animateAlpha(mIvResumeShadow, 1, 200, 0);
        } else {
            AnimUtils.animateAlpha(mBtnResume, new AccelerateDecelerateInterpolator(), 0.1f, 250, 0);
            mBtnResume.setEnabled(false);
//            AnimUtils.animateAlpha(mIvResumeShadow, 0, 200, 0);
        }
    }

    private void block(long duration) {
        mBlocking = true;
        handler.postDelayed(mDeblocker, duration);
    }


    @Override
    protected void animateShow(long delay) {

        block(1400);

        setMode(Database.currentMode);
//        checkPeriph();

        mRoot.setVisibility(View.VISIBLE);

        long moveDuration = 800;

        //cut
        mIvCut.setAlpha(0f);
        AnimUtils.animateAlpha(mIvCut, new AccelerateDecelerateInterpolator(), 1, moveDuration, delay);

//        delay += 70;

        //logo
        mIvLogo.setAlpha(0f);
        mIvLogo.setTranslationY(30);
        AnimUtils.animateAlpha(mIvLogo, new DecelerateInterpolator(2f), 1, moveDuration, delay);
        AnimUtils.animateTranslationY(mIvLogo, new DecelerateInterpolator(2f), 0, moveDuration, delay);

        delay += 90;

        //mode ly
        mLyMode.setAlpha(0f);
        mLyMode.setTranslationY(30);
        AnimUtils.animateAlpha(mLyMode, new DecelerateInterpolator(2f), 1, moveDuration, delay);
        AnimUtils.animateTranslationY(mLyMode, new DecelerateInterpolator(2f), 0, moveDuration, delay);

        //minus plus

        //minus fade and move
        mBtnMinus.setAlpha(0f);
        mBtnMinus.setTranslationY(30);
        AnimUtils.animateAlpha(mBtnMinus, new DecelerateInterpolator(2f), 1, moveDuration, delay);
        AnimUtils.animateTranslationY(mBtnMinus, new DecelerateInterpolator(2f), 0, moveDuration, delay);

        //plus fade and move
        mBtnPlus.setAlpha(0f);
        mBtnPlus.setTranslationY(30);
        AnimUtils.animateAlpha(mBtnPlus, new DecelerateInterpolator(2f), 1, moveDuration, delay);
        AnimUtils.animateTranslationY(mBtnPlus, new DecelerateInterpolator(2f), 0, moveDuration, delay);

        //button ly

        delay += 50;

        long dur = 800;

        float diff = 60;
        mLyButtons.setScaleY(1);
        mLyButtons.setTranslationY(diff);
        AnimUtils.animateTranslationY(mLyButtons, new DecelerateInterpolator(2), 0, dur, delay);


        mIvBtnsShadow.setTranslationY(diff);
        AnimUtils.animateTranslationY(mIvBtnsShadow, new DecelerateInterpolator(2), 0, dur, delay);

        mIvBtnsShadow.setScaleY(1.5f);
        mIvBtnsShadow.setAlpha(0f);
        AnimUtils.animateAlpha(mIvBtnsShadow, new DecelerateInterpolator(2), 1, dur, delay);

        delay += 250;

        //buttons
        long buttonDuration = 800;
        float buttonsDist = 0;

        mBtnPlay.setAlpha(0);
//        mBtnPlay.setTranslationY(buttonsDist);
        AnimUtils.animateAlpha(mBtnPlay, new AccelerateDecelerateInterpolator(), 1, buttonDuration, delay);
//        AnimUtils.animateTranslationY(mBtnPlay, new DecelerateInterpolator(), 0, buttonDuration, delay);

        delay += 60;

        mBtnResume.setAlpha(0);
//        mBtnResume.setTranslationY(buttonsDist);
        AnimUtils.animateAlpha(mBtnResume, new AccelerateDecelerateInterpolator(), 1, buttonDuration, delay);
//        AnimUtils.animateTranslationY(mBtnResume, new DecelerateInterpolator(), 0, buttonDuration, delay);

        delay += 60;

        mBtnRate.setAlpha(0);
//        mBtnRate.setTranslationY(buttonsDist);
        AnimUtils.animateAlpha(mBtnRate, new AccelerateDecelerateInterpolator(), 1, buttonDuration, delay);
//        AnimUtils.animateTranslationY(mBtnRate, new DecelerateInterpolator(), 0, buttonDuration, delay);


        delay += 300;

        mVDivOne.setScaleX(0.1f);
        mVDivOne.setAlpha(0);
//        mVDivOne.setTranslationY(buttonsDist);
        AnimUtils.animateScaleX(mVDivOne, new DecelerateInterpolator(2), 1, buttonDuration, delay);
        AnimUtils.animateAlpha(mVDivOne, new DecelerateInterpolator(2), 1, 150, delay);
//        AnimUtils.animateTranslationY(mVDivOne, new DecelerateInterpolator(), 0, buttonDuration, delay);

        delay += 0;

        mVDivTwo.setScaleX(0.1f);
        mVDivTwo.setAlpha(0);
//        mVDivTwo.setTranslationY(buttonsDist);
        AnimUtils.animateScaleX(mVDivTwo, new DecelerateInterpolator(2), 1, buttonDuration, delay);
        AnimUtils.animateAlpha(mVDivTwo, new DecelerateInterpolator(2), 1, 150, delay);
//        AnimUtils.animateTranslationY(mVDivTwo, new DecelerateInterpolator(), 0, buttonDuration, delay);

//        delay += 250;

        handler.postDelayed(this::checkPeriph, delay);
    }

    /*@Override
    protected long animateHide(long delay) {
        float diff = -50;

        long moveDuration = 180;

        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();

        //cut
        AnimUtils.animateAlpha(mIvCut, interpolator, 0, moveDuration, delay);
        AnimUtils.animateTranslationY(mIvCut, interpolator, diff, moveDuration, delay);

        //logo
        AnimUtils.animateAlpha(mIvLogo, interpolator, 0, moveDuration, delay);
        AnimUtils.animateTranslationY(mIvLogo, interpolator, diff, moveDuration, delay);

        //mode ly
        AnimUtils.animateAlpha(mLyMode, interpolator, 0, moveDuration, delay);
        AnimUtils.animateTranslationY(mLyMode, interpolator, diff, moveDuration, delay);

        //minus plus
        AnimUtils.animateAlpha(mBtnMinus, interpolator, 0, moveDuration, delay);
        AnimUtils.animateTranslationY(mBtnMinus, interpolator, diff, moveDuration, delay);

        AnimUtils.animateAlpha(mBtnPlus, interpolator, 0, moveDuration, delay);
        AnimUtils.animateTranslationY(mBtnPlus, interpolator, diff, moveDuration, delay);

        diff = -80;
        moveDuration = 200;

        //button ly
        AnimUtils.animateTranslationY(mLyButtons, interpolator, diff, moveDuration, delay);

        AnimUtils.animateTranslationY(mIvBtnsShadow, interpolator, diff, moveDuration, delay);
        AnimUtils.animateAlpha(mIvBtnsShadow, interpolator, 0, moveDuration, delay);


        //buttons
        AnimUtils.animateAlpha(mBtnPlay, interpolator, 0, moveDuration, delay);

        AnimUtils.animateAlpha(mBtnResume, interpolator, 0, moveDuration, delay);

        AnimUtils.animateAlpha(mBtnRate, interpolator, 0, moveDuration, delay);

        AnimUtils.animateAlpha(mVDivOne, interpolator, 0, moveDuration, delay);

        AnimUtils.animateAlpha(mVDivTwo, interpolator, 0, moveDuration, delay);

        delay += 1400;

        handler.postDelayed(() -> mRoot.setVisibility(View.INVISIBLE), delay);

        return 500;
    }*/

    @Override
    protected long animateHide(long delay) {

        long dur = 100;

        //cut
        AnimUtils.animateAlpha(mIvCut, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        //logo
        AnimUtils.animateAlpha(mIvLogo, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        //mode ly
        AnimUtils.animateAlpha(mLyMode, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        //minus plus

        //minus fade and move
        AnimUtils.animateAlpha(mBtnMinus, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        //plus fade and move
        AnimUtils.animateAlpha(mBtnPlus, new AccelerateDecelerateInterpolator(), 0, dur, delay);


        //buttons

        AnimUtils.animateAlpha(mBtnPlay, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        AnimUtils.animateAlpha(mBtnResume, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        AnimUtils.animateAlpha(mBtnRate, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        AnimUtils.animateAlpha(mVDivOne, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        AnimUtils.animateAlpha(mVDivTwo, new AccelerateDecelerateInterpolator(), 0, dur, delay);

        delay += 100;

        //ly buttons
        long duration = 200;
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();

        float newScale = (float) mRoot.getHeight()/(float) mLyButtons.getHeight();

        AnimUtils.animateScaleY(mLyButtons, interpolator, newScale, duration, delay);
        AnimUtils.animateScaleY(mIvBtnsShadow, interpolator, newScale*1.5f, duration, delay);

        float translY = mRoot.getHeight()/2-mLyButtons.getHeight()/2 - mLyButtons.getY();

        AnimUtils.animateTranslationY(mLyButtons, interpolator, translY, duration, delay);
        AnimUtils.animateTranslationY(mIvBtnsShadow, interpolator, translY, duration, delay);

        //AnimUtils.animateAlpha(mIvBtnsShadow, interpolator, 0, duration, delay);

        delay += 400;

        handler.postDelayed(() -> mRoot.setVisibility(View.INVISIBLE), delay);

        return 300;
    }

}
