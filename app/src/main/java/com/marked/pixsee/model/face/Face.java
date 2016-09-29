package com.marked.pixsee.model.face;

/**
 * Created by Tudor on 29-Sep-16.
 */

public abstract class Face {
	private float mLeft, mRight, mTop, mBottom;
	private float mEulerX, mEulerY, mEulerZ;


	public float centerX() {
		return (mLeft + mRight) / 2;
	}

	public float centerY() {
		return (mTop + mBottom) / 2;
	}

	public float center() {
		return (float) Math.sqrt(Math.pow(mLeft - mRight, 2.0) + Math.pow(mTop - mBottom, 2.0));
	}

	public float getLeft() {
		return mLeft;
	}

	public void setLeft(float left) {
		mLeft = left;
	}

	public float getRight() {
		return mRight;
	}

	public void setRight(float right) {
		mRight = right;
	}

	public float getTop() {
		return mTop;
	}

	public void setTop(float top) {
		mTop = top;
	}

	public float getBottom() {
		return mBottom;
	}

	public void setBottom(float bottom) {
		mBottom = bottom;
	}

	public float getEulerX() {
		return mEulerX;
	}

	public void setEulerX(float eulerX) {
		mEulerX = eulerX;
	}

	public float getEulerY() {
		return mEulerY;
	}

	public void setEulerY(float eulerY) {
		mEulerY = eulerY;
	}

	public float getEulerZ() {
		return mEulerZ;
	}

	public void setEulerZ(float eulerZ) {
		mEulerZ = eulerZ;
	}
}
