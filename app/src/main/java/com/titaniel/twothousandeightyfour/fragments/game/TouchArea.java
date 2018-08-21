package com.titaniel.twothousandeightyfour.fragments.game;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TouchArea extends View {

    GameField gameField;

    public TouchArea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !gameField.inSelectMode && gameField.onTouchEvent(event);
    }

}
