package com.titaniel.twothousandeightyfour.fragments.achievements;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.titaniel.twothousandeightyfour.MainActivity;
import com.titaniel.twothousandeightyfour.R;
import com.titaniel.twothousandeightyfour.database.Database;
import com.titaniel.twothousandeightyfour.fragments.AnimatedFragment;
import com.titaniel.twothousandeightyfour.utils.AnimUtils;

import java.util.Random;

public class Achievements extends AnimatedFragment {

    private View mRoot;
    private TextView mTvAchievements;
    private ImageView mBtnClose;
    private View mVBarDivider, mListDivider;
    private LinearLayout mLyMoney;
    private TextView mTvMoney;
    private ImageView mIvCoin;
    private View mVBlockLeft, mVBlockRight;
    private RecyclerView mAchiList;
    private ConstraintLayout mLyHeader;

    private LinearLayoutManager mAchiLayoutManager;
    private AchiAdapter mAchiAdapter;

    private float mHeaderElevationPixels;

    private MainActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mActivity = (MainActivity) getActivity();

        //moneylistener
        Database.addMoneyListener((oldValue, newValue) -> {

            AnimUtils.animateTextViewNumber(mTvMoney, oldValue, newValue, 200);

        });

        //header elevation
        mHeaderElevationPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

        //init
        mRoot = getView();
        mTvAchievements = mRoot.findViewById(R.id.tvAchievements);
        mBtnClose = mRoot.findViewById(R.id.ivClose);
        mVBarDivider = mRoot.findViewById(R.id.vBarDivider);
        mLyMoney = mRoot.findViewById(R.id.lyMoney);
        mTvMoney = mRoot.findViewById(R.id.tvMoney);
        mIvCoin = mRoot.findViewById(R.id.ivCoin);
        mVBlockLeft = mRoot.findViewById(R.id.vBlockLeft);
        mVBlockRight = mRoot.findViewById(R.id.vBlockRight);
        mAchiList = mRoot.findViewById(R.id.listAchi);
        mLyHeader = mRoot.findViewById(R.id.lyHeader);
        mListDivider = mRoot.findViewById(R.id.vListTopDivider);

        mBtnClose.setOnClickListener(v -> {
            mActivity.showHome(0, this);
        });

        //achilist
        mAchiLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAchiAdapter = new AchiAdapter(getContext());
        mAchiList.setLayoutManager(mAchiLayoutManager);
        mAchiList.setAdapter(mAchiAdapter);
        mAchiList.addItemDecoration(new AchiDecoration(getContext()));
        mAchiList.setHasFixedSize(true);

        mAchiList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean lastUp = true;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Random r = new Random();
                if(lastUp && recyclerView.computeVerticalScrollOffset() == 0) {
                    moveHeader(false);
                    lastUp = false;
                } else if(!lastUp) {
                    moveHeader(true);
                    lastUp = true;
                }
            }
        });

        //money
        mTvMoney.setText(String.valueOf(Database.getMoney()));

    }

    private void moveHeader(boolean up) {

        mLyHeader.animate()
                .setStartDelay(0)
                .setDuration(50)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .translationZ(up ? mHeaderElevationPixels : 0)
                .start();

        mListDivider.animate()
                .setStartDelay(50)
                .setDuration(50)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .alpha(up ? 0 : 1)
                .start();

    }

    @Override
    protected void animateShow(long delay) {

        mRoot.setVisibility(View.VISIBLE);
        mRoot.setAlpha(0);
        mRoot.animate()
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(200)
                .alpha(1)
                .start();

    }

    @Override
    protected long animateHide(long delay) {

        mRoot.animate()
                .setStartDelay(delay)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(200)
                .alpha(0)
                .withEndAction(() -> mRoot.setVisibility(View.INVISIBLE))
                .start();

        return 200;
    }
}
