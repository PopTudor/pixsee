package dependencyInjection.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.model.database.DatabaseContract;
import com.marked.pixsee.model.database.PixyDatabase;
import com.marked.pixsee.model.user.User;
import com.marked.pixsee.model.user.UserDatasource;
import com.marked.pixsee.model.user.UserDiskDatasource;
import com.marked.pixsee.model.user.UserNetworkDatasource;
import com.marked.pixsee.model.user.UserRepository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dependencyInjection.scopes.ActivityScope;
import dependencyInjection.scopes.Local;
import dependencyInjection.scopes.Remote;
import dependencyInjection.scopes.Repository;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Module
@ActivityScope
public class ActivityModule {
	private AppCompatActivity activity;

	public ActivityModule(AppCompatActivity activity) {
		this.activity = activity;
	}

	@Provides
	@ActivityScope
	ProgressDialog provideProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setTitle("Login");
		dialog.setMessage("Please wait...");
		dialog.setIndeterminate(true);
		return dialog;
	}

	@Provides
	@ActivityScope
	LocalBroadcastManager provideLocalBroadcastManager() {
		return LocalBroadcastManager.getInstance(activity);
	}

	@Provides
	@ActivityScope
	SharedPreferences provideSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(activity);
	}

	@Provides
	@ActivityScope
	AppCompatActivity provideActivity() {
		return activity;
	}

	@Provides
	@ActivityScope
	Context provideContext() {
		return activity;
	}

	@Provides
	@ActivityScope
	SQLiteOpenHelper provideDatabase() {
		return PixyDatabase.getInstance(activity);
	}

	@Provides
	@ActivityScope
	@Local
	UserDatasource provideUserRepositoryLocal(SQLiteOpenHelper database) {
		return new UserDiskDatasource(database);
	}
	@Provides
	@ActivityScope
	@Remote
	UserDatasource provideUserRepositoryRemote(SharedPreferences preferences) {
		return new UserNetworkDatasource(preferences);
	}

	@Provides
	@ActivityScope
	@Repository
	UserDatasource provideUserRepository(@Local UserDatasource local, @Remote UserDatasource remote) {
		return new UserRepository(local,remote);
	}
	@Provides
	@ActivityScope
	@Named(DatabaseContract.AppsUser.TABLE_NAME)
	User provideAppsUser(@Repository UserDatasource repository){
		return repository.getUser(DatabaseContract.AppsUser.TABLE_NAME);
	}
}
