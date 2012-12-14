package cz.clovekvtisni.coordinator.android.event.map;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import cz.clovekvtisni.coordinator.android.event.map.Projection.LatLon;

class TouchListener implements OnTouchListener {
	private final Projection projection;

	private PinchTracker pinchTracker;
	private ScrollTracker scrollTracker;

	public TouchListener(Projection projection) {
		this.projection = projection;
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		double lat = projection.getCenterLatLon().getLat();
		double lon = projection.getCenterLatLon().getLon();

		int action = e.getActionMasked();
		if (action == MotionEvent.ACTION_DOWN) {
			scrollTracker = new ScrollTracker(e.getX(), e.getY(), lat, lon);
		} else if (action == MotionEvent.ACTION_MOVE) {
			if (e.getPointerCount() == 1) {
				scrollTracker.update(e.getX(), e.getY());
			} else if (e.getPointerCount() == 2) {
				pinchTracker.update(e.getX(0), e.getY(0), e.getX(1), e.getY(1));
			}
		} else if (action == MotionEvent.ACTION_POINTER_DOWN) {
			if (e.getPointerCount() == 2) {
				pinchTracker = new PinchTracker(e.getX(0), e.getY(0), e.getX(1), e.getY(1));
			}
		} else if (action == MotionEvent.ACTION_POINTER_UP) {
			if (e.getPointerCount() == 2) {
				int index = 1 - e.getActionIndex();
				scrollTracker = new ScrollTracker(e.getX(index), e.getY(index), lat, lon);
			}
		}

		v.invalidate();

		return true;
	}

	private class ScrollTracker {
		private final float startX;
		private final float startY;
		private final double startLatitude;
		private final double startLongitude;

		public ScrollTracker(float startX, float startY, double startLatitude, double startLongitude) {
			this.startX = startX;
			this.startY = startY;
			this.startLatitude = startLatitude;
			this.startLongitude = startLongitude;
		}

		public void update(float x, float y) {
			double lat = startLatitude + projection.pixelsToLatitudes(y - startY);
			double lon = startLongitude + projection.pixelsToLongitudes(startX - x);
			projection.setCenterLatLon(new LatLon(lat, lon));
		}
	}

	private class PinchTracker {
		private final double startDistance;
		private final double startZoom;

		public PinchTracker(float x1, float y1, float x2, float y2) {
			startDistance = calcDistance(new PointF(x1, y1), new PointF(x2, y2));
			startZoom = projection.getZoom();
		}

		private double calcDistance(PointF a, PointF b) {
			return (double) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
		}

		public void update(float x1, float y1, float x2, float y2) {
			double newZoom = startZoom
					/ (calcDistance(new PointF(x1, y1), new PointF(x2, y2)) / startDistance);
			projection.setZoom(newZoom);
		}

	}
}