package com.marked.pixsee.injection;

/**
 * Created by Tudor on 26-Jul-16.
 * Inherit this interface in production code and then in tests create a private class that inherits the
 * production activity/fragment and override this method to be able to switch the component and it's modules
 */
public interface Injectable {

	/**
	 * override this for easy swap of dependencies
	 */
	void injectComponent();
}
