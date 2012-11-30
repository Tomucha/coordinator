package cz.clovekvtisni.coordinator.android.registration;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import cz.clovekvtisni.coordinator.domain.User;

public class EquipmentFragment extends RegisterFragment {

	private static final String EXTRA_EQUIPMENT = "equipment";

	private ListView listView;
	private String[] equipment;

	static EquipmentFragment newInstance(String[] equipment) {
		Bundle b = new Bundle();
		b.putStringArray(EXTRA_EQUIPMENT, equipment);

		EquipmentFragment f = new EquipmentFragment();
		f.setArguments(b);
		return f;
	}

	public EquipmentFragment() {
	}

	private void initEquipmentList(LinearLayout parentLayout) {
		listView = new ListView(getActivity());
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, equipment));

		parentLayout.addView(listView);
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		equipment = getArguments().getStringArray(EXTRA_EQUIPMENT);
	}

	@Override
	public LinearLayout onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
		LinearLayout layout = super.onCreateView(inflater, container, state);
		setTitle("Vybavení");
		initEquipmentList(layout);
		return layout;
	}

	@Override
	void updateUser(User user) {
		// SparseBooleanArray checkedPositions = listView.getCheckedItemPositions();
	}
}
