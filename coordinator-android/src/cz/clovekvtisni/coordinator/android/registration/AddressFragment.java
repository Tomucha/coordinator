package cz.clovekvtisni.coordinator.android.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.domain.User;

public class AddressFragment extends RegisterFragment {

	public AddressFragment() {
	}
	
	@Override
	public LinearLayout onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
		LinearLayout layout = super.onCreateView(inflater, container, state);
		inflater.inflate(R.layout.frag_registration_address, layout);
		setTitle("Bydliště");
		return layout;
	}

	@Override
	void updateUser(User user) {
		user.setAddressLine(getTextViewText(R.id.street));
		user.setCity(getTextViewText(R.id.town));
		user.setZip(getTextViewText(R.id.zip_code));
	}
}
