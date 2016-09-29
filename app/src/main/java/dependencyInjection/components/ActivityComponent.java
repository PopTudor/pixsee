package dependencyInjection.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.model.database.DatabaseContract;
import com.marked.pixsee.model.user.User;
import com.marked.pixsee.model.user.UserDatasource;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;

import dagger.Component;
import dependencyInjection.modules.ActivityModule;
import dependencyInjection.scopes.ActivityScope;
import dependencyInjection.scopes.Local;
import dependencyInjection.scopes.Remote;
import dependencyInjection.scopes.Repository;
import retrofit2.Retrofit;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Component(modules = {ActivityModule.class},dependencies = AppComponent.class)
@ActivityScope
public interface ActivityComponent {
	AppCompatActivity provideAppCompatActivity();

	Context provideContext();

	SQLiteOpenHelper provideDatabase();

	@Local
	UserDatasource provideLocalDatasource();

	@Remote
	UserDatasource provideRemoteDatasource();

	@Repository
	UserDatasource provideUserRepository();

	@Named(ServerConstants.SERVER)
	Retrofit retrofit();

	@Named(DatabaseContract.AppsUser.TABLE_NAME)
	User provideUser();

	SharedPreferences provideSharedPreferences();
}
