package com.busylee.panoramio.api;

import java.util.ArrayList;

public class PanoramioAsync extends Panoramio {

	public void getPictures(final int count, final double lon,
			final double lat, final ImageLinkListener listener) {
		(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					listener.onLinksGetting(getPanoramasLinks(count, lon, lat));
				} catch (Exception e) {
					e.printStackTrace();
					listener.onError(e);
				}
			}
		})).start();

	}

	public interface ImageLinkListener {

		public void onLinksGetting(ArrayList<String> imageLinks);
		public void onError(Exception e);
	}
}
