package dependencyInjection.components;

import android.app.Application;

import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import dependencyInjection.modules.AppModule;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by Tudor Pop on 17-Mar-16.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
	Application application();

	@Named(ServerConstants.SERVER)
	Retrofit retrofit();

	OkHttpClient okhttpclient();
}
