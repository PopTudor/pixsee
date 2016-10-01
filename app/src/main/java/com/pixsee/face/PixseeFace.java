package com.pixsee.face;

/**
 * Created by Tudor on 29-Sep-16.
 * PixseeFace keeps away other 'Face' classes from 3rd party vendors.
 * If we ever want to switch Google Mobile Vision 'Face' class with
 * default Android  or OpenCV 'Face' classes, then one should wrap
 * those classes in a subclass of PixseeFace and delegate method calls
 * to those classes. One should also update this class's properties
 * when delegating calls or overriding one of the methods below
 */

public abstract class PixseeFace {
	protected float mPositionX, mPositionY, mWidth, mHeight;
	protected float mEulerX, mEulerY, mEulerZ;

	/**
	 * Get the distance of the face translated with an offset of positionX.
	 * Helps in keeping the object on the face by keeping the face's width constant
	 * but modifying the x coordinate
	 *
	 * @return
	 */
	public float centerX() {
		return mPositionX + (mWidth / 2);
	}

	/**
	 * Get the distance of the face translated with an offset of positionY.
	 * Helps in keeping the object on the face by keeping the face's height constant
	 * but modifying the y coordinate
	 * @return
	 */
	public float centerY() {
		return mPositionY + (mHeight / 2);
	}

	public float distance() {
		return (float) Math.sqrt(Math.pow(mPositionX - mWidth, 2.0) + Math.pow(mPositionY - mHeight, 2.0));
	}

	public float getPositionX() {
		return mPositionX;
	}

	public void setPositionX(float positionX) {
		mPositionX = positionX;
	}

	public float getWidth() {
		return mWidth;
	}

	public void setWidth(float width) {
		mWidth = width;
	}

	public float getPositionY() {
		return mPositionY;
	}

	public void setPositionY(float positionY) {
		mPositionY = positionY;
	}

	public float getHeight() {
		return mHeight;
	}

	public void setHeight(float height) {
		mHeight = height;
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
