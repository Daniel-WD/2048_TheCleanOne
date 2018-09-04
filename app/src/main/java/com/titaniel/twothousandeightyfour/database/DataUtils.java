package com.titaniel.twothousandeightyfour.database;

import com.titaniel.twothousandeightyfour.fragments.game.GameField;

import java.util.ArrayList;

public class DataUtils {

    static String twoDimIntArrayToString(int[][] array) {
        if(array == null) return null;
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < array.length; i++) {
            res.append(":");
            for(int j = 0; j < array[i].length; j++) {
                res.append("$");
                res.append(array[i][j]);
                res.append("%");
            }
            res.append(":");
        }

        return res.toString();
    }

    static int[][] stringToTwoDimIntArray(final String string) {
        if(string == null) return null;

        int firstDimCount = (string.length() - string.replace(":", "").length())/2;
        int secDimCount = (string.length() - string.replace("$", "").length())/firstDimCount;

        StringBuilder builder = new StringBuilder(string);

        int[][] res = new int[firstDimCount][secDimCount];

        for(int i = 0; i < res.length; i++) {
            for(int j = 0; j < res[i].length; j++) {
                int index = builder.indexOf("$");
                builder.replace(0, index+1, "");
                index = builder.indexOf("%");
                int number = Integer.parseInt(builder.substring(0, index));
                res[i][j] = number;
            }
        }

        return res;
    }

    static String imageToString(ArrayList<GameField.FieldImage> img) {
        if(img == null || img.size() == 0) return "";
        StringBuilder result = new StringBuilder();

        for(GameField.FieldImage image : img) {
            result.append("(");
            result.append(twoDimIntArrayToString(image.image));
            result.append(")");
        }

        return result.toString();
    }

    static ArrayList<GameField.FieldImage> stringToImage(String string) {
        if(string == null) return null;
        ArrayList<GameField.FieldImage> result = new ArrayList<>();

        StringBuilder builder = new StringBuilder(string);

        while(builder.toString().contains("(")) {
            int dexPlus = builder.indexOf("(");
            int dexMinus = builder.indexOf(")");
            String sub = builder.substring(dexPlus+1, dexMinus);
            result.add(new GameField.FieldImage(stringToTwoDimIntArray(sub)));

            builder.replace(0, dexMinus+1, "");
        }

        return result;
    }

}
