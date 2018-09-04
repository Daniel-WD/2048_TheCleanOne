package com.titaniel.twothousandeightyfour.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public static int digitCount(int number) {
        return String.valueOf(number).length();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static int idToNum(int id) {
        switch(id) {
            case 0: return (int) Math.pow(2, 1);
            case 1: return (int) Math.pow(2, 2);
            case 2: return (int) Math.pow(2, 3);
            case 3: return (int) Math.pow(2, 4);
            case 4: return (int) Math.pow(2, 5);
            case 5: return (int) Math.pow(2, 6);
            case 6: return (int) Math.pow(2, 7);
            case 7: return (int) Math.pow(2, 8);
            case 8: return (int) Math.pow(2, 9);
            case 9: return (int) Math.pow(2, 10);
            case 10: return (int) Math.pow(2, 11);
            case 11: return (int) Math.pow(2, 12);
            case 12: return (int) Math.pow(2, 13);
            case 13: return (int) Math.pow(2, 14);
            case 14: return (int) Math.pow(2, 15);
            case 15: return (int) Math.pow(2, 16);
            case 16: return (int) Math.pow(2, 17);
            case 17: return (int) Math.pow(2, 18);
            case 18: return (int) Math.pow(2, 19);
            case 19: return (int) Math.pow(2, 20);
            case 20: return (int) Math.pow(2, 21);
            case 21: return (int) Math.pow(2, 22);
            case 22: return (int) Math.pow(2, 23);
            case 23: return (int) Math.pow(2, 24);
        }
        return 0;
    }

}
