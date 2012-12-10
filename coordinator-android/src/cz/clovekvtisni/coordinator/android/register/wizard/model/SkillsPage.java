package cz.clovekvtisni.coordinator.android.register.wizard.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import android.support.v4.app.Fragment;
import cz.clovekvtisni.coordinator.android.register.wizard.ui.SkillsFragment;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserSkill;
import cz.clovekvtisni.coordinator.domain.config.Skill;

public class SkillsPage extends Page {

	private List<Skill> skillsList;

	public SkillsPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return SkillsFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		StringBuilder sb = new StringBuilder();

		ArrayList<Integer> selections = mData.getIntegerArrayList(Page.SIMPLE_DATA_KEY);
		if (selections != null && selections.size() > 0) {
			for (Integer selection : selections) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(skillsList.get(selection).getName());
			}
		}

		dest.add(new ReviewItem(getTitle(), sb.toString(), getKey()));
	}

	public List<Skill> getSkillsList() {
		return skillsList;
	}

	public SkillsPage setSkills(List<Skill> skillsList) {
		this.skillsList = skillsList;
		return this;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

	@Override
	public void saveToUser(User user) {
		ArrayList<Integer> selections = mData.getIntegerArrayList(Page.SIMPLE_DATA_KEY);
		if (selections == null) selections = Lists.newArrayList();
		UserSkill[] selectedSkills = new UserSkill[selections.size()];
		for (int i = 0; i < selections.size(); i++) {
			UserSkill e = new UserSkill();
			e.setSkillId(skillsList.get(selections.get(i)).getId());
			selectedSkills[i] = e;
		}

		user.setSkillList(selectedSkills);
	}

}
