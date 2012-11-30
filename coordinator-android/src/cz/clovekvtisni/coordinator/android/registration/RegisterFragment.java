package cz.clovekvtisni.coordinator.android.registration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.domain.User;

public abstract class RegisterFragment extends Fragment {

	private TextView titleView;

	String getTextViewText(int textViewId) {
		TextView textView = (TextView) getView().findViewById(textViewId);
		return textView.getText().toString();
	}
	
	@Override
	public LinearLayout onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.frag_registration, container,
				false);
		titleView = (TextView) view.findViewById(R.id.title);
		return view;
	}

	void setTitle(String title) {
		titleView.setText(title);
	}

	abstract void updateUser(User user);

}
