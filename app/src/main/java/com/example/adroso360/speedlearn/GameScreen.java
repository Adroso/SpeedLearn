package com.example.adroso360.speedlearn;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import android.widget.TextView;

import java.util.Objects;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameScreen extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 30;

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


    public TextView currentQuestion;
    private TextView countDown;
    private TextView gameTime;
    private TextView instructionText;
    private TextView playerAnswerDisplay;
    private String[] generatedQuestion;
    private int questionCount;
    private int playerScore;

    //Buttons
    private GridLayout gridNumbers;
    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button buttonClear;
    private Button buttonEnter;
    private Button buttonTwitter;
    private Button buttonMenu;

    //For Timer
    private long startTime = 0L;
    private Handler timerHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    //DataBase
    private ScoresDbHelper scoresDB;

    //Twitter
    private static final int AUTHENTICATE = 1;
    Twitter twitter = TwitterFactory.getSingleton();

    //Sound
    private SoundHelper soundHelper;
    private int correctSound;
    private int wrongSound;

    private SensorManager shakeSensorManager;
    private float sensorAccel;
    private float sensorAccelCurrent;
    private float sensorAccelLast;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_screen);

        mVisible = false;
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
        /** Initialize Sensors**/
        shakeSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeSensorManager.registerListener(mSensorListener, shakeSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorAccel = 0.00f;
        sensorAccelCurrent = SensorManager.GRAVITY_EARTH;
        sensorAccelLast = SensorManager.GRAVITY_EARTH;


        /** Game Code **/
        soundHelper = new SoundHelper();
        correctSound = soundHelper.addSound(R.raw.sectionpass,this);
        wrongSound = soundHelper.addSound(R.raw.sectionfail,this);

        currentQuestion = (TextView)findViewById(R.id.currentQuestion);
        countDown = (TextView)findViewById(R.id.countDown);
        gameTime = (TextView)findViewById(R.id.gameTime);
        playerAnswerDisplay = (TextView)findViewById(R.id.playerAnswerDisplay);
        buttonTwitter = (Button) findViewById(R.id.buttonTwitter);
        buttonMenu = (Button)findViewById(R.id.buttonMenu);

        //NumButtons
        buttonEnter = (Button) findViewById(R.id.buttonEnter);
        buttonClear = (Button) findViewById(R.id.buttonClear);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9= (Button) findViewById(R.id.button9);

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("0");
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("1");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("2");
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("3");
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("4");
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("5");
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("6");
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("7");
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("8");
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("9");
            }
        });
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePlayerInput("CLEAR");
            }
        });
        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateAnswer((String) playerAnswerDisplay.getText());
            }
        });

        //Twitter
        buttonTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameScreen.this, Authenticate.class);
                startActivityForResult(intent, AUTHENTICATE);
            }
        });

        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameScreen.this, MainScreen.class));

            }
        });

        // Game Start CountDown Timer
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDown.setText(String.valueOf(millisUntilFinished/1000));
            }

            public void onFinish() {

                countDown.setText("GO!");

                //Delays the clearing of countdown text by .5 seconds.
                countDown.postDelayed(new Runnable(){
                    @Override
                    public void run()
                    {
                        countDown.setVisibility(View.GONE);
                        generatedQuestion = GameControl.getEquation();
                        currentQuestion.setText(generatedQuestion[0]);

                        //Starting the Timer
                        startTime = SystemClock.uptimeMillis();
                        timerHandler.postDelayed(gameTimer, 0);
                    }
                }, 500);

            }
        }.start();



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

    private final Runnable gameTimer = new Runnable() {
        public void run(){

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            gameTime.setText("Your Time | " + mins + ":"
                            + String.format("%02d", secs) + ":"
                              + String.format("%03d", milliseconds));
            timerHandler.postDelayed(this, 0);

        }

    };

    private void updatePlayerInput(String inputChange){
        if (Objects.equals(inputChange, "CLEAR")){
            playerAnswerDisplay.setText("");
        } else {
            playerAnswerDisplay.setText(playerAnswerDisplay.getText() + inputChange);
        }

    }

    private void evaluateAnswer(String userAnswer){

        if (questionCount == 10){
            playerAnswerDisplay.setText("Game Finished");
            String tmpTime = String.valueOf(updatedTime);
            currentQuestion.setText(tmpTime);
            timerHandler.removeCallbacks(gameTimer);
            gameOver();

            //Write Score to Database
            scoresDB = new ScoresDbHelper(this);
            SQLiteDatabase db = scoresDB.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("points", playerScore);
            values.put("time", tmpTime);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert("scores",null, values);
            System.out.println(newRowId);




        }else if(Objects.equals(generatedQuestion[1], userAnswer)){
            //Correct
            checkCanPlay(correctSound);
            generatedQuestion = GameControl.getEquation();
            currentQuestion.setText(generatedQuestion[0]);
            playerAnswerDisplay.setText("");
            playerScore = playerScore + 1;

        }else {
            //Incorrect
            checkCanPlay(wrongSound);
            generatedQuestion = GameControl.getEquation();
            currentQuestion.setText(generatedQuestion[0]);
            playerAnswerDisplay.setText("");
            System.out.println("INCORRECT");
        }
        questionCount = questionCount + 1;

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == AUTHENTICATE && resultCode == RESULT_OK) {
            Background.run(new Runnable() {
                @Override
                public void run() {
                    String token = data.getStringExtra("access token");
                    String secret = data.getStringExtra("access token secret");
                    AccessToken accessToken = new AccessToken(token, secret);
                    twitter.setOAuthAccessToken(accessToken);
                    try {
                        twitter.updateStatus("Hey I Scored " + playerScore + " On SPEEDLEARN!");
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void gameOver(){
        SharedPreferences prefs = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        Boolean socialSetting = prefs.getBoolean("socialOption", true);

        gridNumbers = (GridLayout)findViewById(R.id.gridNumbers);
        instructionText = (TextView)findViewById(R.id.instructionText);
        instructionText.setVisibility(View.GONE);
        gridNumbers.setVisibility(View.GONE);

        if (socialSetting){
            buttonTwitter.setVisibility(View.VISIBLE);
        }
        buttonMenu.setVisibility(View.VISIBLE);

        currentQuestion.setText("You Got " + playerScore + " Questions");


    }

    private void checkCanPlay(int soundID){
        SharedPreferences prefs = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        Boolean musicSetting = prefs.getBoolean("musicOption", true);
        if (musicSetting){
            soundHelper.play(soundID);
        }


    }
    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            sensorAccelLast = sensorAccelCurrent;
            sensorAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = sensorAccelCurrent - sensorAccelLast;
            sensorAccel = sensorAccel * 0.9f + delta;

            //number for sensorAccel is the shaken amount threshold (higher equals more shake)
            if (sensorAccel > 6) {
                System.out.println("Shaking");


            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        shakeSensorManager.registerListener(mSensorListener, shakeSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        shakeSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

}

