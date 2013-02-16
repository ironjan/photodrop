package com.github.ironjan.photodrop.helper;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.SystemService;

/* 
 from http://developer.android.com/guide/topics/location/strategies.html 
 with minor changes

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
@EBean
public class LocationBean implements LocationListener {
	@SystemService
	LocationManager mLocationManager;
	private LocBeanCallback mCallback = mDummyCallback;

	private static final LocBeanCallback mDummyCallback = new LocBeanCallback() {

		@Override
		public void updateLocation(Location loc) {
			// dummy
		}
	};

	public void startListening(LocBeanCallback callback) {
		this.mCallback = callback;
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, this);
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}

	public void stopListening() {
		this.mCallback = mDummyCallback;
		mLocationManager.removeUpdates(this);
	}

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	public static Location getBetterLocation(Location location1,
			Location location2) {
		if (location1 == null) {
			return location2;
		}
		if (location2 == null) {
			return location1;
		}
		boolean loc2IsNewer = isLocation2Newer(location1, location2);

		if (haveSignificantTimeDifference(location1, location2)) {
			return getNewer(location1, location2);
		}

		int accuracyDelta = (int) (location2.getAccuracy() - location1
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		boolean isFromSameProvider = isSameProvider(location1, location2);

		if (isMoreAccurate) {
			return location2;
		} else if (loc2IsNewer && !isLessAccurate) {
			return location2;
		} else if (loc2IsNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return location2;
		}
		return location1;
	}

	private static boolean isLocation2Newer(Location location1,
			Location location2) {
		boolean loc2IsNewer;
		long timeDelta = location2.getTime() - location1.getTime();
		loc2IsNewer = timeDelta > 0;
		return loc2IsNewer;
	}

	private static Location getNewer(Location location1, Location location2) {
		long timeDelta = location2.getTime() - location1.getTime();
		if (timeDelta > 0) {
			return location2;
		}

		return location1;
	}

	private static boolean haveSignificantTimeDifference(Location location1,
			Location location2) {
		long timeDelta = location2.getTime() - location1.getTime();
		boolean loc2IsSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean loc2IsSignificantlyOlder = timeDelta < -TWO_MINUTES;

		return loc2IsSignificantlyNewer || loc2IsSignificantlyOlder;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(Location location1, Location location2) {
		if (location1 == null) {
			return location2 == null;
		}
		return location1.getProvider().equals(location2.getProvider());
	}

	private Location mCurrentBestLocation;

	@Override
	public void onLocationChanged(Location location) {
		final Location oldBest = mCurrentBestLocation;
		mCurrentBestLocation = LocationBean.getBetterLocation(
				mCurrentBestLocation, location);
		if (mCurrentBestLocation != oldBest) {
			updateBestLocation(location);
		}
	}

	private void updateBestLocation(Location location) {
		this.mCurrentBestLocation = location;
		mCallback.updateLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {/**/
	}

	@Override
	public void onProviderEnabled(String provider) {/**/
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {/**/
	}
}
