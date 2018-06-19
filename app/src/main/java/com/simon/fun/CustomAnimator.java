package com.simon.fun;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Simon Chang on 2018/6/11.
 *
 */

public class CustomAnimator {

    private static final long ANIM_TIME = 150;

    //新增 item 的動畫效果
    public static void newItem(View v) {
        play(createNewAnim(v));
    }

    //moveV: 被移動的 item, toHolesV: 移動到的目標位子
    public static void moveTo(View moveV, View toHolesV) {
        play(createTranslateAnim(moveV, toHolesV));
    }

    //移動+融合的動畫效果
    public static void moveToMerge(View moveV, View toHolesV, OnMergeListener listener) {
        play(createTranslateAnim(moveV, toHolesV), createMergeAnim(moveV, listener));
    }


    private static ValueAnimator createNewAnim(final View v) {
        ValueAnimator animScale = ValueAnimator.ofFloat(0f,1f);
        animScale.setRepeatCount(0);
        animScale.setRepeatMode(ObjectAnimator.REVERSE);
        animScale.setInterpolator(new DecelerateInterpolator());
        animScale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                v.setScaleX(val);
                v.setScaleY(val);
            }
        });
        return animScale;
    }

    private static ValueAnimator createMergeAnim(final View v, final OnMergeListener listener) {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",1f,0.5f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",1f,1.2f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",1f,1.2f);

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(v, alpha, scaleX, scaleY);
        objectAnimator.setRepeatCount(0);
        objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(listener != null)
                    listener.onMerge();
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                //動化結束 復原
                v.setAlpha(1f);
                v.setScaleX(1f);
                v.setScaleY(1f);
            }
            @Override
            public void onAnimationCancel(Animator animation) { }
            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        return objectAnimator;
    }

    private static ObjectAnimator createTranslateAnim(View moveV, View toHolesV) {
        float moveX = moveV.getX();
        float moveY = moveV.getY();
        float toX = toHolesV.getX();
        float toY = toHolesV.getY();

        Path path = new Path();
        path.moveTo(moveX, moveY); //設定起點
        path.quadTo(moveX, moveY, toX, toY); //設定移動路徑

        ObjectAnimator animTranslate = ObjectAnimator.ofFloat(moveV, "x","y", path);
        animTranslate.setRepeatCount(0);
        animTranslate.setInterpolator(new DecelerateInterpolator());

        return animTranslate;
    }

    private static void play(Animator... items) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(items);
        animatorSet.setDuration(ANIM_TIME);
        animatorSet.start();
    }


    interface OnMergeListener {
        void onMerge(); //做 merge 的數值變換處理
    }
}
