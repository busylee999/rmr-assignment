package com.busylee.panoramio.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class ImagePool {

	private ArrayList<Image> mImagePool;
	private Iterator<Image> mIterator = null;
	
	
	public ImagePool() {
		mImagePool = new ArrayList<Image>();
	}
	
	protected void addImage(Image image){
		mImagePool.add(image);
	}

	protected Image nextImage(){
		if (mIterator == null)
			mIterator = mImagePool.iterator();
		
		if (!mIterator.hasNext())
			mIterator = mImagePool.iterator();
		return mIterator.next();
	}
	
	protected boolean hasImages(){
		return mImagePool.size() > 0;
	}
}