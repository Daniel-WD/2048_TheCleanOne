package com.titaniel.twothousandeightyfour.fragments.game;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.titaniel.twothousandeightyfour.MainActivity;
import com.titaniel.twothousandeightyfour.R;
import com.titaniel.twothousandeightyfour.database.Database;
import com.titaniel.twothousandeightyfour.fragments.AnimatedFragment;
import com.titaniel.twothousandeightyfour.fragments.Dialog;
import com.titaniel.twothousandeightyfour.utils.AnimUtils;
import com.titaniel.twothousandeightyfour.utils.Utils;

public class Game extends AnimatedFragment {

    private final static long CLOSE_POWER_UPS_DELAY = 300;

    private final static int MONEY_BACK = 10;
    private final static int MONEY_DELETE = 25;
    private final static int MONEY_EXCHANGE = 50;
    private final static int MONEY_DOUBLE = 70;

    private int mPUDisabledColor = Color.parseColor("#CBCBCB");

    private View mRoot;
    private ImageView mBtnPause;
    private TextView mTvScore;
    private TextView mTvPoints;
    private LinearLayout mLyPoints;
    private TextView mTvInstruction;
    private TouchArea mTouchArea;
    private View mVDivOne, mVDivTwo;
    private TextView mTvBackCount;
    private ConstraintLayout mLyBack;

    public GameField gameField;

    public boolean loadGame = false;

    private boolean mBackShown = false;
    private boolean mBlocking = false;

    private Runnable mDeblocker = () -> mBlocking = false;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //init
        mRoot = getView();
        mBtnPause = mRoot.findViewById(R.id.ivPause);
        mTvPoints = mRoot.findViewById(R.id.tvPoints);
        gameField = mRoot.findViewById(R.id.gameField);
        mTouchArea = mRoot.findViewById(R.id.touchArea);
        mVDivOne = mRoot.findViewById(R.id.vDivOne);
        mVDivTwo = mRoot.findViewById(R.id.vDivTwo);
        mTvBackCount = mRoot.findViewById(R.id.tvBackCount);
        mLyBack = mRoot.findViewById(R.id.lyBack);

        //back
        mLyBack.setOnClickListener(v -> {
            if(mBlocking) return;
            gameField.performBack();
            backClickAnim();
            block(500);
        });

        //touch area setup
        mTouchArea.gameField = gameField;

        //pause
        mBtnPause.setOnClickListener(v -> {
            mActivity.showDialog(0, Dialog.MODE_PAUSE);
            disableAll();
        });

        //gamefield listener
        gameField.setMoveListener(new GameField.MoveListener() {
            @Override
            public void onMoveCompleted() {
                if(!gameField.canUserMove()) {

                    handler.postDelayed(() -> {
                        if(Database.currentMode.backs > 0) {
                            mActivity.showDialog(300, Dialog.MODE_LOST_BACKABLE);
                        } else {
                            if(Utils.isOnline(getContext())) {
                                mActivity.showDialog(300, Dialog.MODE_LOST_VIDEO);
                            } else {
                                mActivity.showDialog(300, Dialog.MODE_LOST_NO_INTERNET);
                            }

                        }
                    }, 200);

                    disableAll();
                } else {
                    refreshBackState();
                }
            }

            @Override
            public void onMove(int direction) {
            }

            @Override
            public void onJoin(int newPoints) {
                // --> no animation --> see gamefield
                Database.currentMode.points += newPoints;
                updatePointsText();

                if(newPoints == 2048) {
                    mActivity.showDialog(300, Dialog.MODE_WON);
                    disableAll();
                }

                // TODO: 22.08.2018 optimize --> method in mode
                if(Database.currentMode.points > Database.currentMode.best) {
                    Database.currentMode.best = Database.currentMode.points;
                }
            }

            @Override
            public void onBackCompleted() {
                Database.currentMode.backs--;
                refreshBackState();
            }
        });

        //points reset
        updatePointsText();

