package com.marked.pixsee.data.repository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import rx.Observable;

/**
 * Created by Tudor Pop on 15-Mar-16.
 */
public interface Repository<T> {
	void add(@NotNull T item);

	void add(@NotNull List<T> items);

	void update(@NotNull T item);

	void remove(@NotNull T item);

	void remove(Specification specification);

	Observable<List<T>> query(Specification specification);
}
