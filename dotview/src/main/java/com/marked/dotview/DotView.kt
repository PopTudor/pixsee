package com.marked.dotview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.marked.dotview.R.styleable

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


	companion object Static {
		val DEFAULT_SIZE = 45
		val DotPulse = 0

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
		val a = context.obtainStyledAttributes(attributeSet, styleable.DotView, defStyleAttr, 0);
		try {
			mId = a.getInt(styleable.DotView_dotType, DotPulse);
			mColor = a.getColor(styleable.DotView_dotColor, Color.WHITE);
		} finally {
			a.recycle();
		}
		mPaint.setColor(mColor);
		mPaint.setStyle(Paint.Style.FILL);
		applyIndicator();
	}

	private fun applyIndicator() {
		when (mId) {
			DotPulse -> mBaseDot = DotPulse()
		}
		mBaseDot?.target = this
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
		val height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	private fun dp2px(dpValue: Int): Int {
		return (getContext().getResources().getDisplayMetrics().density * dpValue).toInt()
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