package com.marked.pixsee.shop.list;

import android.graphics.drawable.Drawable;

import java.util.UUID;

/**
 * Created by Tudor on 2016-05-11.
 */
public class Category {
	private String id = UUID.randomUUID().toString();
	private String path;
	private Drawable icon;
	private Drawable background;
	private String title;
	private String subtitle;
	private String description;
	private int numOfItems;

	public Category(String path, Drawable icon, Drawable background, String title, String subtitle, String description, int numOfItems) {
		this.path = path;
		this.icon = icon;
		this.background = background;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.numOfItems = numOfItems;
	}
	public Category(Drawable icon, Drawable background, String title, String subtitle, String description, int numOfItems) {
		this(null, icon, background, title, subtitle, description, numOfItems);
	}
	public Category(Drawable icon, Drawable background, String title, String subtitle, String description) {
		this(null, icon, background, title, subtitle, description, -1);
	}
	public Category(Drawable background, String title, String subtitle, String description, int numOfItems) {
		this(null, null, background, title, subtitle, description, numOfItems);
	}
	public Category(String title, String subtitle, String description, int numOfItems) {
		this(null, null, null, title, subtitle, description, numOfItems);
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

	public int getNumOfItems() {
		return numOfItems;
	}

	public String getId() {
		return id;
	}
}
