package com.devpkjain.instadata;

import org.json.JSONException;
import org.json.JSONObject;

public class InstaImage {
	private String url = null;
	private int width = 0;
	private int height = 0;
	public InstaImage(String url, int width, int height) {
		super();
		this.url = url;
		this.width = width;
		this.height = height;
	}
	public InstaImage(JSONObject jsonObject) {
		super();
		try {
			this.url = jsonObject.getString("url");
			this.width = Integer.parseInt(jsonObject.getString("width"));
			this.height = Integer.parseInt(jsonObject.getString("height"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
