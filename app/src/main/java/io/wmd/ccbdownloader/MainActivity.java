package io.wmd.ccbdownloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import io.wmd.ccbdownloader.controllers.MainController;
import io.wmd.ccbdownloader.screens.MainScreen;
import okhttp3.OkHttpClient;

public class MainActivity extends Activity {

	MainScreen mainScreen;

	MainController mainController;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.INSTANCE.setActiveActivity(this);
		OkHttpClient client = new OkHttpClient();

		mainScreen = new MainScreen();
		mainController = new MainController(mainScreen, client);

		mainScreen.initialize(this);
		setContentView(mainScreen.show(this));
		mainController.process();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mainController.saveState();
	}
}

