package cz.clovekvtisni.coordinator.android.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FindView {
	public static ImageView imageView(View view, int id) {
		return (ImageView) view.findViewById(id);
	}

	public static ListView listView(View view, int id) {
		return (ListView) view.findViewById(id);
	}
	
	public static TextView textView(View view, int id) {
		return (TextView) view.findViewById(id);
	}
}
