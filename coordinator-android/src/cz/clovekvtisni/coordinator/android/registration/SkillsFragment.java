package cz.clovekvtisni.coordinator.android.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import cz.clovekvtisni.coordinator.domain.User;

public class SkillsFragment extends RegisterFragment {

private static final String EXTRA_SKILLS = "skills";
	
	private String[] skills;
	
	static SkillsFragment newInstance(String[] skills) {
		Bundle b = new Bundle();
		b.putSerializable(EXTRA_SKILLS, skills);
		
		SkillsFragment f = new SkillsFragment();
		f.setArguments(b);
		return f;
	}
	
	public SkillsFragment() {
	}
	
	private void initSkillsList(LinearLayout parentLayout) {
		ListView listView = new ListView(getActivity());
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, skills));

		parentLayout.addView(listView);
	}
	
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		skills =  getArguments().getStringArray(EXTRA_SKILLS);
	}

	@Override
	public LinearLayout onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
		LinearLayout layout = super.onCreateView(inflater, container, state);
		setTitle("Dovednosti");
		initSkillsList(layout);
		return layout;
	}

	@Override
	void updateUser(User user) {
	}
}
