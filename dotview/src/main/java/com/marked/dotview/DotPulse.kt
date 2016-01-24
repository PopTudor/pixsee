package com.marked.dotview

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.AccelerateDecelerateInterpolator
import java.util.*

/**
 * Created by Tudor Pop on 21-Jan-16.
 */
class DotPulse : BaseDot() {
	val SIZE = 0.5f
	private val scaleFloats = floatArrayOf(SIZE, SIZE, SIZE);

	override
	public fun draw(canvas: Canvas, paint: Paint) {
		val circleSpacing = 8;
		val radius = (Math.min(getWidth(), getHeight()) - circleSpacing * 2) / 6.5f;
		val x = getWidth() / 2 - (radius * 2 + circleSpacing);
		val y = getHeight() / 2.0f;
		for (i in 0..2) {
			canvas.save();
			val translateX = x + (radius * 2.0f) * i + circleSpacing * i;
			canvas.translate(translateX, y);
			canvas.scale(scaleFloats[i], scaleFloats[i]);
			canvas.drawCircle(0.0f, 0.0f, radius, paint);
			canvas.restore();
		}
	}

	override
	public fun createAnimation(): List<Animator> {
		val animators = ArrayList<Animator>();
		val delays = longArrayOf(250, 500, 750);

		for (i in 0..2) {
			val index = i;

			val scaleAnim: ValueAnimator = ValueAnimator.ofFloat(0.5f, 1.0f, 0.5f);

			scaleAnim.setDuration(1000);
			scaleAnim.repeatCount = -1;
			scaleAnim.startDelay = delays[i];
			scaleAnim.interpolator = AccelerateDecelerateInterpolator()
			scaleAnim.addUpdateListener({ animation ->
				scaleFloats[index] = animation.animatedValue as Float;
				postInvalidate();
			});
			scaleAnim.start();
			animators.add(scaleAnim);
		}
		return animators;
	}
}