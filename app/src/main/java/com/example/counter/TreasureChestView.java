package com.example.counter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class TreasureChestView extends View {

    private final Paint bodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bodyDarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lidTopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lockBodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float lidOpenAngle = 0f;

    public TreasureChestView(Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);

        bodyPaint.setColor(0xFF8B4513);       // saddle brown
        bodyDarkPaint.setColor(0xFF6B3410);   // darker brown
        lidPaint.setColor(0xFF9B5523);        // lighter brown for lid
        lidTopPaint.setColor(0xFFA0602B);     // lid top highlight
        bandPaint.setColor(0xFFDAA520);       // gold bands
        bandPaint.setStyle(Paint.Style.FILL);
        lockPaint.setColor(0xFFFFD700);       // gold lock
        lockBodyPaint.setColor(0xFFDAA520);   // lock body
        glowPaint.setColor(0x40FFD700);       // subtle gold glow
        highlightPaint.setColor(0x30FFFFFF);  // highlight
        shadowPaint.setColor(0xFF5A2D0C);     // dark shadow
    }

    public void animateTap(int value) {
        // Bounce scale animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.85f, 1.15f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.85f, 1.15f, 1f);
        scaleX.setDuration(400);
        scaleY.setDuration(400);

        // Lid open animation
        ObjectAnimator lidOpen = ObjectAnimator.ofFloat(this, "lidOpenAngle", 0f, 25f, 0f);
        lidOpen.setDuration(500);
        lidOpen.setInterpolator(new OvershootInterpolator(2f));

        // Slight rotation wobble
        ObjectAnimator rotate = ObjectAnimator.ofFloat(this, "rotation", 0f, -5f, 5f, -3f, 3f, 0f);
        rotate.setDuration(500);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, lidOpen, rotate);
        set.start();
    }

    public float getLidOpenAngle() {
        return lidOpenAngle;
    }

    public void setLidOpenAngle(float angle) {
        this.lidOpenAngle = angle;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;

        float chestW = w * 0.75f;
        float chestH = h * 0.45f;
        float lidH = h * 0.22f;

        float left = cx - chestW / 2f;
        float right = cx + chestW / 2f;
        float bodyTop = cy - chestH / 2f + lidH * 0.3f;
        float bodyBottom = cy + chestH / 2f;
        float lidTop = bodyTop - lidH + lidH * 0.3f;

        // Ground shadow
        Paint groundShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        groundShadow.setColor(0x30000000);
        canvas.drawOval(left - 10, bodyBottom - 5, right + 10, bodyBottom + 20, groundShadow);

        // === CHEST BODY ===
        RectF bodyRect = new RectF(left, bodyTop, right, bodyBottom);
        // Gradient on body
        bodyPaint.setShader(new LinearGradient(left, bodyTop, left, bodyBottom,
                0xFF9B5523, 0xFF6B3410, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(bodyRect, 12, 12, bodyPaint);
        bodyPaint.setShader(null);

        // Body bottom edge shadow
        shadowPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left + 5, bodyBottom - 8, right - 5, bodyBottom, shadowPaint);

        // Horizontal gold bands on body
        float bandH = 6f;
        float band1Y = bodyTop + (bodyBottom - bodyTop) * 0.25f;
        float band2Y = bodyTop + (bodyBottom - bodyTop) * 0.7f;
        canvas.drawRoundRect(left - 3, band1Y, right + 3, band1Y + bandH, 3, 3, bandPaint);
        canvas.drawRoundRect(left - 3, band2Y, right + 3, band2Y + bandH, 3, 3, bandPaint);

        // Vertical gold bands
        float vBandW = 8f;
        canvas.drawRoundRect(cx - vBandW / 2, bodyTop, cx + vBandW / 2, bodyBottom, 3, 3, bandPaint);
        canvas.drawRoundRect(left + chestW * 0.2f - vBandW / 2, bodyTop,
                left + chestW * 0.2f + vBandW / 2, bodyBottom, 3, 3, bandPaint);
        canvas.drawRoundRect(right - chestW * 0.2f - vBandW / 2, bodyTop,
                right - chestW * 0.2f + vBandW / 2, bodyBottom, 3, 3, bandPaint);

        // Corner rivets
        Paint rivetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rivetPaint.setColor(0xFFFFD700);
        float rivetR = 5f;
        float rivetInset = 18f;
        canvas.drawCircle(left + rivetInset, bodyTop + rivetInset, rivetR, rivetPaint);
        canvas.drawCircle(right - rivetInset, bodyTop + rivetInset, rivetR, rivetPaint);
        canvas.drawCircle(left + rivetInset, bodyBottom - rivetInset, rivetR, rivetPaint);
        canvas.drawCircle(right - rivetInset, bodyBottom - rivetInset, rivetR, rivetPaint);

        // === LID ===
        canvas.save();
        // Pivot at the back of the lid (top-center) for the open animation
        canvas.rotate(-lidOpenAngle, cx, bodyTop);

        // Lid body (slightly curved top)
        RectF lidRect = new RectF(left - 2, lidTop, right + 2, bodyTop + 4);
        lidPaint.setShader(new LinearGradient(left, lidTop, left, bodyTop,
                0xFFA86B35, 0xFF8B4513, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(lidRect, 14, 14, lidPaint);
        lidPaint.setShader(null);

        // Lid curved top highlight
        RectF lidArc = new RectF(left + 10, lidTop - 15, right - 10, lidTop + 30);
        canvas.drawOval(lidArc, lidTopPaint);

        // Gold band on lid
        float lidBandY = lidTop + (bodyTop - lidTop) * 0.5f;
        canvas.drawRoundRect(left - 3, lidBandY, right + 3, lidBandY + bandH, 3, 3, bandPaint);

        // Vertical band on lid
        canvas.drawRoundRect(cx - vBandW / 2, lidTop, cx + vBandW / 2, bodyTop + 4, 3, 3, bandPaint);

        // Lid highlight
        highlightPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(left + 10, lidTop + 4, right - 10, lidTop + 14, 5, 5, highlightPaint);

        canvas.restore();

        // === LOCK / KEYHOLE ===
        // Lock base plate
        float lockW = 32f;
        float lockH = 36f;
        float lockLeft = cx - lockW / 2;
        float lockTop2 = bodyTop - lockH / 2;
        canvas.drawRoundRect(lockLeft, lockTop2, lockLeft + lockW, lockTop2 + lockH, 6, 6, lockBodyPaint);

        // Lock arch
        Paint lockArchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lockArchPaint.setColor(0xFFFFD700);
        lockArchPaint.setStyle(Paint.Style.STROKE);
        lockArchPaint.setStrokeWidth(5f);
        RectF lockArc = new RectF(cx - 10, lockTop2 - 12, cx + 10, lockTop2 + 8);
        canvas.drawArc(lockArc, 180, 180, false, lockArchPaint);

        // Keyhole
        Paint keyholePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        keyholePaint.setColor(0xFF3D1E00);
        float keyCY = lockTop2 + lockH * 0.45f;
        canvas.drawCircle(cx, keyCY, 6f, keyholePaint);
        Path keySlot = new Path();
        keySlot.moveTo(cx - 3, keyCY + 3);
        keySlot.lineTo(cx + 3, keyCY + 3);
        keySlot.lineTo(cx + 2, keyCY + 14);
        keySlot.lineTo(cx - 2, keyCY + 14);
        keySlot.close();
        canvas.drawPath(keySlot, keyholePaint);

        // === GLOW effect when lid is open ===
        if (lidOpenAngle > 2f) {
            float glowAlpha = Math.min(1f, lidOpenAngle / 20f);
            Paint openGlow = new Paint(Paint.ANTI_ALIAS_FLAG);
            openGlow.setColor(Color.argb((int) (80 * glowAlpha), 255, 215, 0));
            canvas.drawRect(left + 15, bodyTop - 5, right - 15, bodyTop + 5, openGlow);
        }
    }
}
