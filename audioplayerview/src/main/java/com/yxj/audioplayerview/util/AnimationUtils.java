package com.yxj.audioplayerview.util;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Author:  Yxj
 * Time:    2019/5/8 下午1:56
 * -----------------------------------------
 * Description:
 */
public class AnimationUtils {

    View view;
    private RotateAnimation rotateAnimation;

    public AnimationUtils(View view) {
        this.view = view;
    }

    public void active(int centerX,int centerY){
        rotateAnimation = new RotateAnimation(0,360*3,centerX,centerY);
        rotateAnimation.setDuration(3000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        view.setAnimation(rotateAnimation);
        rotateAnimation.start();
    }

    public void cancel(){
        if(rotateAnimation!=null){
            rotateAnimation.cancel();
        }
    }


}
