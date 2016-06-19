package com.marked.pixsee;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

/**
 * Created by Tudor on 19-Jun-16.
 */
public class TestSchedulerProxy {
	private static final Scheduler SCHEDULER = Schedulers.immediate();
	private static final TestSchedulerProxy INSTANCE = new TestSchedulerProxy();

	static {
		try {
			Schedulers.reset();
			RxJavaPlugins.getInstance().reset();
			RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
				@Override
				public Scheduler getIOScheduler() {
					return SCHEDULER;
				}

				@Override
				public Scheduler getComputationScheduler() {
					return SCHEDULER;
				}

				@Override
				public Scheduler getNewThreadScheduler() {
					return SCHEDULER;
				}
			});
			RxAndroidPlugins.getInstance().reset();
			RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
				@Override
				public Scheduler getMainThreadScheduler() {
					return SCHEDULER;
				}
			});
		} catch (IllegalStateException e) {
			throw new IllegalStateException("Schedulers class already initialized. " +
					"Ensure you always use the TestSchedulerProxy in unit tests.");
		}
	}

	public static TestSchedulerProxy get() {
		return INSTANCE;
	}
}