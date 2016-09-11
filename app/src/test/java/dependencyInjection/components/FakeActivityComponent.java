package dependencyInjection.components;

import dagger.Component;
import dependencyInjection.modules.FakeActivityModule;
import dependencyInjection.scopes.ActivityScope;

/**
 * Created by tudor on 10.08.2016.
 */
@ActivityScope
@Component(modules = FakeActivityModule.class, dependencies = FakeAppComponent.class)
public interface FakeActivityComponent extends ActivityComponent {
}
