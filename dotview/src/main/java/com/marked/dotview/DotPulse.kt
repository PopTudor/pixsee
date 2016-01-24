package com.marked.dotview

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import java.util.*

/**
 * Created by Tudor Pop on 21-Jan-16.
 */
class DotPulse(bundle: Bundle) : BaseDot() {
	val SIZE = 0.5f
	private val scaleFloats = floatArrayOf(SIZE, SIZE, SIZE);

	init {
		mDuration = bundle.getInt(DotView.TAG_DURATION)
		mCircleSpacing = bundle.getInt(DotView.TAG_SPACING)
		mRepeatCount = bundle.getInt(DotView.TAG_REPEAT_COUNT)
	}

	override
	public fun draw(canvas: Canvas, paint: Paint) {
		val radius = (Math.min(getWidth(), getHeight()) - mCircleSpacing * 2) / 6.0f;
		val x = getWidth() / 2 - (radius * 2 + mCircleSpacing);
		val y = getHeight() / 2.0f;
		for (i in 0..2) {
			canvas.save();
			val translateX = x + (radius * 2.0f) * i + mCircleSpacing * i;
			canvas.translate(translateX, y);
			canvas.scale(scaleFloats[i], scaleFloats[i]);
			canvas.drawCircle(0.0f, 0.0f, radius, paint);
			canvas.restore();
		}
	}

	override
	public fun createAnimation(): List<Animator> {
		val animators = ArrayList<Animator>();
		val delays = longArrayOf((mDuration * 0.25).toLong(), (mDuration * 0.5).toLong(), (mDuration * 0.75).toLong());

		for (i in 0..2) {
			val index = i;

			val scaleAnim: ValueAnimator = ValueAnimator.ofFloat(0.5f, 1.0f, 0.5f);

			scaleAnim.setDuration(mDuration.toLong());
			scaleAnim.repeatCount = mRepeatCount;
			scaleAnim.startDelay = delays[i];
			scaleAnim.interpolator = AccelerateInterpolator()
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