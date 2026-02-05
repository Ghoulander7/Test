package com.example.counter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity {
    private int count = 0;
    private final Random random = new Random();
    private FireworksView fireworksView;
    private FloatingTextView floatingText;
    private TreasureChestView chestView;

    // Weighted probabilities: 1=50%, 2=25%, 3=15%, 4=8%, 5=2%
    private static final int[] WEIGHT_THRESHOLDS = {50, 75, 90, 98, 100};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView counterText = findViewById(R.id.counterText);
        Button resetButton = findViewById(R.id.resetButton);
        FrameLayout chestContainer = findViewById(R.id.chestContainer);
        FrameLayout root = findViewById(R.id.rootLayout);

        // Add treasure chest to its container
        chestView = new TreasureChestView(this);
        chestContainer.addView(chestView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Add fireworks overlay
        fireworksView = new FireworksView(this);
        root.addView(fireworksView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // Add floating text overlay
        floatingText = new FloatingTextView(this);
        root.addView(floatingText,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        chestView.setOnClickListener(v -> {
            int value = rollValue();
            count += value;
            counterText.setText(String.valueOf(count));
            chestView.animateTap(value);
            floatingText.show(value);

            // Fireworks on every 10th total or when hitting a 5
            if (value == 5 || count % 100 == 0) {
                fireworksView.launch();
            }
        });

        resetButton.setOnClickListener(v -> {
            count = 0;
            counterText.setText("0");
        });
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
}
