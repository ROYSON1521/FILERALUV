package com.fileraluv.converter;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(FileDownloaderPlugin.class);
        super.onCreate(savedInstanceState);
        showBrandIntro();
    }

    private void showBrandIntro() {
        View content = findViewById(android.R.id.content);
        if (!(content instanceof FrameLayout)) return;

        FrameLayout root = (FrameLayout) content;
        FrameLayout overlay = new FrameLayout(this);
        overlay.setBackgroundColor(Color.rgb(255, 249, 246));
        overlay.setClickable(true);
        overlay.setFocusable(true);
        overlay.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);

        LinearLayout stack = new LinearLayout(this);
        stack.setOrientation(LinearLayout.VERTICAL);
        stack.setGravity(Gravity.CENTER_HORIZONTAL);

        FrameLayout stage = new FrameLayout(this);
        int stageSize = dp(142);
        LinearLayout.LayoutParams stageParams = new LinearLayout.LayoutParams(stageSize, stageSize);
        stageParams.gravity = Gravity.CENTER_HORIZONTAL;
        stack.addView(stage, stageParams);

        View orbit = new View(this);
        GradientDrawable orbitShape = new GradientDrawable();
        orbitShape.setShape(GradientDrawable.OVAL);
        orbitShape.setColor(Color.TRANSPARENT);
        orbitShape.setStroke(dp(2), Color.rgb(255, 198, 175));
        orbit.setBackground(orbitShape);
        FrameLayout.LayoutParams orbitParams = new FrameLayout.LayoutParams(dp(130), dp(130), Gravity.CENTER);
        stage.addView(orbit, orbitParams);

        View orbitDot = new View(this);
        GradientDrawable dotShape = new GradientDrawable();
        dotShape.setShape(GradientDrawable.OVAL);
        dotShape.setColor(Color.rgb(255, 143, 93));
        orbitDot.setBackground(dotShape);
        FrameLayout.LayoutParams dotParams = new FrameLayout.LayoutParams(dp(12), dp(12), Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        stage.addView(orbitDot, dotParams);

        TextView mark = new TextView(this);
        mark.setText("F");
        mark.setTextColor(Color.WHITE);
        mark.setTextSize(58);
        mark.setTypeface(Typeface.create("sans-serif-black", Typeface.NORMAL));
        mark.setGravity(Gravity.CENTER);
        GradientDrawable markBackground = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(239, 73, 67), Color.rgb(255, 132, 81)});
        markBackground.setCornerRadius(dp(28));
        mark.setBackground(markBackground);
        mark.setElevation(dp(12));
        FrameLayout.LayoutParams markParams = new FrameLayout.LayoutParams(dp(92), dp(92), Gravity.CENTER);
        stage.addView(mark, markParams);

        TextView brand = new TextView(this);
        brand.setText("FILERALUV");
        brand.setTextColor(Color.rgb(197, 45, 42));
        brand.setTextSize(29);
        brand.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        brand.setLetterSpacing(0.16f);
        brand.setGravity(Gravity.CENTER);
        brand.setAlpha(0f);
        brand.setTranslationY(dp(10));
        LinearLayout.LayoutParams brandParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        brandParams.topMargin = dp(13);
        stack.addView(brand, brandParams);

        TextView tagline = new TextView(this);
        tagline.setText("FILES, MADE SIMPLE");
        tagline.setTextColor(Color.rgb(133, 106, 101));
        tagline.setTextSize(11);
        tagline.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        tagline.setLetterSpacing(0.22f);
        tagline.setGravity(Gravity.CENTER);
        tagline.setAlpha(0f);
        LinearLayout.LayoutParams taglineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        taglineParams.topMargin = dp(9);
        stack.addView(tagline, taglineParams);

        FrameLayout.LayoutParams stackParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        overlay.addView(stack, stackParams);
        root.addView(overlay, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        mark.setScaleX(0.62f);
        mark.setScaleY(0.62f);
        overlay.post(() -> {
            mark.animate().scaleX(1f).scaleY(1f).setDuration(650)
                    .setInterpolator(new OvershootInterpolator(1.35f)).start();
            ObjectAnimator spin = ObjectAnimator.ofFloat(orbit, View.ROTATION, 0f, 360f);
            spin.setDuration(1350);
            spin.start();
            brand.animate().alpha(1f).translationY(0f).setStartDelay(150).setDuration(420).start();
            tagline.animate().alpha(1f).setStartDelay(330).setDuration(420).start();

            overlay.postDelayed(() -> overlay.animate().alpha(0f).setDuration(320)
                    .withEndAction(() -> root.removeView(overlay)).start(), 1750);
        });
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
