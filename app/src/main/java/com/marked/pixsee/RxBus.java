package com.marked.pixsee;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Tudor on 21-Jul-16.
 */
public class RxBus {
	private static final RxBus INSTANCE = new RxBus();
	private final Subject<Object, Object> mBusSubject = new SerializedSubject<>(PublishSubject.create());

	public static RxBus getInstance() {
		return INSTANCE;
	}

	public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext) {
		return mBusSubject
				.ofType(eventClass)
				.subscribe(onNext);
	}
	public void post(Object event) {
		mBusSubject.onNext(event);
	}
}
