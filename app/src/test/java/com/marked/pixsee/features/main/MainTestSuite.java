package com.marked.pixsee.features.main;


import com.marked.pixsee.features.main.strategy.ProfilePictureStrategyTest;
import com.marked.pixsee.features.main.strategy.ShareStrategyTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Tudor on 25-Jul-16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
		MainActivityTest.class,
		MainPresenterTest.class,
		ProfilePictureStrategyTest.class,
		ShareStrategyTest.class
})
public class MainTestSuite {
}
