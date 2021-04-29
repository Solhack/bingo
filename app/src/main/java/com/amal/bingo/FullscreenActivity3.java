package com.amal.bingo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Random;

import static com.amal.bingo.FullscreenActivity8.peerCount;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity3 extends com.amal.bingo.FullscreenActivity2 {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    public static int count = 0;
    int i = 0;
    int j = 0;
    int[] num = new int[25];
    protected Button[] b = new Button[num.length];
    protected TextView playerName;
    Intent intent1;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    public static Animation inFromRightAnimation(long durationMillis) {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromRight.setDuration(durationMillis);
        return inFromRight;
    }

    public static Animation inFromLeftAnimation(long durationMillis) {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromLeft.setDuration(durationMillis);
        return inFromLeft;
    }

    public static Animation inFromTopAnimation(long durationMillis) {
        Animation inFromTop = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
                Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromTop.setDuration(durationMillis);
        return inFromTop;
    }

    public static Animation inFromBottomAnimation(long durationMillis) {

        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
                Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromBottom.setDuration(durationMillis);
        return inFromBottom;
    }

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
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

    @Override
    public void onBackPressed() {
       finish();
    }

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
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private BroadcastReceiver tempReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("begin1").equals("begin")) {
                Button next = (Button) findViewById(R.id.next);
                if (next.getText().toString().equals("BEGIN")) {
                    Intent intent2 = new Intent("data4");
                    intent2.putExtra("ready", "ready");
                    LocalBroadcastManager.getInstance(com.amal.bingo.FullscreenActivity3.this).sendBroadcast(intent2);
                    Toast.makeText(getApplicationContext(), "Beginning", Toast.LENGTH_SHORT).show();
                    if(count == peerCount+1) {
                        finish();
                        startActivity(intent1);
                    }
                } else {
                    Intent intent2 = new Intent("data4");
                    intent2.putExtra("ready", "not ready");
                    LocalBroadcastManager.getInstance(com.amal.bingo.FullscreenActivity3.this).sendBroadcast(intent2);
                    Toast.makeText(getApplicationContext(), "Waiting for you to begin", Toast.LENGTH_SHORT).show();

                }
            } else if(intent.getStringExtra("begin1").equals("ready")){
                if(count == peerCount+1) {
                    finish();
                    startActivity(intent1);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen3);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(tempReceiver2,new IntentFilter("data3"));
        playerName = (TextView) findViewById(R.id.playername);
        TextView text = (TextView) findViewById(R.id.bingo);
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        Button next = (Button) findViewById(R.id.next);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout2);

        playerName.setAnimation(inFromTopAnimation(500));
        imageView.setAnimation(inFromTopAnimation(500));
        text.setAnimation(inFromLeftAnimation(750));
        next.setAnimation(inFromRightAnimation(1000));
        tableLayout.setAnimation(inFromBottomAnimation(1250));

        playerName.setText(name);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        for (i = 0; i < num.length; i++)
            num[i] = i + 1;

        b[0] = (Button) findViewById(R.id.b1);
        b[1] = (Button) findViewById(R.id.b2);
        b[2] = (Button) findViewById(R.id.b3);
        b[3] = (Button) findViewById(R.id.b4);
        b[4] = (Button) findViewById(R.id.b5);
        b[5] = (Button) findViewById(R.id.b6);
        b[6] = (Button) findViewById(R.id.b7);
        b[7] = (Button) findViewById(R.id.b8);
        b[8] = (Button) findViewById(R.id.b9);
        b[9] = (Button) findViewById(R.id.b10);
        b[10] = (Button) findViewById(R.id.b11);
        b[11] = (Button) findViewById(R.id.b12);
        b[12] = (Button) findViewById(R.id.b13);
        b[13] = (Button) findViewById(R.id.b14);
        b[14] = (Button) findViewById(R.id.b15);
        b[15] = (Button) findViewById(R.id.b16);
        b[16] = (Button) findViewById(R.id.b17);
        b[17] = (Button) findViewById(R.id.b18);
        b[18] = (Button) findViewById(R.id.b19);
        b[19] = (Button) findViewById(R.id.b20);
        b[20] = (Button) findViewById(R.id.b21);
        b[21] = (Button) findViewById(R.id.b22);
        b[22] = (Button) findViewById(R.id.b23);
        b[23] = (Button) findViewById(R.id.b24);
        b[24] = (Button) findViewById(R.id.b25);

        intent1 = new Intent(com.amal.bingo.FullscreenActivity3.this, com.amal.bingo.FullscreenActivity4.class);
        String[] str = {"Good!", "Wow!", "Nice!", "Superb!", "Yes!", "Great!", "Amazing!", "Wonderful!", "Perfect!", "Oh yeah!"};
        Random random = new Random();
        Drawable drawable = b[0].getForeground();

        for(i = 0; i<b.length; i++)
        {
            b[i].setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    Button btn = (Button) findViewById(v.getId());
                    if (btn.getForeground() != null) {
                        btn.setForeground(null);
                        btn.setText(num[j] + "");
                        btn.setTextColor(Color.WHITE);
                        text.setText(str[random.nextInt(10)]);
                        intent1.putExtra("message"+btn.getId(),btn.getText().toString());
                        j++;
                        if(j == 20)
                            Toast.makeText(getApplicationContext(), "To avoid Network Lag Error (101), make sure no players complete arranging the card at the same time", Toast.LENGTH_SHORT).show();
                        else if(j == 25)
                        {
                            text.setText("");
                            next.setText("BEGIN");
                            Intent peerIntent = new Intent("peer");
                            peerIntent.putExtra("peer","peer");
                            LocalBroadcastManager.getInstance(com.amal.bingo.FullscreenActivity3.this).sendBroadcast(peerIntent);
                            com.amal.bingo.FullscreenActivity8.turn = count;
                            count++;
                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent("data2");
                                    intent.putExtra("begin", "begin");
                                    LocalBroadcastManager.getInstance(com.amal.bingo.FullscreenActivity3.this).sendBroadcast(intent);
                                }
                            });
                        }
                    }
                    else if(next.getText().toString().equals("BEGIN")){
                        //do nothing
                    }
                    else
                        text.setText("Choose another position!");
                }
            });
        }

        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                for(i = 0; i<25; i++)
                {
                    b[i].setText("");
                    b[i].setForeground(drawable);
                }
                text.setText("Let's arrange your Bingo card again!");
                j  = 0;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        hide();
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
        mHideHandler.postDelayed(mHidePart2Runnable, 0);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}