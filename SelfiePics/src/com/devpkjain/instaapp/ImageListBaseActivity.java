package com.devpkjain.instaapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.devpkjain.ListViewEx.ItemDragListener;
import com.devpkjain.ListViewEx.ItemDropListener;
import com.devpkjain.ListViewEx.ItemRemoveListener;
import com.devpkjain.ListViewEx.ListViewEx;

public class ImageListBaseActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void setupListView(ListView listView) {
		if (listView instanceof ListViewEx) {
			((ListViewEx) listView).setDropListener(mDropListener);
			((ListViewEx) listView).setRemoveListener(mRemoveListener);
			((ListViewEx) listView).setDragListener(mDragListener);
		}
	}

	protected ItemDropListener mDropListener = new ItemDropListener() {
		public void onDrop(int from, int to) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof LazyAdapter) {
				((LazyAdapter) adapter).onDrop(from, to);
				getListView().invalidateViews();
			}
		}
	};

	protected ItemRemoveListener mRemoveListener = new ItemRemoveListener() {
		public void onRemove(int which) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof LazyAdapter) {
				((LazyAdapter) adapter).onRemove(which);
				getListView().invalidateViews();
			}
		}
	};

	protected ItemDragListener mDragListener = new ItemDragListener() {

		int backgroundColor = 0xe0103010;
		int defaultBackgroundColor;

		public void onDrag(int x, int y, ListView listView) {
			// TODO Auto-generated method stub
		}

		public void onStartDrag(View itemView) {
			itemView.setVisibility(View.INVISIBLE);
			defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
			itemView.setBackgroundColor(backgroundColor);
			ImageView iv = (ImageView) itemView.findViewById(R.id.ImageViewAnchor);
			if (iv != null)
				iv.setVisibility(View.INVISIBLE);
		}

		public void onStopDrag(View itemView) {
			itemView.setVisibility(View.VISIBLE);
			itemView.setBackgroundColor(defaultBackgroundColor);
			ImageView iv = (ImageView) itemView.findViewById(R.id.ImageViewAnchor);
			if (iv != null)
				iv.setVisibility(View.VISIBLE);
		}
	};
}
