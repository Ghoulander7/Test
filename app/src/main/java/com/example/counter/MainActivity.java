package com.example.counter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity {
    private int count = 0;
    private boolean gameActive = false;
    private boolean gameStarted = false;
    private final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long endTimeMs = 0;

    private FireworksView fireworksView;
    private FloatingTextView floatingText;
    private TreasureChestView chestView;
    private TextView counterText;
    private TextView timerText;
    private RelativeLayout gameOverLayout;
    private RelativeLayout winLayout;

    private static final int GOAL = 100;
    private static final long GAME_TIME_MS = 10000;

    private static final int[] WEIGHT_THRESHOLDS = {50, 75, 90, 98, 100};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counterText = findViewById(R.id.counterText);
        timerText = findViewById(R.id.timerText);
        gameOverLayout = findViewById(R.id.gameOverLayout);
        winLayout = findViewById(R.id.winLayout);
        FrameLayout chestContainer = findViewById(R.id.chestContainer);
        FrameLayout root = findViewById(R.id.rootLayout);
        Button restartButton = findViewById(R.id.restartButton);
        Button playAgainButton = findViewById(R.id.playAgainButton);

        chestView = new TreasureChestView(this);
        chestContainer.addView(chestView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        fireworksView = new FireworksView(this);
        root.addView(fireworksView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        floatingText = new FloatingTextView(this);
        root.addView(floatingText,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        chestView.setOnClickListener(v -> onChestTapped());
        restartButton.setOnClickListener(v -> resetGame());
        playAgainButton.setOnClickListener(v -> resetGame());

        timerText.setText("TAP TO START");
        timerText.setTextColor(0xFF4FC3F7);
    }

    private void onChestTapped() {
        if (!gameActive) {
            if (!gameStarted) {
                startGame();
            }
            return;
        }

        int value = rollValue();
        count += value;
        counterText.setText(String.valueOf(count));
        chestView.animateTap(value);
        floatingText.show(value);

        if (value == 5) {
            fireworksView.launch();
        }

        if (count >= GOAL) {
            gameWon();
        }
    }

    private void startGame() {
        gameStarted = true;
        gameActive = true;
        count = 0;
        counterText.setText("0");
        timerText.setTextColor(0xFFFF4444);
        endTimeMs = System.currentTimeMillis() + GAME_TIME_MS;
        scheduleTimerTick();
    }

    private void scheduleTimerTick() {
        handler.postDelayed(() -> timerTick(), 50);
    }

    private void timerTick() {
        if (!gameActive) return;
        long remaining = endTimeMs - System.currentTimeMillis();
        if (remaining <= 0) {
            timerText.setText("0.0");
            if (count < GOAL) {
                gameOver();
            }
            return;
        }
        long tenths = remaining / 100;
        timerText.setText(tenths / 10 + "." + tenths % 10);

        float seconds = remaining / 1000f;
        if (seconds <= 3f) {
            int a = (int) ((Math.sin(seconds * 10) + 1) * 127);
            timerText.setTextColor(0xFF000000 | (a << 16) | ((255 - a / 2) << 8));
        } else {
            timerText.setTextColor(0xFFFF4444);
        }

        scheduleTimerTick();
    }

    private void gameOver() {
        gameActive = false;
        gameStarted = false;

        TextView finalScoreText = findViewById(R.id.finalScoreText);
        finalScoreText.setText("Final Treasure: " + count + " / " + GOAL);
        gameOverLayout.setVisibility(View.VISIBLE);
    }

    private void gameWon() {
        gameActive = false;
        gameStarted = false;

        fireworksView.launch();

        long remaining = endTimeMs - System.currentTimeMillis();
        if (remaining < 0) remaining = 0;
        long tenths = remaining / 100;

        TextView winScoreText = findViewById(R.id.winScoreText);
        TextView winTimeText = findViewById(R.id.winTimeText);
        winScoreText.setText("Treasure: " + count);
        winTimeText.setText("Time left: " + tenths / 10 + "." + tenths % 10 + "s");
        winLayout.setVisibility(View.VISIBLE);
    }

    private void resetGame() {
        count = 0;
        counterText.setText("0");
        gameOverLayout.setVisibility(View.GONE);
        winLayout.setVisibility(View.GONE);
        timerText.setText("TAP TO START");
        timerText.setTextColor(0xFF4FC3F7);
        gameActive = false;
        gameStarted = false;
    }

    private int rollValue() {
        int roll = random.nextInt(100);
        for (int i = 0; i < WEIGHT_THRESHOLDS.length; i++) {
            if (roll < WEIGHT_THRESHOLDS[i]) {
                return i + 1;
            }
        }
        return 1;
    }

    protected void onDestroy() {
        super.onDestroy();
        gameActive = false;
    }
}
