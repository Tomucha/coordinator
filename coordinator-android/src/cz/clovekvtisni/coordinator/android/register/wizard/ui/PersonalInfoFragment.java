package cz.clovekvtisni.coordinator.android.register.wizard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cz.clovekvtisni.coordinator.android.R;
import cz.clovekvtisni.coordinator.android.register.wizard.model.PersonalInfoPage;

public class PersonalInfoFragment extends Fragment {
	private static final String ARG_KEY = "key";

	private PageFragmentCallbacks callbacks;
	private PersonalInfoPage page;

	public PersonalInfoFragment() {
	}

	public static PersonalInfoFragment newInstance(String key) {
		Bundle args = new Bundle();
		args.putString(ARG_KEY, key);

		PersonalInfoFragment f = new PersonalInfoFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof PageFragmentCallbacks)) {
			throw new ClassCastException("Activity must implement PageFragmentCallbacks");
		}

		callbacks = (PageFragmentCallbacks) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_register_personal_info, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle args = getArguments();
		page = (PersonalInfoPage) callbacks.onGetPage(args.getString(ARG_KEY));

		View view = getView();

		((TextView) view.findViewById(R.id.title)).setText(page.getTitle());

		initTextView(((TextView) view.findViewById(R.id.name)), PersonalInfoPage.NAME_DATA_KEY);
		initTextView(((TextView) view.findViewById(R.id.email)), PersonalInfoPage.EMAIL_DATA_KEY);
		initTextView(((TextView) view.findViewById(R.id.phone)), PersonalInfoPage.PHONE_DATA_KEY);
	}

	private void initTextView(final TextView textView, final String dataKey) {
		textView.setText(page.getData().getString(dataKey));
		textView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				page.getData().putString(dataKey, (editable != null) ? editable.toString() : null);
				page.notifyDataChanged();
			}
		});
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = null;
	}

}
