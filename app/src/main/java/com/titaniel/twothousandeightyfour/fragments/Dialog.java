package com.titaniel.twothousandeightyfour.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.titaniel.twothousandeightyfour.MainActivity;
import com.titaniel.twothousandeightyfour.R;
import com.titaniel.twothousandeightyfour.admob.Admob;
import com.titaniel.twothousandeightyfour.database.Database;
import com.titaniel.twothousandeightyfour.utils.AnimUtils;

import jp.wasabeef.blurry.Blurry;

public class Dialog extends AnimatedFragment {

    private RewardedVideoAdListener mAdListener = new RewardedVideoAdListener() {

        boolean reward = false;

        @Override
        public void onRewardedVideoAdLoaded() {

        }

        @Override
        public void onRewardedVideoAdOpened() {

        }

        @Override
        public void onRewardedVideoStarted() {

        }

        @Override
        public void onRewardedVideoAdClosed() {
            handler.postDelayed(() -> {
                Database.currentMode.backs += 4;
                mBtnLostUndoBackable.callOnClick();
            }, 200);
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            reward = true;
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {

        }

        @Override
        public void onRewardedVideoCompleted() {

        }
    };

    public static final int MODE_LOST_NO_INTERNET = 0;
    public static final int MODE_WON = 1;
    public static final int MODE_PAUSE = 2;
    public static final int MODE_LOST_BACKABLE = 3;
    public static final int MODE_LOST_VIDEO = 4;

    private int mMode = 0;

    private View mRoot;
    private ImageView mIvBgShadow;

    private FrameLayout mLyContainer;
    private ConstraintLayout mLyWon, mLyLostNoInternet, mLyPause, mLyLostBackable, mLyLostVideo;

    //won
    private TextView mTvWon;
    private Button mBtnWonHome, mBtnWonRestart, mBtnWonResume;

    //lost no internet
    private TextView mTvLostNoInternet;
    private Button mBtnLostHomeNoInternet, mBtnLostRestartNoInternet;

    //lost backable
    private TextView mTvLostBackable;
    private Button mBtnLostHomeBackable, mBtnLostUndoBackable, mBtnLostRestartBackable;

    //lost video
    private TextView mTvLostVideo;
    private Button mBtnLostHomeVideo, mBtnLostNewBacksVideo, mBtnLostRestartVideo;

    //pause
    private Button mBtnPauseHome, mBtnPauseRestart, mBtnPauseBack;


    private boolean mBlocking = false;

    private Runnable mDeblocker = () -> mBlocking = false;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Admob.rewardedVideoAdListener = mAdListener;

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mIvBgShadow = mRoot.findViewById(R.id.ivBgShadow);
        mLyWon = mRoot.findViewById(R.id.lyWon);
        mLyLostNoInternet = mRoot.findViewById(R.id.lyLost);
        mLyPause = mRoot.findViewById(R.id.lyPause);
        mTvWon = mRoot.findViewById(R.id.tvWon);
        mTvLostNoInternet = mRoot.findViewById(R.id.tvLostNoInternet);
        mBtnWonHome = mRoot.findViewById(R.id.btnWonHome);
        mBtnLostHomeNoInternet = mRoot.findViewById(R.id.btnLostHomeNoInternet);
        mBtnWonRestart = mRoot.findViewById(R.id.btnWonRestart);
        mBtnLostRestartNoInternet = mRoot.findViewById(R.id.btnLostRestartNoInternet);
        mBtnWonResume = mRoot.findViewById(R.id.btnWonResume);
        mLyContainer = mRoot.findViewById(R.id.lyContainer);
        mBtnPauseBack = mRoot.findViewById(R.id.btnPauseBack);
        mBtnPauseHome = mRoot.findViewById(R.id.btnPauseHome);
        mBtnPauseRestart = mRoot.findViewById(R.id.btnPauseRestart);
        mBtnLostHomeBackable = mRoot.findViewById(R.id.btnLostHomeBackable);
        mBtnLostRestartBackable = mRoot.findViewById(R.id.btnLostRestartBackable);
        mBtnLostUndoBackable = mRoot.findViewById(R.id.btnLostUndoBackable);
        mLyLostBackable = mRoot.findViewById(R.id.lyLostBackable);
        mLyLostVideo = mRoot.findViewById(R.id.lyLostVideo);
        mBtnLostHomeVideo = mRoot.findViewById(R.id.btnLostHomeVideo);
        mBtnLostNewBacksVideo = mRoot.findViewById(R.id.btnLostNewBacksVideo);
        mBtnLostRestartVideo = mRoot.findViewById(R.id.btnLostRestartVideo);
        mTvLostVideo = mRoot.findViewById(R.id.tvLostVideo);


        // ---------- WON ---------------------

        //home
        mBtnWonHome.setOnClickListener(v -> {
            mActivity.hideDialog(0);
            mActivity.showHome(0, mActivity.game);
        });

        //restart
        mBtnWonRestart.setOnClickListener(v -> {
            mActivity.game.restart();
            mActivity.hideDialog(0);
        });

        //resume
        mBtnWonResume.setOnClickListener(v -> {
            mActivity.hideDialog(0);
            mActivity.game.enableAll();
        });


        // ---------- LOST NO INTERNET ---------------------

        //home
        mBtnLostHomeNoInternet.setOnClickListener(v -> {
            Database.currentMode.saved = null;
            mActivity.hideDialog(0);
            mActivity.showHome(0, mActivity.game);
        });

        //restart
        mBtnLostRestartNoInternet.setOnClickListener(v -> {
            Database.currentMode.saved = null;
            mActivity.game.restart();
            mActivity.hideDialog(0);
        });

        // ---------- LOST BACKABLE ---------------------

        //undo
        mBtnLostUndoBackable.setOnClickListener(v -> {
            mBtnPauseBack.callOnClick();

            handler.postDelayed(() -> {
                mActivity.game.gameField.performBack();
            }, 600);
        });

        //home
        mBtnLostHomeBackable.setOnClickListener(v -> {
            mActivity.hideDialog(0);
            mActivity.showHome(0, mActivity.game);
        });

        //restart
        mBtnLostRestartBackable.setOnClickListener(v -> {
            mBtnLostRestartNoInternet.callOnClick();
        });


        // ---------- LOST VIDEO ---------------------

        //new backs
        mBtnLostNewBacksVideo.setOnClickListener(v -> {

            // TODO: 23.08.2018 VIDEO

            if (Admob.rewardedVideoAd.isLoaded()) {
                Admob.rewardedVideoAd.show();
            }

        });

        //home
        mBtnLostHomeVideo.setOnClickListener(v -> {
            mBtnLostHomeNoInternet.callOnClick();
        });

        //restart
        mBtnLostRestartVideo.setOnClickListener(v -> {
            mBtnLostRestartNoInternet.callOnClick();
        });


        // ---------- PAUSE ---------------------

        //home
        mBtnPauseHome.setOnClickListener(v -> {
            Database.currentMode.saved = mActivity.game.gameField.getSaveImage();
            mActivity.hideDialog(0);
            mActivity.showHome(0, mActivity.game);
        });

        //restart
        mBtnPauseRestart.setOnClickListener(v -> {
            mActivity.game.restart();
            mActivity.hideDialog(0);
        });

        //back
        mBtnPauseBack.setOnClickListener(v -> {
            mActivity.hideDialog(0);

            handler.postDelayed(() -> {
                mActivity.game.enableAll();
            }, 350);
        });

    }

