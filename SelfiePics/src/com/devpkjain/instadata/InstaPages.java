package com.devpkjain.instadata;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InstaPages {
	public static int PAGE_SIZE = 10 ;
	public static String PAGE_ID_REQ = null ;
	public static String PAGE_ID_MAX = null ;
	public static int WHAT_FETCH_PAGES = 2;
	public static String NEXT_URL = "next_url";
	public static String MIN_TAG_ID = "min_tag_id";
	public static String NEXT_MIN_ID = "next_min_id";
	public static String NEXT_MAX_ID = "next_max_id";
	public static String NEXT_MAX_TAG_ID = "next_max_tag_id";

	private String strNextUrl = null;
	private String strMin_tag_id = null;
	private String strNext_min_id = null;
	private String strNext_max_id = null;
	private String strNext_max_tag_id = null;
	public String getMaxTadId(){
		return strNext_max_tag_id;
	}
	private ArrayList<PageItems> pageItems = new ArrayList<PageItems>();

	public ArrayList<PageItems> getPageItems() {
		return pageItems;
	}

	public InstaPages(String strNextUrl, String strMin_tag_id,
			String strNext_min_id, String strNext_max_id,
			String strNext_max_tag_id) {
		super();
		this.strNextUrl = strNextUrl;
		this.strMin_tag_id = strMin_tag_id;
		this.strNext_min_id = strNext_min_id;
		this.strNext_max_id = strNext_max_id;
		this.strNext_max_tag_id = strNext_max_tag_id;
	}

	public InstaPages(JSONObject jsonObject) {
		super();
		try {
			this.strNextUrl = jsonObject.getString(NEXT_URL);
			this.strMin_tag_id = jsonObject.getString(MIN_TAG_ID);
			this.strNext_min_id = jsonObject.getString(NEXT_MIN_ID);
			this.strNext_max_id = jsonObject.getString(NEXT_MAX_ID);
			this.strNext_max_tag_id = jsonObject.getString(NEXT_MAX_TAG_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addPageItems(PageItems pageItem) {
		pageItems.add(pageItem);
	}

	public static InstaPages parseInstaPages(JSONObject jsonObject) {
		InstaPages instaPages = null;

		try {
			JSONObject jsonObjPagination = jsonObject
					.getJSONObject("pagination");
			if (jsonObjPagination != null) {
				instaPages = new InstaPages(jsonObjPagination);

			}

			JSONArray jsonData = jsonObject.getJSONArray("data");
			PageItems instaData = null;
			if (jsonData != null) {
				for (int i = 0; i < jsonData.length(); i++) {
					JSONObject jsonObjData = (JSONObject) jsonData.get(i);
					JSONObject jsonObjImages = (JSONObject) jsonObjData
							.getJSONObject("images");

					instaData = new PageItems(jsonObjImages);
					instaPages.addPageItems(instaData);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instaPages;
	}
}
