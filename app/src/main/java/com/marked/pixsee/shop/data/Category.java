package com.marked.pixsee.shop.data;

import android.graphics.drawable.Drawable;

/**
 * Created by Tudor on 2016-05-11.
 */
public class Category {
	private String path;
	private Drawable icon;
	private Drawable background;
	private String title;
	private String subtitle;

	public Category(Drawable icon, Drawable background, String title, String subtitle) {
		this.icon = icon;
		this.background = background;
		this.title = title;
		this.subtitle = subtitle;
	}

	public Drawable getIcon() {
		return icon;
	}

	public Drawable getBackground() {
		return background;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getPath() {
		return path;
	}
}
