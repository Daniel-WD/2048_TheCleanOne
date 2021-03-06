package com.titaniel.twothousandeightyfour.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.titaniel.twothousandeightyfour.R;
import com.titaniel.twothousandeightyfour.fragments.game.GameField;

import java.util.ArrayList;
import java.util.Objects;

public class Database {

    public static final int START_BACK_VALUE = 3;

    public static class Achievement {

        public static final int STATE_LOCKED = 0, STATE_GETABLE = 1, STATE_GOTTEN = 2;

        public final String title, description;
        public final String money;
        public int state = STATE_LOCKED;

        public Achievement(String title, String description, String money) {
            this.title = title;
            this.description = description;
            this.money = money;
        }

    }
    public static class Mode {

        final public String representative;
        final public int fieldSize;
        public int record = 0;
        final int id;
        public ArrayList<GameField.FieldImage> saved;
        public int points = 0, backs = 0;

        public Mode(String representative, int fieldSize, int id) {
            this.representative = representative;
            this.fieldSize = fieldSize;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            Mode mode = (Mode) o;
            return fieldSize == mode.fieldSize &&
                    record == mode.record &&
                    id == mode.id &&
                    points == mode.points &&
                    Objects.equals(representative, mode.representative) &&
                    Objects.equals(saved, mode.saved);
        }

        @Override
        public int hashCode() {

            return Objects.hash(representative, fieldSize, record, id, saved, points);
        }
    }

    public interface MoneyListener {
        void onMoneyChanged(int oldValue, int newValue);
    }
    private final static ArrayList<MoneyListener> mMoneyListeners = new ArrayList<>();
    private static int money = 20000;

    public static Achievement[] achievements;

    public static final Mode[] modes = {
            new Mode("3x3", 3, -1),
            new Mode("4x4", 4, 0),
            new Mode("5x5", 5, 1),
            new Mode("6x6", 6, 2),
            new Mode("7x7", 7, 3),
            new Mode("8x8", 8, 4),
            new Mode("9x9", 9, 5)
    };
    public static Mode currentMode = modes[1];

    private static SharedPreferences sPrefs;

    public static void init(Context c) {
        sPrefs = ((AppCompatActivity) c).getPreferences(Context.MODE_PRIVATE);

        //init achievemnts
        achievements = new Achievement[] {
                new Achievement(c.getString(R.string.beginner), c.getString(R.string.beginner_desc), "30"),
                new Achievement(c.getString(R.string.master), c.getString(R.string.master_desc), "10"),
                new Achievement(c.getString(R.string.hero), c.getString(R.string.hero_desc), "50"),
                new Achievement(c.getString(R.string.champion), c.getString(R.string.champion_desc), "50"),
                new Achievement(c.getString(R.string.combiner), c.getString(R.string.combiner_desc), "10"),
                new Achievement(c.getString(R.string.smart), c.getString(R.string.smart_desc), "20"),
        };

        achievements[0].state = Achievement.STATE_GETABLE;
        achievements[1].state = Achievement.STATE_GOTTEN;
        achievements[2].state = Achievement.STATE_LOCKED;
        achievements[3].state = Achievement.STATE_GETABLE;
        achievements[4].state = Achievement.STATE_GOTTEN;
        achievements[5].state = Achievement.STATE_GETABLE;
    }

    public static void load() {

        long time = System.nanoTime();

        for(Mode mode : modes) {
            mode.record = sPrefs.getInt("record-" + mode.id, 0);
            mode.points = sPrefs.getInt("points-" + mode.id, 0);
            mode.backs = sPrefs.getInt("backs-" + mode.id, 0);

            mode.saved = DataUtils.stringToImage(sPrefs.getString("image-" + mode.id, null));
        }

        Log.e("load_duration", "time: " + (System.nanoTime()-time));
    }

    public static void save() {
        SharedPreferences.Editor editor = sPrefs.edit();

        for(Mode mode : modes) {
            editor.putInt("record-" + mode.id, mode.record);
            editor.putInt("points-" + mode.id, mode.points);
            editor.putInt("backs-" + mode.id, mode.backs);

            String image = null;
            if(mode.saved != null && mode.saved.size() > 0) {
                image = DataUtils.imageToString(mode.saved);
            }

            editor.putString("image-" + mode.id, image);
        }

        editor.apply();
    }

    // MONEY

    public static int getMoney() {
        return money;
    }

    public static void addMoney(int m) {
        setMoney(money+m);
    }

    public static void removeMoney(int m) {
        setMoney(money-m);
    }

    private static void setMoney(int money) {
        int oldMoney = Database.money;
        Database.money = money;
        for(MoneyListener listener : mMoneyListeners) {
            listener.onMoneyChanged(oldMoney, money);
        }
    }

    public static void addMoneyListener(MoneyListener listener) {
        mMoneyListeners.add(listener);
    }

    public static void removeMoneyListener(MoneyListener listener) {
        mMoneyListeners.remove(listener);
    }


    // MODE -> HOME

    public static int curModePos() {
        if(currentMode == null) return -1;
        int result = -1;
        for(int i = 0; i < modes.length; i++) {
            if(currentMode.equals(modes[i])) result = i;
        }
        return result;
    }

    public static Mode nextMode() {
        if(!hasNextMode()) return null;
        return modes[curModePos()+1];
    }

    public static Mode previousMode() {
        if(!hasPreviousMode()) return null;
        return modes[curModePos()-1];
    }

    public static boolean hasNextMode() {
        return curModePos() < modes.length-1;
    }

    public static boolean hasPreviousMode() {
        return curModePos() != 0;
    }

}
