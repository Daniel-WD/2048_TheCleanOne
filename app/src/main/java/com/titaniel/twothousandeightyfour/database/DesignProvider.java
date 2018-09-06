package com.titaniel.twothousandeightyfour.database;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;

import com.titaniel.twothousandeightyfour.R;

import java.util.ArrayList;

public class DesignProvider {

    public static int[] colors;

    public static class TileDesign {

        TileDesign(Drawable... drawables) {
            this.tileDrawables = drawables;
        }

        public Drawable[] tileDrawables;

    }

    public static ArrayList<TileDesign> tileDesigns = new ArrayList<>();

    public static void init(Context context) {

        colors = new int[24];

        //colors
        float[] firstColor = {282, 0.64f, 0.93f};

        for(int i = 0; i < 6; i++) {
            colors[i] = Color.HSVToColor(firstColor);
            firstColor[2] -= 0.1f;
        }

        float[] secondColor = {214, 0.70f, 0.84f};

        for(int i = 6; i < 10; i++) {
            colors[i] = Color.HSVToColor(secondColor);
            secondColor[2] -= 0.1f;
        }

        float[] thirdColor = {170, 0.99f, 0.79f};

        for(int i = 10; i < colors.length; i++) {
            colors[i] = Color.HSVToColor(thirdColor);
            thirdColor[2] -= 0.1f;
        }

        Drawable[] numbers = {
                context.getDrawable(R.drawable.num_0),
                context.getDrawable(R.drawable.num_1),
                context.getDrawable(R.drawable.num_2),
                context.getDrawable(R.drawable.num_3),
                context.getDrawable(R.drawable.num_4),
                context.getDrawable(R.drawable.num_5),
                context.getDrawable(R.drawable.num_6),
                context.getDrawable(R.drawable.num_7),
                context.getDrawable(R.drawable.num_8),
                context.getDrawable(R.drawable.num_9),
                context.getDrawable(R.drawable.num_10),
                context.getDrawable(R.drawable.num_11),
                context.getDrawable(R.drawable.num_12),
                context.getDrawable(R.drawable.num_13),
                context.getDrawable(R.drawable.num_14),
                context.getDrawable(R.drawable.num_15),
                context.getDrawable(R.drawable.num_16),
                context.getDrawable(R.drawable.num_17),
                context.getDrawable(R.drawable.num_18),
                context.getDrawable(R.drawable.num_19),
                context.getDrawable(R.drawable.num_20),
                context.getDrawable(R.drawable.num_21),
                context.getDrawable(R.drawable.num_22)
        };

        tileDesigns.add(new TileDesign(numbers));

        colorize();
    }

    public static void setSize(int size) {
        for(TileDesign tileDesign : tileDesigns) {
            for(Drawable drawable : tileDesign.tileDrawables) {
                drawable.setBounds(0, 0, size, size);
            }
        }
    }

    private static void colorize() {

        for(TileDesign tileDesign : tileDesigns) {
            Drawable[] tileDrawables = tileDesign.tileDrawables;
            for(int i = 0; i < tileDrawables.length; i++) {
                tileDrawables[i].setTint(colors[i]);
            }
        }

    }

}
