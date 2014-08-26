package com.devpkjain.ListViewEx;

import android.view.View;
import android.widget.ListView;

public interface ItemDragListener {

	void onStartDrag(View itemView);

	void onDrag(int x, int y, ListView listView);

	void onStopDrag(View itemView);
}
