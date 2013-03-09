package cz.clovekvtisni.coordinator.android.util;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class BetterArrayAdapter<T> extends ArrayAdapter<T> {
	private final int layoutResource;

	public BetterArrayAdapter(Context context, int layoutResource, List<T> objects) {
		super(context, layoutResource, objects);
		this.layoutResource = layoutResource;
	}

	protected abstract void setUpItem(int position, View item);

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(layoutResource, parent, false);
		}
		setUpItem(position, convertView);
		return convertView;
	}
}
