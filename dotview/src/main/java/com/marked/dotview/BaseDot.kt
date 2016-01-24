package com.marked.dotview

import android.animation.Animator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

/**
 * Created by Tudor Pop on 21-Jan-16.
 * All the other views inherit from this view.
 * Those views will decide for themselves the way they look and what animations they have
 * Check out Factory Method Pattern
 */
abstract class BaseDot {
	var target: View? = null
	private var mAnimators: List<Animator>? = null

	protected var mDuration = 1000
	protected var mCircleSpacing = 8;
	protected var mRepeatCount = -1;

	fun getWidth(): Int {
		return target?.width ?: 0
	}

	fun getHeight(): Int {
		return target?.height ?: 0
	}

	fun postInvalidate(): Unit {
		target?.postInvalidate()
	}

	abstract fun draw(canvas: Canvas, paint: Paint)
	abstract fun createAnimation(): List<Animator>

	fun initAnimation() {
		mAnimators = createAnimation()
	}

	public fun setAnimationStatus(animStatus: AnimStatus) {
		if (mAnimators == null) return

		var count = mAnimators?.size?.minus(1) ?: 0
		if (count <= 0) ++count
		for (i in 0..count) {
			val animator = mAnimators?.get(i);
			val isRunning = animator?.isRunning ?: false;
			when (animStatus) {
				AnimStatus.START ->
					if (!isRunning) animator?.start()
				AnimStatus.END ->
					if (isRunning) {
						animator?.end()
					}
				AnimStatus.CANCEL ->
					if (isRunning) animator?.cancel()
			}
		}
	}

	public enum class AnimStatus {
		START, END, CANCEL
	}

}