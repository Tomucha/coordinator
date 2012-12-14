package cz.clovekvtisni.coordinator.android.event;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import cz.clovekvtisni.coordinator.android.event.map.OsmMapView;

public class CustomViewPager extends ViewPager {
	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		if (v instanceof OsmMapView) return true;
		else return super.canScroll(v, checkV, dx, x, y);
	}
}