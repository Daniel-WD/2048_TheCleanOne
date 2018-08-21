package com.titaniel.twothousandeightyfour.fragments.achievements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.titaniel.twothousandeightyfour.R;
import com.titaniel.twothousandeightyfour.database.Database;
import com.titaniel.twothousandeightyfour.utils.AnimUtils;

import jp.wasabeef.blurry.Blurry;

class AchiAdapter extends RecyclerView.Adapter<AchiAdapter.AchiHolder> {

    private int mDisabledColor = Color.parseColor("#7F7F7F");
    private Drawable mDisabledBackground;
    private final Context mContext;

    private boolean mBlocking = false;
    private Runnable mDeblocker = () -> mBlocking = false;
    private Handler mHandler = new Handler();

    public AchiAdapter(Context context) {
        mContext = context;

        mDisabledBackground = context.getDrawable(R.drawable.bg_border_rect_disabled);
    }

    @NonNull
    @Override
    public AchiHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AchiHolder(LayoutInflater.from(mContext).inflate(R.layout.item_achievement, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AchiHolder achiHolder, int i) {
        achiHolder.tvTitle.setText(Database.achievements[i].title);
        achiHolder.tvDesc.setText(Database.achievements[i].description);
        achiHolder.tvMoney.setText(Database.achievements[i].money);
        achiHolder.setState(Database.achievements[i].state);
    }

    @Override
    public int getItemCount() {
        return Database.achievements.length;
    }

    private void block(long duration) {
        mBlocking = true;
        mHandler.postDelayed(mDeblocker, duration);
    }


    class AchiHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc;
        FrameLayout lyMoney;
        TextView tvMoney;
        ImageView ivCoin, ivLocked, ivShadow;
        View vBgDisabled;

        AchiHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            lyMoney = itemView.findViewById(R.id.lyMoney);
            tvMoney = itemView.findViewById(R.id.tvMoney);
            ivCoin = itemView.findViewById(R.id.ivCoin);
            ivLocked = itemView.findViewById(R.id.ivLocked);
            ivShadow = itemView.findViewById(R.id.ivShadow);
            vBgDisabled = itemView.findViewById(R.id.vBgDisabled);

            //button
            lyMoney.setOnClickListener(view -> {
                if(mBlocking || Database.achievements[getAdapterPosition()].state != Database.Achievement.STATE_GETABLE) return;
                block(200);
                performClick();
            });

            //shadow
            ivShadow.setImageResource(R.drawable.bg_solid_rect);
            ivShadow.post(() -> {
                Blurry.with(mContext)
                        .radius(10)
                        .sampling(4)
                        .capture(ivShadow)
                        .into(ivShadow);
                ivShadow.setScaleX(1.8f);
                ivShadow.setScaleY(1.8f);
            });

        }

        void performClick() {

            long duration = 150;

            ivShadow.animate()
                    .setDuration(duration)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .alpha(0)
                    .start();

            vBgDisabled.setVisibility(View.VISIBLE);
            vBgDisabled.setAlpha(0);
            vBgDisabled.animate()
                    .setDuration(duration)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .alpha(1)
                    .start();

            AnimUtils.animateColorFilter(ivCoin, mDisabledColor, duration, 0);
            AnimUtils.animateTextColor(tvMoney, mDisabledColor, duration, 0);

            Database.achievements[getAdapterPosition()].state = Database.Achievement.STATE_GOTTEN;
            Database.addMoney(Integer.parseInt(Database.achievements[getAdapterPosition()].money));
        }

        void setState(int state) {
            switch(state) {
                case Database.Achievement.STATE_GETABLE:
                    ivShadow.setVisibility(View.VISIBLE);
                    tvMoney.setVisibility(View.VISIBLE);
                    ivCoin.setVisibility(View.VISIBLE);
                    ivLocked.setVisibility(View.GONE);
                    vBgDisabled.setVisibility(View.INVISIBLE);

                    tvMoney.setTextColor(Color.WHITE);
                    ivCoin.setColorFilter(Color.WHITE);
                    break;

                case Database.Achievement.STATE_GOTTEN:
                    ivShadow.setVisibility(View.INVISIBLE);
                    tvMoney.setVisibility(View.VISIBLE);
                    ivCoin.setVisibility(View.VISIBLE);
                    ivLocked.setVisibility(View.GONE);
                    vBgDisabled.setVisibility(View.VISIBLE);

                    tvMoney.setTextColor(mDisabledColor);
                    ivCoin.setColorFilter(mDisabledColor);
                    break;

                case Database.Achievement.STATE_LOCKED:
                    ivShadow.setVisibility(View.VISIBLE);
                    tvMoney.setVisibility(View.GONE);
                    ivCoin.setVisibility(View.GONE);
                    ivLocked.setVisibility(View.VISIBLE);
                    vBgDisabled.setVisibility(View.INVISIBLE);

                    lyMoney.setBackgroundColor(Color.BLACK);
                    break;
            }
        }

    }

}
