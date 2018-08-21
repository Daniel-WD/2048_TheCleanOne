package com.titaniel.twothousandeightyfour.fragments.achievements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class AchiDecoration extends RecyclerView.ItemDecoration {

    private float mDividerThickness;
    private int mDividerColor = Color.parseColor("#d8d8d8");
    private Paint mPaint;

    private Context mContext;

    public AchiDecoration(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mDividerColor);

        mDividerThickness =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.getResources().getDisplayMetrics());

        mPaint.setStrokeWidth(mDividerThickness);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        for(int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            c.drawLine(0, child.getBottom(), child.getWidth(), child.getBottom(), mPaint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = (int) mDividerThickness;
    }
}
