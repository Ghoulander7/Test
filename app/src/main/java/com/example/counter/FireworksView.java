package com.example.counter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireworksView extends View {

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean running = false;

    private static final int[] COLORS = {
        0xFFFF4444, 0xFF44FF44, 0xFF4488FF,
        0xFFFFFF44, 0xFFFF44FF, 0xFF44FFFF,
        0xFFFF8800, 0xFFFF0088, 0xFF00FF88
    };

    public FireworksView(Context context) {
        super(context);
    }

    public void launch() {
        particles.clear();
        running = true;

        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) {
            w = 1080;
            h = 1920;
        }

        // Create 3 burst points at random positions
        for (int burst = 0; burst < 3; burst++) {
            float cx = random.nextInt(w);
            float cy = random.nextInt(h / 2) + h / 6;
            int color = COLORS[random.nextInt(COLORS.length)];

            // Each burst has 40 particles
            for (int i = 0; i < 40; i++) {
                double angle = random.nextDouble() * 2 * Math.PI;
                float speed = 4f + random.nextFloat() * 10f;
                float vx = (float) (Math.cos(angle) * speed);
                float vy = (float) (Math.sin(angle) * speed);
                float size = 6f + random.nextFloat() * 8f;
                // Slight color variation
                int r = Math.min(255, Math.max(0, Color.red(color) + random.nextInt(60) - 30));
                int g = Math.min(255, Math.max(0, Color.green(color) + random.nextInt(60) - 30));
                int b = Math.min(255, Math.max(0, Color.blue(color) + random.nextInt(60) - 30));
                particles.add(new Particle(cx, cy, vx, vy, size, Color.rgb(r, g, b)));
            }
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!running || particles.isEmpty()) return;

        boolean anyAlive = false;
        for (Particle p : particles) {
            if (p.alpha <= 0) continue;
            anyAlive = true;

            p.x += p.vx;
            p.y += p.vy;
            p.vy += 0.15f; // gravity
            p.vx *= 0.98f; // drag
            p.vy *= 0.98f;
            p.alpha -= 3;
            p.size *= 0.995f;

            if (p.alpha > 0) {
                paint.setColor(p.color);
                paint.setAlpha(Math.max(0, p.alpha));
                canvas.drawCircle(p.x, p.y, p.size, paint);
            }
        }

        if (anyAlive) {
            postInvalidateOnAnimation();
        } else {
            running = false;
            particles.clear();
        }
    }

    private static class Particle {
        float x, y, vx, vy, size;
        int color;
        int alpha = 255;

        Particle(float x, float y, float vx, float vy, float size, int color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.color = color;
        }
    }
}
