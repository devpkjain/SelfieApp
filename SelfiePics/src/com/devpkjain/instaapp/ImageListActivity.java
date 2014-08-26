package com.devpkjain.instaapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androbar.android.widget.ActionBar;
import com.devpkjain.instadata.InstaImage;
import com.devpkjain.instadata.InstaPages;
import com.devpkjain.instadata.PageItems;
import com.devpkjain.instautils.InstaUtils;
import com.devpkjain.instautils.Utils;

public class ImageListActivity extends ImageListBaseActivity {

	private static final String TAG = "PKJAIN";
	private String sourceUrl;

	ActionBar actionBar;
	ListView list;
	LazyAdapter adapter;
	ArrayList<PageItems> imageList;
	DefaultHttpClient httpClient = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_list);

		Bundle extras = getIntent().getExtras();

		String title = "devpkjain";
		actionBar = (ActionBar) findViewById(R.id.imageListActionbar);
		actionBar.setTitle(title);
		actionBar.addAction(new RefreshAction());
		final ActionBar.Action goHomeAction = new ActionBar.IntentAction(this,
				null, R.drawable.ic_title_home);

		list = getListView();

		list.setOnItemClickListener(itemClickListener);

		ListView listView = getListView();
		setupListView(listView);
		httpClient = (DefaultHttpClient) InstaUtils.getDefaultHttpClient();

		httpClient.getParams().setParameter("http.useragent", "Instagram");
		initInstaAccess();

	}

	private void fetchInstaPages() {
		new FetchActivity().execute();
	}

	private void initInstaPages() {
		imageList = new ArrayList<PageItems>();
		adapter = new LazyAdapter(this, imageList, mPageHandler);
		InstaPages.PAGE_ID_REQ = null;
		setListAdapter(adapter);

		fetchInstaPages();
	}

	private void refresh() {
		InstaPages.PAGE_ID_REQ = null;
		imageList.clear();
		adapter.notifyDataSetChanged();
		fetchInstaPages();
	}

	public Handler mPageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == InstaPages.WHAT_FETCH_PAGES) {
				if (InstaPages.PAGE_ID_REQ == null
						|| !InstaPages.PAGE_ID_REQ
								.equalsIgnoreCase(InstaPages.PAGE_ID_MAX)) {
					InstaPages.PAGE_ID_REQ = InstaPages.PAGE_ID_MAX;
					fetchInstaPages();
				}
			}
		}
	};

	public Handler getPageHandler() {
		return mPageHandler;
	};

	public AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View view1, int i,
				long l) {
			Log.i(Utils.TAG, "Clicked: " + String.valueOf(i));

			final PageItems pageItems = (PageItems) adapter.getItem(i);

			ImageView iv = (ImageView) adapterView.findViewWithTag(pageItems
					.getImgStandard().getUrl());
			Drawable drawable = iv.getDrawable();

			showImage(pageItems.getImgStandard().getUrl(), drawable);

		}
	};

	public void showImage(String url, Drawable drawable) {
		Dialog builder = new Dialog(this);
		builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
		builder.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.BLUE));
		builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {

			}
		});

		ImageView imageView = new ImageView(this);

		imageView.setBackgroundResource(R.drawable.fullscreen);
		imageView.setBackgroundDrawable(drawable);

		builder.setContentView(imageView, new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		builder.setCancelable(true);
		builder.setCanceledOnTouchOutside(true);
		
		Toast.makeText(this,
				"Search median 'Selfie', Tap To Inlarge, Drag & Drop, Scroll to Load More. developed by: devpjain@gmail.com",
				Toast.LENGTH_LONG).show();

		builder.show();
	}

	public void showShareDialog(InstaImage image) {
		final InstaImage finalImage = image;

		// get the permalink
		String url = Utils.createPermalinkUrl(finalImage.getUrl());
		String jsonResponse = Utils.doRestfulGet(httpClient, url,
				getApplicationContext());
		if (jsonResponse != null) {
			try {
				JSONTokener jsonTokener = new JSONTokener(jsonResponse);
				JSONObject jsonObject = new JSONObject(jsonTokener);
				String permalink = jsonObject.getString("permalink");

			} catch (JSONException j) {
				Log.e(TAG, "JSON parse error: " + j.toString());
				Toast.makeText(getApplicationContext(),
						"There was an error communicating with Instagram",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Failed to get permalink for the image", Toast.LENGTH_SHORT)
					.show();
		}

	}

	public void showDeleteDialog(InstaImage image) {
		final InstaImage finalImage = image;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete this image?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String url = "";
								String jsonResponse = Utils.doRestfulGet(
										httpClient, url,
										getApplicationContext());
								if (jsonResponse != null) {
									imageList.remove(finalImage);
									adapter.notifyDataSetChanged();
								} else {
									Toast.makeText(getApplicationContext(),
											"Delete failed", Toast.LENGTH_SHORT)
											.show();
								}
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showCommentDialog(InstaImage image, String username) {
		final InstaImage finalImage = image;
		final String finalUsername = username;

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Comment");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String comment = input.getText().toString();

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

	@Override
	public void onDestroy() {
		adapter.imageLoader.stopThread();
		list.setAdapter(null);
		super.onDestroy();
	}

	public void clearCache(View view) {
		adapter.imageLoader.clearCache();
		adapter.notifyDataSetChanged();
	}

	private class FetchActivity extends AsyncTask<Void, String, Boolean> {
		protected void onPreExecute() {
			actionBar.setProgressBarVisibility(View.VISIBLE);
		}

		protected void onPostExecute(Boolean result) {
			actionBar.setProgressBarVisibility(View.GONE);
			if (result) {
				adapter.notifyDataSetChanged();
				// list.invalidate();
				list.setVisibility(View.VISIBLE);
				InstaPages.PAGE_ID_REQ = null;
			}
		}

		protected void onProgressUpdate(String toastText) {
			Toast.makeText(ImageListActivity.this, toastText,
					Toast.LENGTH_SHORT).show();
		}

		protected Boolean doInBackground(Void... voids) {
			Log.i(Utils.TAG, "Image fetch");

			if (Utils.isOnline(getApplicationContext()) == false) {
				publishProgress("No connection to Internet.\nTry again later");
				Log.i(Utils.TAG, "No internet, didn't load Activity Feed");
				return false;
			}

			//
			try {
				String hashTag = "selfie";
				
				sourceUrl = InstaUtils.MEDIA_URL.replace("tag_name", hashTag)
						+ "?access_token="
						+ InstaUtils.getAccessToken(ImageListActivity.this);
				sourceUrl += "&count=" + InstaPages.PAGE_SIZE;

				if (InstaPages.PAGE_ID_REQ != null) {
					sourceUrl += "&max_tag_id=" + InstaPages.PAGE_ID_REQ;

					Log.i(Utils.TAG, "PAGE_ID_REQ: " + InstaPages.PAGE_ID_REQ);
				}
				HttpGet httpGet = new HttpGet(sourceUrl);
				HttpResponse httpResponse = httpClient.execute(httpGet);

				// test result code
				if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					publishProgress("Login failed.");
					Log.e(TAG, "Login status code bad.");
					return false;
				}

				// test json response
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(httpEntity.getContent(),
									"UTF-8"));
					String json = reader.readLine();
					JSONTokener jsonTokener = new JSONTokener(json);
					JSONObject jsonObject = new JSONObject(jsonTokener);

					InstaPages pages = InstaPages.parseInstaPages(jsonObject);
					imageList.addAll(pages.getPageItems());
					InstaPages.PAGE_ID_MAX = "" + pages.getMaxTadId();
					// Log.i(TAG, "JSON: " + jsonObject.toString());
					return true;

				} else {
					publishProgress("Improper data returned from Instagram");
					Log.e(TAG, "instagram returned bad data");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

	}

	private class RefreshAction implements ActionBar.Action {

		public int getDrawable() {
			return R.drawable.ic_title_refresh;
		}

		public void performAction(View view) {
			refresh();
		}
	}

	public void initInstaAccess() {
		WebView webview = (WebView) findViewById(R.id.item_webview);
		checkAccessToken(this, webview);
	}

	public void postReceiveAccessToken(String strAccessToken) {

		initInstaPages();
		// new MyWebservicesAsyncTask().execute(strAccessToken);
	}

	public void checkAccessToken(Activity activity, WebView webview) {
		String accessToken = null;
		accessToken = InstaUtils.getAccessToken(activity);

		if (accessToken == null) {

			final InstaUtils instaUtils = new InstaUtils(activity) {

				@Override
				public void OnReceiveAccessToken(String accessToken) {

					postReceiveAccessToken(accessToken);
				}
			};
			instaUtils.requestAccessToken(webview);
		} else {
			postReceiveAccessToken(accessToken);
		}
	}

	// private class MyWebservicesAsyncTask extends
	// AsyncTask<String, Void, Boolean> {
	// @Override
	// protected Boolean doInBackground(String... params) {
	// String accessToken = params[0];
	//
	// boolean bRes = true;
	//
	// if (bRes) {
	// int nCount = InstaUtils.getTagDetails(InstaUtils.TAGS_URL,
	// "selfie", accessToken);
	// return true;
	// }
	//
	// return false;
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result) {
	// // called in UI thread
	// if (result) {
	// initInstaPages();
	// }
	// super.onPostExecute(result);
	// }
	// }

}