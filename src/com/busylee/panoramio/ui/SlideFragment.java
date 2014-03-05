package com.busylee.panoramio.ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class SlideFragment extends Fragment implements ImageLinkListener,
		LocationListener {
	public final static String ARGUMENT_DURATION = "duration";
	public final static String ARGUMENT_COUNT = "count";

	public final static int DEF_DURATION = 500;
	public final static int DEF_COUNT = 5;

	int mCount = DEF_COUNT, mDuration = DEF_DURATION;

	// views
	ImageSwitcher imageSwitcher = null;
	ProgressBar pbPreloader = null;

	// other
	SlideManager mSlideManager = null;
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
	public void onCreate(Bundle save) {
		super.onCreate(save);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View fragmentLayout = inflater.inflate(R.layout.fragment_slide, null);

		initializeViews(fragmentLayout);

		if (mSlideManager == null) {

			Bundle arguments = getArguments();
			if (arguments != null) {
				mCount = arguments.getInt(ARGUMENT_COUNT, DEF_COUNT);
				mDuration = arguments.getInt(ARGUMENT_DURATION, DEF_DURATION);
			}

			getCoords();

		} else {
			mSlideManager.updateImageSwitcher(imageSwitcher);
			hideProgressBar();
		}

		return fragmentLayout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationTimer.cancel();
		if (mSlideManager != null)
			mSlideManager.stopSlideShow();
		mLocationManager.removeUpdates(this);
	}

	public void getCoords() {
		final int minTime = 1000 * 10;
		final int minDistance = 10;
		mLocationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, minTime, minDistance, this);
			mLocationTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (panoramioApi == null)
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(
										getActivity(),
										getResources().getString(
												R.string.location_by_network),
										Toast.LENGTH_SHORT).show();
								mLocationManager.requestLocationUpdates(
										LocationManager.NETWORK_PROVIDER,
										minTime, minDistance,
										SlideFragment.this);
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

	private void initializeViews(View layoutView) {
		pbPreloader = (ProgressBar) layoutView.findViewById(R.id.pbPreloader);
		imageSwitcher = (ImageSwitcher) layoutView
				.findViewById(R.id.imageSwitcher);

		imageSwitcher.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				ImageView myView = new ImageView(getActivity()
						.getApplicationContext());
				myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				myView.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				return myView;
			}

		});

		Animation in = AnimationUtils.loadAnimation(getActivity(),
				android.R.anim.slide_in_left);
		Animation out = AnimationUtils.loadAnimation(getActivity(),
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
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(),
						getResources().getString(R.string.error_get_links),
						Toast.LENGTH_LONG).show();
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