    private void makeShadow() {
        mIvBgShadow.setScaleX(1);
        mIvBgShadow.setScaleY(1);
        mIvBgShadow.setImageResource(R.drawable.bg_solid_rect);
        mIvBgShadow.post(() -> {
            Blurry.with(getContext())
                    .radius(6)
                    .sampling(5)
                    .capture(mIvBgShadow)
                    .into(mIvBgShadow);
            if(mMode == MODE_WON || mMode == MODE_LOST_BACKABLE || mMode == MODE_LOST_VIDEO) {
                mIvBgShadow.setTranslationY(mIvBgShadow.getTranslationY() + 0.01f);
                mIvBgShadow.setScaleX(2f);
                mIvBgShadow.setScaleY(1.36f);
            } else {
                mIvBgShadow.setTranslationY(mIvBgShadow.getTranslationY() + 0.01f);
                mIvBgShadow.setScaleX(2f);
                mIvBgShadow.setScaleY(1.5f);
            }
        });
    }

    public void setMode(int mode) {
        mMode = mode;
        switch(mode) {
            case MODE_LOST_NO_INTERNET:
                mLyLostNoInternet.setVisibility(View.VISIBLE);
                mLyLostVideo.setVisibility(View.GONE);
                mLyWon.setVisibility(View.GONE);
                mLyPause.setVisibility(View.GONE);
                mLyLostBackable.setVisibility(View.GONE);
                break;
            case MODE_WON:
                mLyWon.setVisibility(View.VISIBLE);
                mLyLostVideo.setVisibility(View.GONE);
                mLyLostNoInternet.setVisibility(View.GONE);
                mLyPause.setVisibility(View.GONE);
                mLyLostBackable.setVisibility(View.GONE);
                break;
            case MODE_PAUSE:
                mLyPause.setVisibility(View.VISIBLE);
                mLyLostVideo.setVisibility(View.GONE);
                mLyLostNoInternet.setVisibility(View.GONE);
                mLyWon.setVisibility(View.GONE);
                mLyLostBackable.setVisibility(View.GONE);
                break;
            case MODE_LOST_BACKABLE:
                mLyLostBackable.setVisibility(View.VISIBLE);
                mLyLostVideo.setVisibility(View.GONE);
                mLyPause.setVisibility(View.GONE);
                mLyLostNoInternet.setVisibility(View.GONE);
                mLyWon.setVisibility(View.GONE);
                break;
            case MODE_LOST_VIDEO:
                mLyLostVideo.setVisibility(View.VISIBLE);
                mLyLostBackable.setVisibility(View.GONE);
                mLyPause.setVisibility(View.GONE);
                mLyLostNoInternet.setVisibility(View.GONE);
                mLyWon.setVisibility(View.GONE);
                break;
        }
        makeShadow();
    }

    @Override
    protected void animateShow(long delay) {

        mRoot.setVisibility(View.VISIBLE);

        float diff = -mRoot.getHeight();
        long duration = 450;

        mLyContainer.setTranslationY(diff);
        mIvBgShadow.setTranslationY(diff);
        AnimUtils.animateTranslationY(mLyContainer, new DecelerateInterpolator(2), 0, duration, delay);
        AnimUtils.animateTranslationY(mIvBgShadow, new DecelerateInterpolator(2), 0, duration, delay);

    }

    @Override
    protected long animateHide(long delay) {

        float diff = mRoot.getHeight();
        long duration = 450;

        AnimUtils.animateTranslationY(mLyContainer, new AccelerateInterpolator(2), diff, duration, delay);
        AnimUtils.animateTranslationY(mIvBgShadow, new AccelerateInterpolator(2), diff, duration, delay);

        handler.postDelayed(() -> mRoot.setVisibility(View.INVISIBLE), 500);

        return 450;
    }
}
