package com.marked.pixsee.shop.list;

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
	private String description;

	public Category(Drawable icon, Drawable background, String title, String subtitle,String description) {
		this.icon = icon;
		this.background = background;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

}
