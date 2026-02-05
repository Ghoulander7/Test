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

    private final Paint woodPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint woodDarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint metalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint metalDarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint metalHighlight = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint plankLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float lidOpenAngle = 0f;

    public TreasureChestView(Context context) {
        super(context);
        setClickable(true);
        setFocusable(true);

        woodPaint.setColor(0xFF7B3F00);
        woodDarkPaint.setColor(0xFF5C2E00);
        metalPaint.setColor(0xFFB8860B);
        metalDarkPaint.setColor(0xFF8B6914);
        metalHighlight.setColor(0xFFFFD700);
        plankLine.setColor(0xFF4A2500);
        plankLine.setStrokeWidth(2f);
        blackPaint.setColor(0xFF1A0A00);
    }

    public void animateTap(int value) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0.85f, 1.15f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.85f, 1.15f, 1f);
        scaleX.setDuration(400);
        scaleY.setDuration(400);

        ObjectAnimator lidOpen = ObjectAnimator.ofFloat(this, "lidOpenAngle", 0f, 25f, 0f);
        lidOpen.setDuration(500);
        lidOpen.setInterpolator(new OvershootInterpolator(2f));

        ObjectAnimator rotate = ObjectAnimator.ofFloat(this, "rotation", 0f, -4f, 4f, -2f, 2f, 0f);
        rotate.setDuration(500);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, lidOpen, rotate);
        set.start();
    }

    public float getLidOpenAngle() { return lidOpenAngle; }
    public void setLidOpenAngle(float angle) {
        this.lidOpenAngle = angle;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        if (w == 0 || h == 0) return;
        float cx = w / 2f;

        // Chest dimensions
        float chestW = w * 0.80f;
        float bodyH = h * 0.40f;
        float lidH = h * 0.28f;

        float left = cx - chestW / 2f;
        float right = cx + chestW / 2f;
        float bodyBottom = h * 0.78f;
        float bodyTop = bodyBottom - bodyH;
        float lidBottom = bodyTop;

        // ---- Ground shadow ----
        Paint shadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadow.setColor(0x30000000);
        canvas.drawOval(left - 5, bodyBottom - 3, right + 5, bodyBottom + 15, shadow);

        // ---- CHEST BODY (rectangular box) ----
        woodPaint.setShader(new LinearGradient(left, bodyTop, left, bodyBottom,
                0xFF8B5A2B, 0xFF5C2E00, Shader.TileMode.CLAMP));
        canvas.drawRect(left, bodyTop, right, bodyBottom, woodPaint);
        woodPaint.setShader(null);

        // Horizontal plank lines on body
        float plankSpacing = bodyH / 4f;
        for (int i = 1; i < 4; i++) {
            float y = bodyTop + plankSpacing * i;
            canvas.drawLine(left + 2, y, right - 2, y, plankLine);
        }

        // ---- METAL CORNER BRACKETS (L-shaped) ----
        float bracketLen = chestW * 0.12f;
        float bracketW = 7f;
        metalPaint.setStyle(Paint.Style.FILL);

        // Top-left
        canvas.drawRect(left, bodyTop, left + bracketLen, bodyTop + bracketW, metalPaint);
        canvas.drawRect(left, bodyTop, left + bracketW, bodyTop + bracketLen, metalPaint);
        // Top-right
        canvas.drawRect(right - bracketLen, bodyTop, right, bodyTop + bracketW, metalPaint);
        canvas.drawRect(right - bracketW, bodyTop, right, bodyTop + bracketLen, metalPaint);
        // Bottom-left
        canvas.drawRect(left, bodyBottom - bracketW, left + bracketLen, bodyBottom, metalPaint);
        canvas.drawRect(left, bodyBottom - bracketLen, left + bracketW, bodyBottom, metalPaint);
        // Bottom-right
        canvas.drawRect(right - bracketLen, bodyBottom - bracketW, right, bodyBottom, metalPaint);
        canvas.drawRect(right - bracketW, bodyBottom - bracketLen, right, bodyBottom, metalPaint);

        // Bracket rivets (small gold dots)
        float rivetR = 3.5f;
        canvas.drawCircle(left + bracketW / 2, bodyTop + bracketW / 2, rivetR, metalHighlight);
        canvas.drawCircle(right - bracketW / 2, bodyTop + bracketW / 2, rivetR, metalHighlight);
        canvas.drawCircle(left + bracketW / 2, bodyBottom - bracketW / 2, rivetR, metalHighlight);
        canvas.drawCircle(right - bracketW / 2, bodyBottom - bracketW / 2, rivetR, metalHighlight);

        // ---- HORIZONTAL METAL BAND across body center ----
        float bandH = 8f;
        float bandY = bodyTop + bodyH * 0.5f - bandH / 2f;
        canvas.drawRect(left, bandY, right, bandY + bandH, metalPaint);
        // Band rivets
        canvas.drawCircle(left + 14, bandY + bandH / 2, rivetR, metalHighlight);
        canvas.drawCircle(right - 14, bandY + bandH / 2, rivetR, metalHighlight);
        canvas.drawCircle(cx, bandY + bandH / 2, rivetR, metalHighlight);

        // ---- LID (domed/arched top) ----
        canvas.save();
        canvas.rotate(-lidOpenAngle, cx, lidBottom);

        float lidTop = lidBottom - lidH;

        // Draw arched lid using a path
        Path lidPath = new Path();
        lidPath.moveTo(left, lidBottom);
        lidPath.lineTo(left, lidTop + lidH * 0.35f);
        lidPath.quadTo(cx, lidTop - lidH * 0.15f, right, lidTop + lidH * 0.35f);
        lidPath.lineTo(right, lidBottom);
        lidPath.close();

        woodPaint.setShader(new LinearGradient(left, lidTop, left, lidBottom,
                0xFFA0693D, 0xFF7B3F00, Shader.TileMode.CLAMP));
        canvas.drawPath(lidPath, woodPaint);
        woodPaint.setShader(null);

        // Plank lines on lid
        float lidPlankY1 = lidBottom - lidH * 0.35f;
        float lidPlankY2 = lidBottom - lidH * 0.65f;
        canvas.drawLine(left + 8, lidPlankY1, right - 8, lidPlankY1, plankLine);
        // Shorter line for upper curved area
        float inset = chestW * 0.08f;
        canvas.drawLine(left + inset, lidPlankY2, right - inset, lidPlankY2, plankLine);

        // Metal band across lid
        float lidBandY = lidBottom - lidH * 0.5f;
        // Curved band following the arch
        Paint lidBandPaint = new Paint(metalPaint);
        lidBandPaint.setStyle(Paint.Style.STROKE);
        lidBandPaint.setStrokeWidth(bandH);
        Path bandPath = new Path();
        bandPath.moveTo(left, lidBandY + 4);
        bandPath.quadTo(cx, lidBandY - lidH * 0.12f, right, lidBandY + 4);
        canvas.drawPath(bandPath, lidBandPaint);

        // Lid highlight along top curve
        Paint highlight = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlight.setColor(0x20FFFFFF);
        highlight.setStyle(Paint.Style.STROKE);
        highlight.setStrokeWidth(4f);
        Path highlightPath = new Path();
        highlightPath.moveTo(left + 12, lidTop + lidH * 0.38f);
        highlightPath.quadTo(cx, lidTop - lidH * 0.1f, right - 12, lidTop + lidH * 0.38f);
        canvas.drawPath(highlightPath, highlight);

        canvas.restore();

        // ---- FRONT CLASP / LOCK ----
        // Lock plate
        float lockW = 28f;
        float lockH = 34f;
        float lockLeft2 = cx - lockW / 2f;
        float lockTop = bodyTop - lockH * 0.4f;
        RectF lockRect = new RectF(lockLeft2, lockTop, lockLeft2 + lockW, lockTop + lockH);
        canvas.drawRoundRect(lockRect, 5, 5, metalPaint);

        // Lock plate border
        Paint lockBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        lockBorder.setColor(0xFF8B6914);
        lockBorder.setStyle(Paint.Style.STROKE);
        lockBorder.setStrokeWidth(2f);
        canvas.drawRoundRect(lockRect, 5, 5, lockBorder);

        // Keyhole circle
        float keyCY = lockTop + lockH * 0.38f;
        canvas.drawCircle(cx, keyCY, 5f, blackPaint);

        // Keyhole slot
        Path slot = new Path();
        slot.moveTo(cx - 2.5f, keyCY + 3);
        slot.lineTo(cx + 2.5f, keyCY + 3);
        slot.lineTo(cx + 1.5f, keyCY + 12);
        slot.lineTo(cx - 1.5f, keyCY + 12);
        slot.close();
        canvas.drawPath(slot, blackPaint);

        // ---- GOLD GLOW when lid opens ----
        if (lidOpenAngle > 2f) {
            float glowA = Math.min(1f, lidOpenAngle / 20f);
            Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
            glow.setColor(Color.argb((int) (100 * glowA), 255, 215, 0));
            canvas.drawRect(left + 10, bodyTop - 6, right - 10, bodyTop + 4, glow);
            // Wider softer glow
            glow.setColor(Color.argb((int) (40 * glowA), 255, 215, 0));
            canvas.drawRect(left, bodyTop - 12, right, bodyTop + 2, glow);
        }
    }
}
