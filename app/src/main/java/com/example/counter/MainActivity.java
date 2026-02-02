package com.example.counter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int count = 0;
    private FireworksView fireworksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView counterText = findViewById(R.id.counterText);
        Button counterButton = findViewById(R.id.counterButton);
        Button resetButton = findViewById(R.id.resetButton);

        // Add fireworks overlay on top of everything
        FrameLayout root = findViewById(R.id.rootLayout);
        fireworksView = new FireworksView(this);
        root.addView(fireworksView,
            new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        counterButton.setOnClickListener(v -> {
            count++;
            counterText.setText(String.valueOf(count));
            if (count % 10 == 0) {
                fireworksView.launch();
            }
        });

        resetButton.setOnClickListener(v -> {
            count = 0;
            counterText.setText("0");
        });
    }
}
