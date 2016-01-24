package com.marked.dotview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.marked.dotview.R.styleable
import com.marked.dotview.Utils.toPaintStyle

/**
 * Created by Tudor Pop on 22-Jan-16.
 */
class DotView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : View(context, attrs, defStyleAttr, defStyleRes) {
	/* attrs */
	var mId = 0;
	var mColor = 0;
	val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
	var mBaseDot: BaseDot? = null
	var mHasAnimation = false

	var mFill = Paint.Style.FILL
	var mStrokeWidth = 1.0f

	var mDotSize = 45.0f
	val mBundle = Bundle()


	companion object Static {
		val DotPulse = 0
		val DotPulseSync = 1
		val TAG_DURATION = "TAG_DURATION"
		val TAG_SPACING = "TAG_SPACING"
		val TAG_REPEAT_COUNT = "TAG_REPEAT_COUNT"
	}

	init {
		init(attrs, defStyleAttr)
	}

	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0) {
		init(attrs, defStyleAttr)
	}

	constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0, 0) {
		init(attrs, 0)
	}

	constructor(context: Context?) : this(context, null, 0, 0) {
		init(null, 0)
	}

	fun init(attributeSet: AttributeSet?, defStyleAttr: Int) {
		val a = context.obtainStyledAttributes(attributeSet, styleable.DotView, defStyleAttr, 0)
		try {
			mId = a.getInt(styleable.DotView_dotType, DotPulse)
			mColor = a.getColor(styleable.DotView_dotColor, Color.WHITE)
			mFill = a.getInt(styleable.DotView_dotFill, 0).toPaintStyle()
			mStrokeWidth = a.getFloat(styleable.DotView_dotStrokeWidth, 1.0f)
			mDotSize = a.getFloat(styleable.DotView_dotSize, 45.0f)

			/* FROM HERE WE SET ATTRS TO BE SENT TO DOT CLASSES*/
			mBundle.putInt(TAG_DURATION, a.getInt(styleable.DotView_dotDuration, 1000))
			mBundle.putInt(TAG_SPACING, a.getInt(styleable.DotView_dotSpacing, 10))
			mBundle.putInt(TAG_REPEAT_COUNT, a.getInt(styleable.DotView_dotRepeatCount, -1))
		} finally {
			a.recycle();
		}
		mPaint.color = mColor;
		mPaint.style = mFill;
		mPaint.strokeWidth = dp2px(mStrokeWidth).toFloat()

		selectDotType();
	}


	private fun selectDotType() {
		when (mId) {
			DotPulse -> mBaseDot = DotPulse(mBundle)
			DotPulseSync -> mBaseDot = DotPulseSync()
		}
		mBaseDot?.target = this
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val dpToPx = dp2px(mDotSize)
		val width = measureDimension(dpToPx, widthMeasureSpec);
		val height = measureDimension(dpToPx, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	private fun dp2px(dpValue: Float): Int {
		return (context.resources.displayMetrics.density * dpValue).toInt()
	}

	private fun measureDimension(defaultSize: Int, measureSpec: Int): Int {
		var result = defaultSize;
		val specMode = MeasureSpec.getMode(measureSpec);
		val specSize = MeasureSpec.getSize(measureSpec);
		when (specMode) {
			MeasureSpec.EXACTLY -> {
				result = specSize;
			}
			MeasureSpec.AT_MOST -> {
				result = Math.min(defaultSize, specSize);
			}
			else -> {
				result = defaultSize;
			}
		}
		return result;
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		mBaseDot?.setAnimationStatus(BaseDot.AnimStatus.CANCEL);
	}

	override fun setVisibility(visibility: Int) {
		if (getVisibility() != visibility ) {
			super.setVisibility(visibility);
			if (visibility == GONE || visibility == INVISIBLE)
				mBaseDot?.setAnimationStatus(BaseDot.AnimStatus.END)
			else
				mBaseDot?.setAnimationStatus(BaseDot.AnimStatus.START)
		}
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		mBaseDot?.draw(canvas, mPaint)
	}

	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		if (!mHasAnimation) {
			mHasAnimation = true;
			mBaseDot?.initAnimation();
		}
	}
}