package com.marked.pixsee.main;

import com.marked.pixsee.main.strategy.ProfilePictureStrategyTest;
import com.marked.pixsee.main.strategy.ShareStrategyTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Tudor on 25-Jul-16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(value = {MainActivityTest.class, MainPresenterTest.class, RemoteMessageToUserMapperTest.class
		, ProfilePictureStrategyTest.class, ShareStrategyTest.class
})
public class MainTestSuite {
}
