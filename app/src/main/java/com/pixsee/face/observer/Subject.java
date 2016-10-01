package com.pixsee.face.observer;

/**
 * Created by Tudor on 01-Oct-16.
 */

public interface Subject {
	void subscribe(Observer observer);

	void unSubscribe(Observer observer);

	void notifyObservers();
}
