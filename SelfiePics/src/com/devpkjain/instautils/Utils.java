

package com.devpkjain.instautils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    // strings
    public static final String TAG = "devpkjain";
    public static final String PREFS_NAME = "devpkjain_prefs";

    // intent identifiers
    public static final int CAMERA_PIC_REQUEST = 1;
    public static final int UPLOAD_FROM_GALLERY = 2;
    public static final int UPLOAD_FROM_CAMERA = 3;
    public static final int SELECT_FROM_GALLERY = 4;

    // image constants
    public static final String OUTPUT_DIR = "devpkjain";
    public static final String OUTPUT_FILE = "devpkjain.jpg";
    public static final String OUTPUT_FILE_PROCESSED = "devpkjain_processed.jpg";
    public static final int IMAGE_WIDTH = 612;
    public static final int IMAGE_HEIGHT = 612;
    public static final int IMAGE_BORDER = 24;
    public static final int IMAGE_CORNER_RADIUS = 35;
    public static final int IMAGE_JPEG_COMPRESSION_QUALITY = 75;

    // url constants
	public static final String LOGIN_URL = "https://instagr.am/api/v1/accounts/login/";
    public static final String LOGOUT_URL = "http://instagr.am/api/v1/accounts/logout/";
    public static final String UPLOAD_URL = "http://instagr.am/api/v1/media/upload/";
    public static final String CONFIGURE_URL = "https://instagr.am/api/v1/media/configure/";
    public static final String TIMELINE_URL = "http://instagr.am/api/v1/feed/timeline/";
    public static final String POPULAR_URL = "http://instagr.am/api/v1/feed/popular/";

    public static final String USERTIMELINE_PREFIX = "http://instagr.am/api/v1/feed/user/";
    public static final String MEDIA_PREFIX = "http://instagr.am/api/v1/media/";
    public static final String LIKE_POSTFIX = "/like/";
    public static final String UNLIKE_POSTFIX = "/unlike/";
    public static final String COMMENT_POSTFIX = "/comment/";
    public static final String DELETE_POSTFIX = "/delete/";
    public static final String PERMALINK_POSTFIX = "/permalink/";


    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if( cm.getActiveNetworkInfo() == null ) return false;
        return cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static String createUserTimelineUrl(String pk) {
        return USERTIMELINE_PREFIX + pk + "/";
    }

    public static String getUserPk(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        Boolean loginValid = sharedPreferences.getBoolean("loginValid",false);

        if( loginValid ) {
            return sharedPreferences.getString("pk",null);
        } else {
            return null;
        }
    }

    public static String createDeleteUrl(String id) {
        return MEDIA_PREFIX + id + DELETE_POSTFIX;
    }


    public static String createLikeUrl(String id) {
        return MEDIA_PREFIX + id + LIKE_POSTFIX;
    }

    public static String createUnlikeUrl(String id) {
        return MEDIA_PREFIX + id + UNLIKE_POSTFIX;
    }

    public static String createCommentUrl(String id) {
        return MEDIA_PREFIX + id + COMMENT_POSTFIX;
    }

    public static String createPermalinkUrl(String id) {
        return MEDIA_PREFIX + id + PERMALINK_POSTFIX;
    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    public static void launchCredentials(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putBoolean("loginValid", false);
        editor.commit();
  }

    public static String getUsername(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        Boolean loginValid = sharedPreferences.getBoolean("loginValid",false);

        if( loginValid ) {
            return sharedPreferences.getString("username",null);
        } else {
            return null;
        }
    }

    public static String getPk(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        Boolean loginValid = sharedPreferences.getBoolean("loginValid",false);

        if( loginValid ) {
            return sharedPreferences.getString("pk",null);
        } else {
            return null;
        }
    }

    public static boolean doLogin(Context ctx, DefaultHttpClient httpClient) {
        Log.i(TAG, "Doing login");

        // gather login info
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        Boolean loginValid = sharedPreferences.getBoolean("loginValid",false);

        if( !loginValid ) {
            launchCredentials(ctx);
            return false;
        }

        String username = sharedPreferences.getString("username","");
        String password = sharedPreferences.getString("password","");

        // create POST
        HttpPost httpPost = new HttpPost(LOGIN_URL);
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("username", username));
        postParams.add(new BasicNameValuePair("password", password));
        postParams.add(new BasicNameValuePair("device_id", "0000"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            // test result code
            if( httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK ) {
                Log.i(TAG, "Login HTTP status fail");
                return false;
            }

            // test json response
            HttpEntity httpEntity = httpResponse.getEntity();
            if( httpEntity != null ) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
                String json = reader.readLine();
                JSONTokener jsonTokener = new JSONTokener(json);
                JSONObject jsonObject = new JSONObject(jsonTokener);
                Log.i(TAG,"JSON: " + jsonObject.toString());

                String loginStatus = jsonObject.getString("status");

                if( !loginStatus.equals("ok") ) {
                    Log.e(TAG, "JSON status not ok: " + jsonObject.getString("status"));
                    return false;
                }
            }

        } catch( IOException e ) {
            Log.e(TAG, "HttpPost error: " + e.toString());
            return false;
        } catch( JSONException e ) {
            Log.e(TAG, "JSON parse error: " + e.toString());
            return false;
        }

        return true;
    }

    public static String doRestulPut(DefaultHttpClient httpClient, String url,
                                     List<NameValuePair> postParams, Context ctx) {
        // create POST
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            // test result code
            if( httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK ) {
                Log.i(TAG, "Login HTTP status fail");
                return null;
            }

            // test json response
            HttpEntity httpEntity = httpResponse.getEntity();
            if( httpEntity != null ) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
                String json = reader.readLine();
                JSONTokener jsonTokener = new JSONTokener(json);
                JSONObject jsonObject = new JSONObject(jsonTokener);
                Log.i(TAG,"JSON: " + jsonObject.toString());

                String loginStatus = jsonObject.getString("status");

                if( !loginStatus.equals("ok") ) {
                    Log.e(TAG, "JSON status not ok: " + jsonObject.getString("status"));
                    return null;
                } else {
                    return json;
                }
            } else {
                return null;
            }
        } catch( IOException e ) {
            Log.e(TAG, "HttpPost error: " + e.toString());
            return null;
        } catch( JSONException e ) {
            Log.e(TAG, "JSON parse error: " + e.toString());
            return null;
        }
    }

    public static String doRestfulGet(DefaultHttpClient httpClient, String url, Context ctx) {
        Log.i(Utils.TAG, "Image fetch");

        if( Utils.isOnline(ctx) == false ) {
            Toast.makeText(ctx,"No connection to Internet.\nTry again later",Toast.LENGTH_SHORT).show();
            Log.i(Utils.TAG, "No internet!");
            return null;
        }


        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            // test result code
            if( httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK ) {
                Toast.makeText(ctx, "Action failed.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Return status code bad.");
                return null;
            }

            // test json response
            HttpEntity httpEntity = httpResponse.getEntity();
            if( httpEntity != null ) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
                String json = reader.readLine();
                JSONTokener jsonTokener = new JSONTokener(json);
                JSONObject jsonObject = new JSONObject(jsonTokener);
                Log.i(TAG,"JSON: " + jsonObject.toString());

                String loginStatus = jsonObject.getString("status");

                if( !loginStatus.equals("ok") ) {
                    Toast.makeText(ctx,"Network activity did not return ok",Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "JSON status not ok: " + jsonObject.getString("status"));
                    return null;
                } else {
                    return json;
                }
            } else {
                Toast.makeText(ctx,"Improper data returned from Instagram",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "instagram returned bad data");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}