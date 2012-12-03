package cz.clovekvtisni.coordinator.android.register.wizard.model;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import cz.clovekvtisni.coordinator.android.register.wizard.ui.AddressFragment;
import cz.clovekvtisni.coordinator.domain.User;

public class AddressPage extends Page {
	public static final String STREET_DATA_KEY = "street";
	public static final String CITY_DATA_KEY = "city";
	public static final String ZIP_CODE_DATA_KEY = "zip";

	private static final String[] STRING_DATA_KEYS = { STREET_DATA_KEY, CITY_DATA_KEY, ZIP_CODE_DATA_KEY };

	public AddressPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return AddressFragment.newInstance(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem("Ulice", mData.getString(STREET_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem("Město", mData.getString(CITY_DATA_KEY), getKey(), -1));
		dest.add(new ReviewItem("PSČ", mData.getString(ZIP_CODE_DATA_KEY), getKey(), -1));
	}

	@Override
	public boolean isCompleted() {
		for (String dataKey : STRING_DATA_KEYS) {
			if (TextUtils.isEmpty(mData.getString(dataKey))) return false;
		}
		return true;
	}
	
	@Override
    public void saveToUser(User user) {
    	user.setAddressLine(mData.getString(STREET_DATA_KEY));
    	user.setCity(mData.getString(CITY_DATA_KEY));
    	user.setZip(mData.getString(ZIP_CODE_DATA_KEY));
    }
}