        //hide back
        mLyBack.setVisibility(View.INVISIBLE);
    }

    private void refreshBackState() {

        if(Database.currentMode.backs == 0 || !gameField.canPerformBack()) {
            hideBack();
        } else {
            mTvBackCount.setText(String.valueOf(Database.currentMode.backs));
            showBack();
        }

    }

    private void showBack() {
        if(mBackShown) return;
        mBackShown = true;

        mLyBack.setVisibility(View.VISIBLE);
        mLyBack.setTranslationX(mLyBack.getWidth());
        mLyBack.setAlpha(1);
        AnimUtils.animateTranslationX(mLyBack, new DecelerateInterpolator(2), 0, 200, 0);

    }

    private void hideBack() {
        if(!mBackShown) return;
        mBackShown = false;

        AnimUtils.animateTranslationX(mLyBack, new AccelerateInterpolator(2), mLyBack.getWidth(), 200, 0);

    }

    private void backClickAnim() {
        if(!mBackShown) return;

        AnimUtils.animateTranslationX(mLyBack, new AccelerateInterpolator(), mLyBack.getWidth() * 0.1f, 150, 0);

        handler.postDelayed(() -> {
            AnimUtils.animateTranslationX(mLyBack, new DecelerateInterpolator(), 0, 150, 0);
        }, 150);

    }

    public void disableAll() {

        gameField.setEnabled(false);
        mBtnPause.setEnabled(false);
        mLyBack.setEnabled(false);

        long duration = 200;
        float alpha = 0.1f;

        AnimUtils.animateAlpha(gameField, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mTvPoints, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivOne, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivTwo, new AccelerateDecelerateInterpolator(), alpha, duration, 0);

        if(mLyBack.getVisibility() == View.VISIBLE) {
            AnimUtils.animateAlpha(mLyBack, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        }

        AnimUtils.animateAlpha(mBtnPause, new AccelerateDecelerateInterpolator(), 0, duration, 0);
    }

    public void enableAll() {

        gameField.setEnabled(true);
        mBtnPause.setEnabled(true);
        mLyBack.setEnabled(true);

        long duration = 200;
        float alpha = 1f;

        AnimUtils.animateAlpha(gameField, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mBtnPause, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mTvPoints, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivOne, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivTwo, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        if(mBackShown) AnimUtils.animateAlpha(mLyBack, new AccelerateDecelerateInterpolator(), alpha, duration, 0);

    }


    private void block(long duration) {
        mBlocking = true;
        handler.postDelayed(mDeblocker, duration);
    }

    public void restart() {
        enableAll();
        Database.currentMode.points = 0;
        Database.currentMode.backs = Database.START_BACK_VALUE;
        reset();
        refreshBackState();
        handler.postDelayed(() -> gameField.showStartTiles(), 500);
    }

    private void reset() {
        gameField.clear();
        updatePointsText();
        gameField.setFieldSize(Database.currentMode.fieldSize);
    }

    public void updatePointsText() {
        mTvPoints.setText(String.valueOf(Database.currentMode.points));
    }

    @Override
    protected void animateShow(long delay) {

        reset();

        mRoot.setVisibility(View.VISIBLE);

        mLyBack.setVisibility(View.INVISIBLE);
        mBackShown = false;

        long moveDuration = 700;

        //pause
        mBtnPause.setAlpha(0f);
        mBtnPause.setTranslationY(30);
        AnimUtils.animateAlpha(mBtnPause, new DecelerateInterpolator(2), 1, moveDuration, delay);
        AnimUtils.animateTranslationY(mBtnPause, new DecelerateInterpolator(2), 0, moveDuration, delay);

        delay += 150;

        //points
        mTvPoints.setAlpha(0f);
        mTvPoints.setTranslationY(30);
        AnimUtils.animateAlpha(mTvPoints, new DecelerateInterpolator(2), 1, moveDuration, delay);
        AnimUtils.animateTranslationY(mTvPoints, new DecelerateInterpolator(2), 0, moveDuration, delay);

        delay += 200;

        //point dividers
        long scaleDuration = 300;

        mVDivOne.setAlpha(0f);
        mVDivOne.setScaleX(0.1f);
        AnimUtils.animateAlpha(mVDivOne, new DecelerateInterpolator(2), 1, scaleDuration, delay);
        AnimUtils.animateScaleX(mVDivOne, new DecelerateInterpolator(2), 1, scaleDuration, delay);

        mVDivTwo.setAlpha(0f);
        mVDivTwo.setScaleX(0.1f);
        AnimUtils.animateAlpha(mVDivTwo, new DecelerateInterpolator(2), 1, scaleDuration, delay);
        AnimUtils.animateScaleX(mVDivTwo, new DecelerateInterpolator(2), 1, scaleDuration, delay);

        delay += 150;

        //gamefield
        gameField.setVisibility(View.INVISIBLE);

        handler.postDelayed(() -> {
            gameField.setVisibility(View.VISIBLE);
            gameField.animateIn();
        }, delay);

        delay += 500;

//        gameField.setTranslationY(50);
//        AnimUtils.animateAlpha(gameField, new DecelerateInterpolator(), 1, moveDuration, delay);
//        AnimUtils.animateTranslationY(gameField, new DecelerateInterpolator(), 0, moveDuration, delay);

        if(!loadGame) {
            handler.postDelayed(() -> gameField.showStartTiles(), delay);
        } else {
            handler.postDelayed(() -> {
                gameField.setSaveImageAndAnimate(Database.currentMode.saved);
            }, delay);

            delay += 400;

            handler.postDelayed(this::refreshBackState, delay);
        }

    }

    @Override
    protected long animateHide(long delay) {

        enableAll();
        mRoot.setVisibility(View.INVISIBLE);

        return 500;
    }

    public boolean onBackPressed() {
        mBtnPause.callOnClick();
        return true;
    }

}
