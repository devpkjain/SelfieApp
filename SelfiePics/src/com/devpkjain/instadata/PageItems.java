package com.devpkjain.instadata;

import org.json.JSONException;
import org.json.JSONObject;

public class PageItems {

	public InstaImage imgLowRes = null;
	public InstaImage imgThumbnail = null;
	public InstaImage imgStandard = null;

	public InstaImage getImgLowRes() {
		return imgLowRes;
	}

	public void setImgLowRes(InstaImage imgLowRes) {
		this.imgLowRes = imgLowRes;
	}

	public InstaImage getImgThumbnail() {
		return imgThumbnail;
	}

	public void setImgThumbnail(InstaImage imgThumbnail) {
		this.imgThumbnail = imgThumbnail;
	}

	public InstaImage getImgStandard() {
		return imgStandard;
	}

	public void setImgStandard(InstaImage imgStandard) {
		this.imgStandard = imgStandard;
	}

	public PageItems(InstaImage imgLowRes, InstaImage imgThumbnail,
			InstaImage imgStandard) {
		super();
		this.imgLowRes = imgLowRes;
		this.imgThumbnail = imgThumbnail;
		this.imgStandard = imgStandard;
	}

	public PageItems(JSONObject jsonObject) {
		super();
		try {
			JSONObject jsonObjLowRes = (JSONObject) jsonObject
					.getJSONObject("low_resolution");
			JSONObject jsonObjThumb = (JSONObject) jsonObject
					.getJSONObject("thumbnail");
			JSONObject jsonObjStandard = (JSONObject) jsonObject
					.getJSONObject("standard_resolution");

			this.imgLowRes = new InstaImage(jsonObjLowRes);
			this.imgThumbnail = new InstaImage(jsonObjThumb);
			this.imgStandard = new InstaImage(jsonObjStandard);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
