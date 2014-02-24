package com.busylee.panoramio.ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.busylee.panoramio.R;
import com.busylee.panoramio.api.PanoramioAsync;
import com.busylee.panoramio.api.PanoramioAsync.ImageLinkListener;
import com.busylee.panoramio.utils.SlideManager;

@SuppressLint("HandlerLeak")
public class SlideActivity extends Activity implements ImageLinkListener,
		LocationListener {
	public final static String EXTRA_DURATION = "duration";
	public final static String EXTRA_COUNT = "count";

	public final static int DEF_DURATION = 500;
	public final static int DEF_COUNT = 5;

	int mCount = DEF_COUNT, mDuration = DEF_DURATION;

	// views
	ImageSwitcher imageSwitcher = null;
	ProgressBar pbPreloader = null;

	// other
	SlideManager mSlideManager;
	PanoramioAsync panoramioApi = null;
	Timer mLocationTimer = new Timer();

	// location
	LocationManager mLocationManager;

	Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == SlideManager.SHOW_PRELODER)
				showProgressBar();
			else if (msg.arg1 == SlideManager.OFF_PRELODER)
				hideProgressBar();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.activity_slide);

		initializeViews();

		Intent intent = getIntent();
		if (intent != null) {
			mCount = intent.getIntExtra(EXTRA_COUNT, DEF_COUNT);
			mDuration = intent.getIntExtra(EXTRA_DURATION, DEF_DURATION);
		}

		getCoords();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationTimer.cancel();
		if (mSlideManager != null)
			mSlideManager.stopSlideShow();
	}

	public void getCoords() {
		final int minTime = 1000 * 10;
		final int minDistance = 10;
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, minTime, minDistance, this);
			mLocationTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (panoramioApi == null)
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
										SlideActivity.this,
										getResources().getString(
												R.string.location_by_network),
										Toast.LENGTH_SHORT).show();
								mLocationManager.requestLocationUpdates(
										LocationManager.NETWORK_PROVIDER,
										minTime, minDistance,
										SlideActivity.this);
							}
						});

				}
			}, 10 * 1000);
		} else if (mLocationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, minTime, minDistance,
					this);
		}
	}

	private void initializeViews() {
		pbPreloader = (ProgressBar) findViewById(R.id.pbPreloader);
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);

		imageSwitcher.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				ImageView myView = new ImageView(getApplicationContext());
				myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				myView.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				return myView;
			}

		});

		Animation in = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left);
		Animation out = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
		imageSwitcher.setInAnimation(in);
		imageSwitcher.setOutAnimation(out);
	}

	@Override
	public void onLinksGetting(ArrayList<String> imageLinks) {
		mSlideManager = new SlideManager(imageSwitcher, uiHandler, mDuration);
		mSlideManager.initImages(imageLinks);
		mSlideManager.startSlideShow();
	}

	@Override
	public void onError(Exception e) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(SlideActivity.this,
						getResources().getString(
								R.string.error_get_links), Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	public void showProgressBar() {
		pbPreloader.setVisibility(View.VISIBLE);
	}

	public void hideProgressBar() {
		pbPreloader.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (panoramioApi == null) {
			panoramioApi = new PanoramioAsync();
			panoramioApi.getPictures(mCount, location.getLongitude(),
					location.getLatitude(), this);
			mLocationManager.removeUpdates(this);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

}
