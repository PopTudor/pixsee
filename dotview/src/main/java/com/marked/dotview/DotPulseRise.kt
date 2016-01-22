package com.marked.dotview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator
import java.util.*

/**
 * Created by Tudor Pop on 22-Jan-16.
 */
class DotPulseRise : BaseDot() {
	override fun draw(canvas: Canvas, paint: Paint) {
		val width = getWidth().toFloat()
		val radius = width / 10.toFloat()
		canvas.drawCircle(width / 4, width * 2, radius, paint)
		canvas.drawCircle(width * 3 / 4, radius * 2, radius, paint);

		canvas.drawCircle(radius, getHeight() - 2 * radius, radius, paint);
		canvas.drawCircle(width / 2, getHeight() - 2 * radius, radius, paint);
		canvas.drawCircle(width - radius, getHeight() - 2 * radius, radius, paint);

	}

	override fun createAnimation(): List<Animator> {
		val rotation6 = PropertyValuesHolder.ofFloat("rotationX", 0.0f, 360.0f);
		val animator: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(target, rotation6);
		animator.setInterpolator(LinearInterpolator());
		animator.setRepeatCount(-1);
		animator.setDuration(1500);
		animator.start();
		val animators = ArrayList<Animator>();
		animators.add(animator);
		return animators;
	}
}