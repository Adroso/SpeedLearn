package com.example.adroso360.speedlearn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HighScoreScreen extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    /** Variables **/

    private ScoresDbHelper scoresDB;
    private TextView score1Points;
    private TextView score1time;
    private TextView score2Points;
    private TextView score2time;
    private TextView score3Points;
    private TextView score3time;
    private TextView score4Points;
    private TextView score4time;
    private TextView score5Points;
    private TextView score5time;
    private Button buttonMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Fullscreen Methods **/
        setContentView(R.layout.activity_high_score_screen);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        /** My Code **/

        /** Finding Views **/
        score1Points = (TextView)findViewById(R.id.score1Points);
        score1time = (TextView)findViewById(R.id.score1Time);
        score2Points = (TextView)findViewById(R.id.score2Points);
        score2time = (TextView)findViewById(R.id.score2Time);
        score3Points = (TextView)findViewById(R.id.score3Points);
        score3time = (TextView)findViewById(R.id.score3Time);
        score4Points = (TextView)findViewById(R.id.score4Points);
        score4time = (TextView)findViewById(R.id.score4Time);
        score5Points = (TextView)findViewById(R.id.score5Points);
        score5time = (TextView)findViewById(R.id.score5Time);
        buttonMenu = (Button)findViewById(R.id.buttonMenu);


        /** DataBase Section **/

        scoresDB = new ScoresDbHelper(this);
        SQLiteDatabase db = scoresDB.getReadableDatabase();

        /** Queries that get both points and time to be used for sorting **/

        Cursor cursor = db.rawQuery("SELECT points FROM scores ORDER BY points DESC", null);
        List storedPoints = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemPoints = cursor.getString(0);
            storedPoints.add(itemPoints);
        }
        cursor.close();

        Cursor cursorTime = db.rawQuery("SELECT time FROM scores ORDER BY points DESC", null);
        List storedTime = new ArrayList<>();
        while(cursorTime.moveToNext()) {
            String itemTime = cursorTime.getString(0);
            storedTime.add(itemTime);
        }
        cursor.close();

        // Printing
        try{
            score1Points.setText(storedPoints.get(0).toString());
            score1time.setText(timeStringBuilder(String.valueOf(storedTime.get(0))));
            score2Points.setText(storedPoints.get(1).toString());
            score2time.setText(timeStringBuilder(String.valueOf(storedTime.get(1))));
            score3Points.setText(storedPoints.get(2).toString());
            score3time.setText(timeStringBuilder(String.valueOf(storedTime.get(2))));
            score4Points.setText(storedPoints.get(3).toString());
            score4time.setText(timeStringBuilder(String.valueOf(storedTime.get(3))));
            score5Points.setText(storedPoints.get(4).toString());
            score5time.setText(timeStringBuilder(String.valueOf(storedTime.get(4))));
        } catch (Exception e){
            System.out.println("Empty Entries in DB");
        }

        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HighScoreScreen.this, MainScreen.class));

            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public String timeStringBuilder(String initTime){
        /** Takes the raw millisecond value from the DB
         * and formats it into minutes and seconds **/
        int miliTime = Integer.parseInt(initTime);
        int secs = (int) (miliTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        String newTime = ("" + mins + "m "
                + String.format("%02d", secs) + "s ");

       return newTime;
    }
}
