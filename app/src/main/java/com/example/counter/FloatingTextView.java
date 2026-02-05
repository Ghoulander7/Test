package com.example.counter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

public class FloatingTextView extends View {

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String text = "";
    private float textAlpha = 0f;
    private float offsetY = 0f;

    private static final int[] VALUE_COLORS = {
        0xFFCCCCCC,  // 1 - gray (common)
        0xFF4FC3F7,  // 2 - light blue
        0xFF9C27B0,  // 3 - purple
        0xFFFF9800,  // 4 - orange
        0xFFFFD700,  // 5 - gold (legendary)
    };

    private static final String[] VALUE_LABELS = {
        "", "", "", "", "LEGENDARY!"
    };

    public FloatingTextView(Context context) {
        super(context);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        glowPaint.setTextAlign(Paint.Align.CENTER);
        glowPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void show(int value) {
        int colorIndex = Math.min(value, 5) - 1;
        int color = VALUE_COLORS[Math.max(0, colorIndex)];
        text = "+" + value;
        textPaint.setColor(color);

        // Size scales with value
        float baseSize = 60f + (value - 1) * 20f;
        textPaint.setTextSize(baseSize);
        glowPaint.setTextSize(baseSize + 4);
        glowPaint.setColor(Color.argb(80, Color.red(color), Color.green(color), Color.blue(color)));

        // Animate: float up and fade out
        setTextAlpha(1f);
        setOffsetY(0f);
        setVisibility(VISIBLE);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "textAlpha", 1f, 1f, 0.8f, 0f);
        fadeOut.setDuration(1200);

        ObjectAnimator floatUp = ObjectAnimator.ofFloat(this, "offsetY", 0f, -250f);
        floatUp.setDuration(1200);

        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(this, "scaleX", 0.5f, 1.2f, 1f);
        scaleUp.setDuration(400);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(this, "scaleY", 0.5f, 1.2f, 1f);
        scaleUpY.setDuration(400);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(fadeOut, floatUp, scaleUp, scaleUpY);
        set.start();
    }

    public float getTextAlpha() { return textAlpha; }
    public void setTextAlpha(float alpha) {
        this.textAlpha = alpha;
        invalidate();
    }

    public float getOffsetY() { return offsetY; }
    public void setOffsetY(float y) {
        this.offsetY = y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text.isEmpty() || textAlpha <= 0f) return;

        float cx = getWidth() / 2f;
        float cy = getHeight() * 0.35f + offsetY;

        int alpha = (int) (textAlpha * 255);
        glowPaint.setAlpha(Math.min(255, alpha));
        textPaint.setAlpha(alpha);

        // Glow behind text
        canvas.drawText(text, cx, cy, glowPaint);
        canvas.drawText(text, cx, cy, textPaint);
    }
}
