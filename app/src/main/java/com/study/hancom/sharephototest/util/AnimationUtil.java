package com.study.hancom.sharephototest.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

import static android.view.View.LAYER_TYPE_NONE;
import static android.view.View.LAYER_TYPE_SOFTWARE;

public class AnimationUtil {

    private List<ObjectAnimator> mWobbleAnimatorList = new LinkedList<ObjectAnimator>();

    /**
     * The GridView from Android Lollipoop requires some different
     * setVisibility() logic when switching cells.
     *
     * @return true if OS version is less than Lollipop, false if not
     */
    public static boolean isPreLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public void startWobbleAnimation(View v) {
        animateWobble(v);
    }

    public void stopWobbleAll() {
        for (Animator wobbleAnimator : mWobbleAnimatorList) {
            wobbleAnimator.cancel();
        }
        mWobbleAnimatorList.clear();
    }

    private void animateWobble(View v) {
        ObjectAnimator animator = createBaseWobble(v);
        animator.setFloatValues(-1, 1);
        animator.start();
        mWobbleAnimatorList.add(animator);
    }

    private ObjectAnimator createBaseWobble(final View v) {
        if (!isPreLollipop())
            v.setLayerType(LAYER_TYPE_SOFTWARE, null);

        ObjectAnimator animator = new ObjectAnimator();
        animator.setDuration(120);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setPropertyName("rotation");
        animator.setTarget(v);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setLayerType(LAYER_TYPE_NONE, null);
            }
        });
        return animator;
    }
}
