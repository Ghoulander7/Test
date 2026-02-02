package com.example.counter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.counterButton);
        button.setOnClickListener(v -> {
            count++;
            button.setText(String.valueOf(count));
        });
    }
}
