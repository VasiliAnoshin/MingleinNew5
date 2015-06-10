package com.minglein.minglein;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

	/** Duration of wait **/
	private final int SPLASH_DISPLAY_LENGTH = 2000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_splash);

		AlertDialog dialog = noInternetDialog(this);

		if (!isNetworkAvailable()) {
			dialog.show();
		} else {
			buildHandler();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	private AlertDialog noInternetDialog(Context c) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("No Internet connection.");
		builder.setMessage("You have no internet connection");

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (isNetworkAvailable()) {
					dialog.dismiss();
					buildHandler();
				}
			}
		});

		return builder.create();
	}

	/**
	 * New Handler to start the Menu-Activity and close this Splash-Screen after
	 * some seconds.
	 */
	private void buildHandler() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				/* Create an Intent that will start the Menu-Activity. */
				Intent mainIntent = new Intent(Splash.this,
						LinkedinConnection.class);
				Splash.this.startActivity(mainIntent);
				Splash.this.finish();
			}
		}, SPLASH_DISPLAY_LENGTH);
	}
}
