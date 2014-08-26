package com.devpkjain.instautils;

import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class InstaUtils {
	public static final String MEDIA_URL = "https://api.instagram.com/v1/tags/tag_name/media/recent";
	public static final String TAGS_URL = "https://api.instagram.com/v1/tags/";
	public static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
	public static final String AUTHORIZE_PATH = "https://api.instagram.com/oauth/authorize/";
	public static final String CLIENT_ID = "af55869181524b7ba0d0b706c1f655f8";
	public static final String REDIRECT_URI = "https://devpkjain.wordpress.com/";
	public static final String AUTH_DATA = "&display=touch&scope=likes+comments+relationships";
	public static final String SHPREF_KEY_ACCESS_TOKEN = "Access_Token";
	Activity mActivity = null;
	private static String accessToken = null;
	
	
	
	
	public InstaUtils(Activity mActivity) {
		super();
		this.mActivity = mActivity;
	}

	public String getLoginRequestUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(AUTHORIZE_PATH);
		sb.append("?response_type=token");
		sb.append("&client_id=" + CLIENT_ID);
		sb.append("&redirect_uri=" + REDIRECT_URI);
		sb.append(AUTH_DATA);
		return sb.toString();
	}

	public abstract void OnReceiveAccessToken(String strAccessToken);

	private String extractToken(String url) {
		String[] sArray = url.split("access_token=");
		return (sArray[1].split("&token_type=Bearer"))[0];
	}

	public void requestAccessToken(final WebView webview) {
		webview.setVisibility(View.VISIBLE);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				if (url.startsWith(REDIRECT_URI)) {

					if (url.indexOf("access_token=") != -1) {
						String accessToken = extractToken(url);
						setAccessToken(accessToken);
						webview.setVisibility(View.GONE);
						OnReceiveAccessToken(accessToken);
					}
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		webview.loadUrl(getLoginRequestUrl());
	}

	public static void getUserDetails(String tokenURLString, String access_token) {
		try {
			URL url = new URL(tokenURLString);
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url
					.openConnection();
			httpsURLConnection.setRequestMethod("POST");
			httpsURLConnection.setDoInput(true);
			httpsURLConnection.setDoOutput(true);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					httpsURLConnection.getOutputStream());
			outputStreamWriter.write("client_id=" + CLIENT_ID
					+ "&grant_type=authorization_code" + "&redirect_uri="
					+ REDIRECT_URI + "&access_token=" + access_token);

			outputStreamWriter.flush();
			String response = httpsURLConnection.getInputStream().toString();
			JSONObject jsonObject = (JSONObject) new JSONTokener(response)
					.nextValue();
			String accessTokenString = jsonObject.getString("access_token");
			String id = jsonObject.getJSONObject("user").getString("id");
			String username = jsonObject.getJSONObject("user").getString(
					"username");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getTagDetails(String urlString, String tag,
			String accessToken) {
		int nCount = 0;
		try {

			URL url = new URL(urlString + tag + "?access_token=" + accessToken);
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url
					.openConnection();
			httpsURLConnection.setRequestMethod("GET");

			String response = HttpUtils
					.mConvertStreamToString(httpsURLConnection.getInputStream());
			JSONObject jsonObject = (JSONObject) new JSONTokener(response)
					.nextValue();

			jsonObject = jsonObject.getJSONObject("data");
			String media_count = jsonObject.getString("media_count");
			String tag_name = jsonObject.getString("name");

			nCount = Integer.parseInt(media_count);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return nCount;
	}
	public static String getAccessToken(Activity activity) {
		return activity.getPreferences(Context.MODE_PRIVATE).getString(
				SHPREF_KEY_ACCESS_TOKEN, null);
	}

	public void setAccessToken(String accessToken) {
		if (mActivity != null && accessToken != null) {
			this.accessToken = accessToken;
			Editor e = mActivity.getPreferences(Context.MODE_PRIVATE).edit();
			e.putString(SHPREF_KEY_ACCESS_TOKEN, accessToken);
			e.commit();
		}
	}
	public static HttpClient getDefaultHttpClient()
	{
	    HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);

	    SchemeRegistry schReg = new SchemeRegistry();
	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

	    return new DefaultHttpClient(conMgr, params);
	}
}
