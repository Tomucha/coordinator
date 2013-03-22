package cz.clovekvtisni.coordinator.android.util;

import java.util.ArrayList;
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
	
	public BetterArrayAdapter(Context context, int layoutResource) {
		this(context, layoutResource, new ArrayList<T>());
	}

	protected abstract void setUpView(T item, View view);

    /**
     * No such method in 2.2
     * @param items
     */
    public void addAll(T... items) {
        for (T item : items) {
            add(item);
        }
    }

    /**
     * No such method in 2.2
     * @param items
     */
    public void addAll(java.util.Collection<? extends T> collection) {
        for (T item : collection) {
            add(item);
        }
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(layoutResource, parent, false);
		}
		setUpView(getItem(position), convertView);
		return convertView;
	}
}
