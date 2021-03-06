package com.titaniel.twothousandeightyfour;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.MobileAds;
import com.titaniel.twothousandeightyfour.admob.Admob;
import com.titaniel.twothousandeightyfour.database.Database;
import com.titaniel.twothousandeightyfour.database.DesignProvider;
import com.titaniel.twothousandeightyfour.fragments.Dialog;
import com.titaniel.twothousandeightyfour.fragments.game.Game;
import com.titaniel.twothousandeightyfour.fragments.achievements.Achievements;
import com.titaniel.twothousandeightyfour.fragments.AnimatedFragment;
import com.titaniel.twothousandeightyfour.fragments.Home;

public class MainActivity extends AppCompatActivity {

    //states
    public static final int
            STATE_FM_HOME = 0,
            STATE_FM_ACHIEVEMENTS = 1,
            STATE_FM_GAME = 2,
            STATE_FM_DIALOG = 3;

    public int state = STATE_FM_HOME;

    //fragments
    public Home home;
    public Achievements achievements;
    public Game game;
    public Dialog dialog;

   // private ImageView mIvBackground;

    private Handler mHandler = new Handler();

    private Handler mAdmobHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HandlerThread thread = new HandlerThread("admob");
        thread.start();
        mAdmobHandler = new Handler(thread.getLooper());
        mAdmobHandler.post(() -> {
            //admob
            Admob.init(this, mHandler);
        });


        //Database
        Database.init(this);

        //Design Provider
        DesignProvider.init(this);

        //init Fragments
        home = (Home) getSupportFragmentManager().findFragmentById(R.id.fragmentHome);
//        achievements = (Achievements) getSupportFragmentManager().findFragmentById(R.id.fragmentAchievements);
        game = (Game) getSupportFragmentManager().findFragmentById(R.id.fragmentGame);
        dialog = (Dialog) getSupportFragmentManager().findFragmentById(R.id.fragmentDialog);

        //views
        //mIvBackground = findViewById(R.id.ivBackground);

        mHandler.postDelayed(() -> showHome(0, null), 500);
//        mHandler.postDelayed(() -> showDialog(0, Dialog.MODE_LOST_VIDEO), 800);


    }

    public void showHome(long delay, @Nullable AnimatedFragment oldFragment) {
        state = STATE_FM_HOME;
        if(oldFragment != null) delay += oldFragment.hide(delay);
        home.show(delay);
    }

    public void showAchievements(long delay, @Nullable AnimatedFragment oldFragment) {
        state = STATE_FM_ACHIEVEMENTS;
        if(oldFragment != null) delay += oldFragment.hide(delay);
        achievements.show(delay);
    }

    public void showGame(long delay, @Nullable AnimatedFragment oldFragment) {
        state = STATE_FM_GAME;
        if(oldFragment != null) delay += oldFragment.hide(delay);
        game.show(delay);
    }

    public void showDialog(long delay, int mode) {
        state = STATE_FM_DIALOG;
        dialog.setMode(mode);
        dialog.show(delay);
    }

    public void hideDialog(long delay) {
        state = STATE_FM_GAME;
        dialog.hide(delay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //hide statusbar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        Database.load();


        mHandler.post(() -> {
            if(Admob.rewardedVideoAd != null) Admob.rewardedVideoAd.resume(this);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(state == STATE_FM_GAME) {
            Database.currentMode.saved = game.gameField.getSaveImage();
        }

        Database.save();

        mHandler.post(() -> {
            if(Admob.rewardedVideoAd != null) Admob.rewardedVideoAd.pause(this);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.post(() -> {
            if(Admob.rewardedVideoAd != null) Admob.rewardedVideoAd.destroy(this);
        });
    }

    @Override
    public void onBackPressed() {
        switch(state) {
            case STATE_FM_HOME:
                super.onBackPressed();
                break;
            case STATE_FM_ACHIEVEMENTS:
                showHome(0, achievements);
                break;
            case STATE_FM_GAME:
                game.onBackPressed();
                break;
            case STATE_FM_DIALOG:

                break;
        }
    }
}
