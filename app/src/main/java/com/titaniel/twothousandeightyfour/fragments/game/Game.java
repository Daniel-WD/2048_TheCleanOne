package com.titaniel.twothousandeightyfour.fragments.game;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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

    public GameField gameField;

    public boolean loadGame = false;

    public int points = 0;

    private boolean mPUShown = false;
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
        mTvScore = mRoot.findViewById(R.id.tvScore);
        mTvPoints = mRoot.findViewById(R.id.tvPoints);
        mLyPoints = mRoot.findViewById(R.id.lyPoints);
        mTvInstruction = mRoot.findViewById(R.id.tvInstruction);
        gameField = mRoot.findViewById(R.id.gameField);
        mTouchArea = mRoot.findViewById(R.id.touchArea);
        mVDivOne = mRoot.findViewById(R.id.vDivOne);
        mVDivTwo = mRoot.findViewById(R.id.vDivTwo);

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
                    mActivity.showDialog(300, Dialog.MODE_LOST);
                    disableAll();
//                    Log.e("djsfl", "lost");
                }
            }

            @Override
            public void onMove(int direction) {

            }

            @Override
            public void onJoin(int newPoints) {
                // --> no animation --> see gamefield
                points += newPoints;
                updatePointsText();

                if(newPoints == 2048) {
                    mActivity.showDialog(300, Dialog.MODE_WON);
                    disableAll();
                }

                if(points > Database.currentMode.best) {
                    Database.currentMode.best = points;
                }
            }
        });

        //points reset
        mTvPoints.setText(String.valueOf(points));

    }


    public void disableAll() {

        gameField.setEnabled(false);
        mBtnPause.setEnabled(false);

        long duration = 200;
        float alpha = 0.1f;

        AnimUtils.animateAlpha(gameField, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mBtnPause, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mTvPoints, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivOne, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivTwo, new AccelerateDecelerateInterpolator(), alpha, duration, 0);

        AnimUtils.animateAlpha(mBtnPause, new AccelerateDecelerateInterpolator(), 0, duration, 0);
    }

    public void enableAll() {

        gameField.setEnabled(true);
        mBtnPause.setEnabled(true);

        long duration = 200;
        float alpha = 1f;

        AnimUtils.animateAlpha(gameField, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mBtnPause, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mTvPoints, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivOne, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mVDivTwo, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
        AnimUtils.animateAlpha(mBtnPause, new AccelerateDecelerateInterpolator(), alpha, duration, 0);
    }


    private void block(long duration) {
        mBlocking = true;
        handler.postDelayed(mDeblocker, duration);
    }

    public void restart() {
        enableAll();
        reset();
        handler.postDelayed(() -> gameField.showStartTiles(), 500);
    }

    private void reset() {
        gameField.clear();
        points = 0;
        mTvPoints.setText("0");
        gameField.setFieldSize(Database.currentMode.fieldSize);
    }

    public void updatePointsText() {
        mTvPoints.setText(String.valueOf(points));
    }

    @Override
    protected void animateShow(long delay) {

        reset();

        mRoot.setVisibility(View.VISIBLE);

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
        long scaleDuration = 350;

        mVDivOne.setAlpha(0f);
        mVDivOne.setScaleX(0.1f);
        AnimUtils.animateAlpha(mVDivOne, new DecelerateInterpolator(2), 1, scaleDuration, delay);
        AnimUtils.animateScaleX(mVDivOne, new DecelerateInterpolator(2), 1, scaleDuration, delay);

        mVDivTwo.setAlpha(0f);
        mVDivTwo.setScaleX(0.1f);
        AnimUtils.animateAlpha(mVDivTwo, new DecelerateInterpolator(2), 1, scaleDuration, delay);
        AnimUtils.animateScaleX(mVDivTwo, new DecelerateInterpolator(2), 1, scaleDuration, delay);

        delay += 250;

        //gamefield
        gameField.setVisibility(View.INVISIBLE);

        handler.postDelayed(() -> {
            gameField.setVisibility(View.VISIBLE);
            gameField.animateIn();
        }, delay);

        delay += 750;

//        gameField.setTranslationY(50);
//        AnimUtils.animateAlpha(gameField, new DecelerateInterpolator(), 1, moveDuration, delay);
//        AnimUtils.animateTranslationY(gameField, new DecelerateInterpolator(), 0, moveDuration, delay);



        if(!loadGame) {
            handler.postDelayed(() -> gameField.showStartTiles(), delay);
        } else {
            handler.postDelayed(() -> {
                gameField.setSaveImage(Database.currentMode.saved);
                points = Database.currentMode.savedPoints;
                updatePointsText();
            }, delay);
        }

        mActivity.animateBgScale(1.4f, 0, 1000);

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
