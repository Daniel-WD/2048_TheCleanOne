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

import com.titaniel.twothousandeightyfour.MainActivity;
import com.titaniel.twothousandeightyfour.R;
import com.titaniel.twothousandeightyfour.database.Database;
import com.titaniel.twothousandeightyfour.utils.AnimUtils;

import jp.wasabeef.blurry.Blurry;

public class Dialog extends AnimatedFragment {

    public static final int MODE_LOST = 0;
    public static final int MODE_WON = 1;
    public static final int MODE_PAUSE = 2;

    private int mMode = 0;

    private View mRoot;
    private ImageView mIvBgShadow;

    private FrameLayout mLyContainer;
    private ConstraintLayout mLyWon, mLyLost, mLyPause;

    private TextView mTvWon;
    private Button mBtnWonHome, mBtnWonRestart, mBtnWonResume;

    private TextView mTvLost;
    private Button mBtnLostHome, mBtnLostRestart;

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

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mIvBgShadow = mRoot.findViewById(R.id.ivBgShadow);
        mLyWon = mRoot.findViewById(R.id.lyWon);
        mLyLost = mRoot.findViewById(R.id.lyLost);
        mLyPause = mRoot.findViewById(R.id.lyPause);
        mTvWon = mRoot.findViewById(R.id.tvWon);
        mTvLost = mRoot.findViewById(R.id.tvLost);
        mBtnWonHome = mRoot.findViewById(R.id.btnWonHome);
        mBtnLostHome = mRoot.findViewById(R.id.btnLostHome);
        mBtnWonRestart = mRoot.findViewById(R.id.btnWonRestart);
        mBtnLostRestart = mRoot.findViewById(R.id.btnLostRestart);
        mBtnWonResume = mRoot.findViewById(R.id.btnWonResume);
        mLyContainer = mRoot.findViewById(R.id.lyContainer);
        mBtnPauseBack = mRoot.findViewById(R.id.btnPauseBack);
        mBtnPauseHome = mRoot.findViewById(R.id.btnPauseHome);
        mBtnPauseRestart = mRoot.findViewById(R.id.btnPauseRestart);

        //make bg shadow
        mIvBgShadow.setScaleX(1);
        mIvBgShadow.setScaleY(1);
        mIvBgShadow.setImageResource(R.drawable.bg_solid_rect);
        mIvBgShadow.post(() -> {
            Blurry.with(getContext())
                    .radius(6)
                    .sampling(5)
                    .capture(mIvBgShadow)
                    .into(mIvBgShadow);
            if(mMode == MODE_WON) {
                mIvBgShadow.setTranslationY(0.01f);
                mIvBgShadow.setScaleX(2f);
                mIvBgShadow.setScaleY(1.36f);
            } else {
                mIvBgShadow.setTranslationY(0.01f);
                mIvBgShadow.setScaleX(2f);
                mIvBgShadow.setScaleY(1.5f);
            }
        });

        // ---------- WON ---------------------

        //won home
        mBtnWonHome.setOnClickListener(v -> {
            mActivity.hideDialog(0);
            mActivity.showHome(0, mActivity.game);
        });

        //won restart
        mBtnWonRestart.setOnClickListener(v -> {
            mActivity.game.restart();
            mActivity.hideDialog(0);
        });

        //won resume
        mBtnWonResume.setOnClickListener(v -> {
            mActivity.hideDialog(0);
            mActivity.game.enableAll();
        });

        // ---------- LOST ---------------------

        //lost home
        mBtnLostHome.setOnClickListener(v -> {
            Database.currentMode.saved = null;
            mActivity.hideDialog(0);
            mActivity.showHome(0, mActivity.game);
        });

        //lost restart
        mBtnLostRestart.setOnClickListener(v -> {
            Database.currentMode.saved = null;
            mActivity.game.restart();
            mActivity.hideDialog(0);
        });

        // ---------- PAUSE ---------------------

        //pause home
        mBtnPauseHome.setOnClickListener(v -> {
            Database.currentMode.saved = mActivity.game.gameField.getSaveImage();
            Database.currentMode.savedPoints = mActivity.game.points;
            mActivity.hideDialog(0);
            mActivity.showHome(0, mActivity.game);
        });

        //pause restart
        mBtnPauseRestart.setOnClickListener(v -> {
            mActivity.game.restart();
            mActivity.hideDialog(0);
        });

        //pause back
        mBtnPauseBack.setOnClickListener(v -> {
            mActivity.hideDialog(0);
            mActivity.game.enableAll();
        });

    }

    public void setMode(int mode) {
        mMode = mode;
        switch(mode) {
            case MODE_LOST:
                mLyLost.setVisibility(View.VISIBLE);
                mLyWon.setVisibility(View.GONE);
                mLyPause.setVisibility(View.GONE);
                break;
            case MODE_WON:
                mLyWon.setVisibility(View.VISIBLE);
                mLyLost.setVisibility(View.GONE);
                mLyPause.setVisibility(View.GONE);
                break;
            case MODE_PAUSE:
                mLyPause.setVisibility(View.VISIBLE);
                mLyLost.setVisibility(View.GONE);
                mLyWon.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void animateShow(long delay) {

        mRoot.setVisibility(View.VISIBLE);

        float diff = -mRoot.getHeight();
        long duration = 500;

        mLyContainer.setTranslationY(diff);
        mIvBgShadow.setTranslationY(diff);
        AnimUtils.animateTranslationY(mLyContainer, new DecelerateInterpolator(2), 0, duration, delay);
        AnimUtils.animateTranslationY(mIvBgShadow, new DecelerateInterpolator(2), 0, duration, delay);

    }

    @Override
    protected long animateHide(long delay) {

        float diff = mRoot.getHeight();
        long duration = 500;

        AnimUtils.animateTranslationY(mLyContainer, new AccelerateInterpolator(2), diff, duration, delay);
        AnimUtils.animateTranslationY(mIvBgShadow, new AccelerateInterpolator(2), diff, duration, delay);

        handler.postDelayed(() -> mRoot.setVisibility(View.INVISIBLE), 500);

        return 500;
    }
}
