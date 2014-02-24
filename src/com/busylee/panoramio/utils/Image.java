package com.busylee.panoramio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class Image implements Runnable {
	private String mUrl = null;
	private Drawable mDrawable = null;
	private boolean mReady = false;

	public Image(String url) {
		mUrl = url;
	}

	@Override
	public void run() {
		try {
			mDrawable = drawableFromUrl(mUrl);

			setAsReady();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Drawable drawableFromUrl(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url)
				.openConnection();
		connection.connect();
		InputStream input = connection.getInputStream();
		return Drawable.createFromStream(input, url);

	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	private void setAsReady() {
		mReady = true;
	}

	public boolean isReady() {
		return mReady;
	}

}
