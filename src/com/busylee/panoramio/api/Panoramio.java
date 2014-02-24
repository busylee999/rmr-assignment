package com.busylee.panoramio.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Panoramio extends RequestManager {
	private final static String LAT_FROM = "miny";
	private final static String LON_FROM = "minx";
	private final static String LAT_TO = "maxy";
	private final static String LON_TO = "maxx";
	private final static String FROM = "from";
	private final static String TO = "to";
	private final static String SET = "set";
	private final static String DEF_SET = "full";
	
	private final static String JSON_PHOTOS = "photos";
	private final static String JSON_PHOTO_URL = "photo_file_url";

	private final static String REQUEST_URL = "http://www.panoramio.com/map/get_panoramas.php";

	protected String getPanoramasJsonString(int count, double lon, double lat) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new LinkedList<NameValuePair>();

		params.add(new BasicNameValuePair(SET, DEF_SET));
		params.add(new BasicNameValuePair(FROM, String.valueOf(0)));
		params.add(new BasicNameValuePair(TO, String.valueOf(count)));
		params.add(new BasicNameValuePair(LAT_FROM, String.valueOf(lat - 5)));
		params.add(new BasicNameValuePair(LON_FROM, String.valueOf(lon - 5)));
		params.add(new BasicNameValuePair(LAT_TO, String.valueOf(lat + 5)));
		params.add(new BasicNameValuePair(LON_TO, String.valueOf(lon + 5)));

		return executeGetRequest(REQUEST_URL, params);

	}

	protected ArrayList<String> getPanoramasLinks(int count, double lon, double lat) throws JSONException, ClientProtocolException, IOException{
		ArrayList<String> images = new ArrayList<String>();
		JSONObject jsonObject = new JSONObject((getPanoramasJsonString( count, lon, lat)));
		JSONArray jsonPhotosArray = jsonObject.getJSONArray((JSON_PHOTOS));
		for(int i = 0; i < jsonPhotosArray.length() && i < count; i++){
			JSONObject jsonPhoto = jsonPhotosArray.getJSONObject(i);
			images.add(jsonPhoto.getString(JSON_PHOTO_URL));
		}
		return images;
	}
}
