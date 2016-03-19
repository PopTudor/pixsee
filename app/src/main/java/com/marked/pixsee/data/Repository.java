package com.marked.pixsee.data;

/**
 * Created by Tudor Pop on 15-Mar-16.
 */
public interface Repository {
	void clear();

	int length();

	void loadMore(int num);
}