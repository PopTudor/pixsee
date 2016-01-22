package com.marked.dotview

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import java.util.*

/**
 * Created by Tudor Pop on 22-Jan-16.
 */
class DotPulseSync : BaseDot() {

	val translateYFloats = FloatArray(3);

	override fun draw(canvas: Canvas, paint: Paint) {
		val circleSpacing = 4;
		val radius = (getWidth() - circleSpacing * 2) / 6.0f;
		val x = getWidth() / 2 - (radius * 2 + circleSpacing);
		for (i in 0..3 - 1) {
			canvas.save();
			val translateX = x + (radius * 2) * i + circleSpacing * i;
			canvas.translate(translateX, translateYFloats[i]);
			canvas.drawCircle(0.0f, 0.0f, radius, paint);
			canvas.restore();
		}

	}

	override fun createAnimation(): List<Animator> {
		val animators = ArrayList<Animator>();
		val circleSpacing = 4;
		val radius = (getWidth() - circleSpacing * 2) / 6;
		val delays = longArrayOf(70, 140, 210);
		val width = getWidth()
		for (i in 0..2) {
			val index = i;
			val scaleAnim: ValueAnimator = ValueAnimator.ofFloat(width / 2.0f, width / 2 - radius * 2.0f, width / 2.0f);
			scaleAnim.setDuration(600);
			scaleAnim.setRepeatCount(-1);
			scaleAnim.setStartDelay(delays[i]);
			scaleAnim.addUpdateListener({ animation ->
				translateYFloats[index] = animation.getAnimatedValue() as Float;
				postInvalidate();
			});
			scaleAnim.start();
			animators.add(scaleAnim);
		}
		return animators;
	}
}