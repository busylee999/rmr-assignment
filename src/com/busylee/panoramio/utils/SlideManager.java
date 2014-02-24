package com.busylee.panoramio.utils;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageSwitcher;

public class SlideManager extends ImageFetcher {
	public final static int SHOW_PRELODER = 1;
	public final static int OFF_PRELODER = 0;

	private ImageSwitcher mImageSwitcher;
	private int mShowTime = 500;
	private Handler mHandler;
	private boolean mRun = true;
	
	private Thread mSlideThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			if (!hasImages())
				return;
			
			while (mRun){
				slideNext();
			}
			
		}
	});
	
	public SlideManager(ImageSwitcher imageSwitcher, Handler handler, int showTime){
		mImageSwitcher = imageSwitcher;
		mHandler = handler;
		mShowTime = showTime;
	}
	
	public void setImageSwitcher(ImageSwitcher mImageSwitcher) {
		this.mImageSwitcher = mImageSwitcher;
	}

	public void startSlideShow(){
		mRun = true;
		mSlideThread.start();
	}
	
	public void stopSlideShow(){
		mRun = false;
	}
	
	private void showPreloader() {
		Message msg = new Message();
		msg.arg1 = SHOW_PRELODER;
		mHandler.sendMessage(msg);
	}
	
	private void offPreloader(){
		Message msg = new Message();
		msg.arg1 = OFF_PRELODER;
		mHandler.sendMessage(msg);
	}

	private void showImage(final Drawable drawable) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mImageSwitcher.setImageDrawable(drawable);
			}
		});
	}
	
	private void slideNext(){
		boolean preloader = false; 
		final Image image = nextImage();
		if (!image.isReady()){
			showPreloader();
			preloader = true;
		}
		
		while(!image.isReady()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (preloader)
			offPreloader();
		
		showImage(image.getDrawable());
		
		try {
			Thread.sleep(mShowTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
