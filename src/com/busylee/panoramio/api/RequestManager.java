package com.busylee.panoramio.api;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class RequestManager {
	public final static String TAG = "RequestManager";

	protected String executeGetRequest(String url,
			final List<NameValuePair> params) throws ClientProtocolException, IOException {
		if (!url.endsWith("?"))
			url += "?";

		url += URLEncodedUtils.format(params, "utf-8");
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		String line = null;

		

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity httpEntity = response.getEntity();
		
		line = EntityUtils.toString(httpEntity, "UTF-8");

		Log.d(TAG, line);
		
		return line;
	}
}
