package com.devpkjain.instaapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devpkjain.ListViewEx.ItemDropListener;
import com.devpkjain.ListViewEx.ItemRemoveListener;
import com.devpkjain.instadata.InstaPages;
import com.devpkjain.instadata.PageItems;
import com.devpkjain.instautils.ImageLoader;

public class LazyAdapter extends BaseAdapter  implements ItemRemoveListener, ItemDropListener{

	private Activity activity;
	private ArrayList<PageItems> imageList;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private Handler handler= null;
	public LazyAdapter(Activity a, ArrayList<PageItems> i,Handler handler) {
		activity = a;
		imageList = i;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
		this.handler = handler;
	}
	public void setHandelr(Handler handler){
		this.handler = handler;
	};
	public void reqPage(String nItemID){
		if(this.handler!=null)
			handler.sendMessage(handler.obtainMessage(InstaPages.WHAT_FETCH_PAGES));
	}
	public int getCount() {
		return imageList.size();
	}

	public Object getItem(int position) {
		return imageList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		public TextView username;
		public TextView comments;
		public TextView caption;
		public ImageView image;
		public ImageView imageFullView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.image_list_item, null);
			holder = new ViewHolder();
			holder.image = (ImageView) vi.findViewById(R.id.imageSmallView);
			holder.imageFullView = (ImageView) vi.findViewById(R.id.imageFullView);
		
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		PageItems image = imageList.get(position);
		int nSize = 200;
		int nReminder = position % 3;
		if(nReminder==0)
			nSize = 300;
		holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(nSize*2,nSize);
		holder.image.setLayoutParams(params);
		
		holder.image.setTag(image.getImgLowRes().getUrl());
		
		imageLoader.DisplayImage(image.getImgLowRes().getUrl(), activity,
				holder.image);
		
		holder.imageFullView.setTag(image.getImgStandard().getUrl());
		imageLoader.DisplayImage(image.getImgStandard().getUrl(), activity,
				holder.imageFullView);
		
		if(imageList.size()>=InstaPages.PAGE_SIZE && position == imageList.size()-1){
			Toast.makeText(activity, "Load More Content", Toast.LENGTH_SHORT).show();
			reqPage(InstaPages.PAGE_ID_MAX);
		}
		return vi;
	}
	public void onRemove(int which) {
		if (which < 0 || which > imageList.size()) return;		
		imageList.remove(which);
	}

	public void onDrop(int from, int to) {
		PageItems temp = imageList.get(from);
		imageList.remove(from);
		imageList.add(to,temp);
	}
}