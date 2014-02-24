package com.busylee.panoramio.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.busylee.panoramio.R;

public class StartActivity extends Activity implements OnSeekBarChangeListener,
		OnClickListener {

	private TextView mTvDurationValue, mTvCountValue;
	private SeekBar mSbDuration, mSbCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
			int duration = SlideActivity.DEF_DURATION + progress;
			mTvDurationValue.setText(String.valueOf(duration));
		} else if (seekBar.getId() == R.id.sbCount) {
			int count = SlideActivity.DEF_COUNT + progress;
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
		Intent intent = new Intent(this, SlideActivity.class);
		intent.putExtra(SlideActivity.EXTRA_DURATION, mSbDuration.getProgress()
				+ SlideActivity.DEF_DURATION);
		intent.putExtra(SlideActivity.EXTRA_COUNT, mSbCount.getProgress()
				+ SlideActivity.DEF_COUNT);
		startActivity(intent);
	}
}
