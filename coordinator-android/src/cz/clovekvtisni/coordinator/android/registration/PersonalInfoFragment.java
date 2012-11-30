package cz.clovekvtisni.coordinator.android.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.domain.User;

public class PersonalInfoFragment extends RegisterFragment {
	
	public PersonalInfoFragment() {
	}
	
	@Override
	public LinearLayout onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
		LinearLayout layout = super.onCreateView(inflater, container, state);
		inflater.inflate(R.layout.frag_registration_personal_info, layout);
		setTitle("Osobní údaje");
		return layout;
	}
	
	@Override
	void updateUser(User user) {
		user.setEmail(getTextViewText(R.id.email));
		user.setPhone(getTextViewText(R.id.phone));
		
		String[] name = getTextViewText(R.id.name).split(" ", 2);
		user.setFirstName(name[0]);
		user.setLastName(name[1]);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		System.out.println("DETACH");
	}
	
}
