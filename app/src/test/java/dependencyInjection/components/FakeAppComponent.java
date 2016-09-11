package dependencyInjection.components;

import javax.inject.Singleton;

import dagger.Component;
import dependencyInjection.modules.FakeAppModule;

/**
 * Created by Tudor on 22-Jul-16.
 */
@Singleton
@Component(modules = {FakeAppModule.class})
public interface FakeAppComponent extends AppComponent {
	
}