package cz.clovekvtisni.coordinator.android.event;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationTool implements LocationListener {
	private final BestLocationListener listener;
	private final LocationManager locationManager;
	
	private Location currentBestLocation;

	public LocationTool(BestLocationListener listener, Context context) {
		this.listener = listener;
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public Location getCurrentBestLocation() {
		return currentBestLocation;
	}

	public void start() {
		currentBestLocation = null;
		
		Location lastGpsLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (isBetterLocation(lastGpsLocation, currentBestLocation)) {
			currentBestLocation = lastGpsLocation;
		}

		Location lastNetworkLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (isBetterLocation(lastNetworkLocation, currentBestLocation)) {
			currentBestLocation = lastNetworkLocation;
		}

		if (currentBestLocation != null) listener.onBestLocationUpdated(currentBestLocation);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, this);
	}

	public void stop() {
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (isBetterLocation(location, currentBestLocation)) {
			currentBestLocation = location;
			listener.onBestLocationUpdated(currentBestLocation);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	private boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (location == null) return false;
		if (currentBestLocation == null) return true;

		boolean isNewer = location.getTime() - currentBestLocation.getTime() > 0;

		return isNewer;
	}

	public static interface BestLocationListener {
		public void onBestLocationUpdated(Location location);
	}

}
