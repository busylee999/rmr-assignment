package com.busylee.panoramio.utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageFetcher extends ImagePool {
	
	private final ExecutorService mExecutor;
	
	public ImageFetcher(){
		super();
		mExecutor = Executors.newSingleThreadExecutor();
		
	}
	
	public void initImages(ArrayList<String> imgUrls){
		for (String imgUrl : imgUrls) {
			addImage(imgUrl);
		}
	}
	
	private void addImage(String imgUrl){
		Image newImage = new Image(imgUrl);
		addImage(newImage);
		execute(newImage);
	}
	
	private void execute(Runnable runnable){
		mExecutor.execute(runnable);
	}
	
	
}
