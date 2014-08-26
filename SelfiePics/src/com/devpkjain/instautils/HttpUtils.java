package com.devpkjain.instautils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtils {
	private DefaultHttpClient httpclient;

	public HttpUtils() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();

		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
				params, schemeRegistry);

		httpclient = new DefaultHttpClient(cm, params);
	}

	String doGetRequest(String link) throws ClientProtocolException,
			IOException {
		HttpGet httpget = new HttpGet(link);

		HttpResponse response = httpclient.execute(httpget);
		httpget.addHeader("Content-Type", "application/json");
//		httpget.addHeader("User-Agent", "R2G Client");
		httpget.addHeader("Content-Type", "text/xml;charset=\"utf-8\"");
		httpget.addHeader("Accept", "*/*");
//		httpget.addHeader("Connection", "Keep-Alive");
//		httpget.setHeader("Authorization", "Bearer "+accessToken); // for OAuth2.0, String accessToken
		int status = response.getStatusLine().getStatusCode();

		// If receive anything but a 200 status, return a null input stream
		if (status == HttpStatus.SC_OK) {
			HttpEntity httpEntity = response.getEntity();
			String strResponse = EntityUtils.toString(httpEntity);
			return strResponse;
		} else {
			return null;
		}
	}

	String doPostRequest(String link, String content)
			throws ClientProtocolException, IOException {
		HttpPost httppost = new HttpPost(link);
		httppost.addHeader("Content-Type", "application/atom+xml");
		ByteArrayEntity entity = new ByteArrayEntity(content.getBytes());
		httppost.setEntity(entity);
		HttpResponse response = httpclient.execute(httppost);

		int status = response.getStatusLine().getStatusCode();

		// If receive anything but a 201 status, return a null input stream
		if (status == HttpStatus.SC_CREATED) {
	        String strResponse = EntityUtils.toString(response.getEntity());
			return strResponse;
		} else {
			return null;
		}
	}

	static String mConvertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	public String authenticateOAuth2(String accessToken)
			throws IOException, JSONException {
//				// create entity object
//				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//				params.add(new BasicNameValuePair("access_token", accessToken));
//				HttpEntity entity = new UrlEncodedFormEntity(params);
//				// create post object
//				HttpPost httppost = new HttpPost(OAUTH2_TOKENINFO_LINK);
//				httppost.addHeader(entity.getContentType());
//				httppost.setEntity(entity);
//				
//				HttpResponse response = httpclient.execute(httppost);
//				
//				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//					InputStream istream = (response.getEntity() != null)
//							? response.getEntity().getContent() : null;
//					if (istream != null) {
//						String jsonResult = mConvertStreamToString(istream);
//						JSONObject json = new JSONObject(jsonResult);
//						this.username = json.getString("user_name");
//						this.accessToken = accessToken;
//						return this.username;
//					}
//				}
				return null;
			}
}
