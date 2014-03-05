package com.busylee.panoramio.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.busylee.panoramio.R;

public class StartActivity extends FragmentActivity implements OnSeekBarChangeListener,
		OnClickListener {

	private TextView mTvDurationValue, mTvCountValue;
	private SeekBar mSbDuration, mSbCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_start);
		initializeViews();
	}

	private void initializeViews() {
		mSbDuration = (SeekBar) findViewById(R.id.sbDuration);
		mSbDuration.setOnSeekBarChangeListener(this);

		mSbCount = (SeekBar) findViewById(R.id.sbCount);
		mSbCount.setOnSeekBarChangeListener(this);

		mTvDurationValue = (TextView) findViewById(R.id.tvDurationValue);
		mTvCountValue = (TextView) findViewById(R.id.tvCountValue);

		Button btnStart = (Button) findViewById(R.id.btnStart);
		btnStart.setOnClickListener(this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar.getId() == R.id.sbDuration) {
			int duration = SlideFragment.DEF_DURATION + progress;
			mTvDurationValue.setText(String.valueOf(duration));
		} else if (seekBar.getId() == R.id.sbCount) {
			int count = SlideFragment.DEF_COUNT + progress;
			mTvCountValue.setText(String.valueOf(count));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	public boolean checkConnection() {
		ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectionManager.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnected()
				&& netInfo.isAvailable();
	}

	@Override
	public void onClick(View v) {
		if (!checkConnection()) {
			Toast.makeText(
					this,
					getResources()
							.getString(R.string.error_internet_connection),
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		showSLideFragment();
	}
	
	protected void showSLideFragment(){
		FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
		
		fTrans.replace(R.id.flSlideFrame,getSlideFragmentInstance(mSbDuration.getProgress()
				+ SlideFragment.DEF_DURATION, mSbCount.getProgress()
				+ SlideFragment.DEF_COUNT));
		
		fTrans.commit();
	}
	
	protected SlideFragment getSlideFragmentInstance(int duration, int count){
		SlideFragment sfSlider = new SlideFragment();
		Bundle args = new Bundle();
	    args.putInt(SlideFragment.ARGUMENT_DURATION, duration);
	    args.putInt(SlideFragment.ARGUMENT_COUNT, count);
	    sfSlider.setArguments(args);
	    return sfSlider;
	}
}
