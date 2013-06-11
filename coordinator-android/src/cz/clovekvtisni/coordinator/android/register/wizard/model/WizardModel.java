package cz.clovekvtisni.coordinator.android.register.wizard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.Bundle;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.domain.config.Skill;

/**
 * Represents a wizard model, including the pages/steps in the wizard, their
 * dependencies, and their currently populated choices/values/selections.
 */
public class WizardModel implements ModelCallbacks {
	protected Context mContext;

	private List<ModelCallbacks> mListeners = new ArrayList<ModelCallbacks>();
	private PageList mRootPageList;

	public WizardModel(Context context, Organization organization, OrganizationInEvent organizationInEvent, ConfigResponse config, User user) {
		mContext = context;

		Page personalInfo = new PersonalInfoPage(this, "Osobní údaje").setRequired(true);

		Page address = new AddressPage(this, "Bydliště").setRequired(true);

		List<Equipment> equipmentList = Lists.newArrayList();
		Set<String> equipmentIds = Sets.newHashSet(organizationInEvent == null ? organization.getPreRegistrationEquipment() : organizationInEvent.getRegistrationEquipment());
		for (Equipment equipment : config.getEquipmentList()) {
			if (equipmentIds.contains(equipment.getId())) {
				equipmentList.add(equipment);
			}
		}
		Page equipment = new EquipmentPage(this, "Vybavení").setEquipments(equipmentList);

		List<Skill> skillList = Lists.newArrayList();
		Set<String> skillIds = Sets.newHashSet(organizationInEvent == null ? organization.getPreRegistrationSkills() : organizationInEvent.getRegistrationSkills());
		for (Skill skill : config.getSkillList()) {
			if (skillIds.contains(skill.getId())) {
				skillList.add(skill);
			}
		}
		Page skills = new SkillsPage(this, "Dovednosti").setSkills(skillList);

		mRootPageList = new PageList(personalInfo, address, equipment, skills);

        if (user != null) {
            loadFromUser(user);
        }
	}

	public void saveToUser(User user) {
		for (Page page : mRootPageList)
			page.saveToUser(user);
	}

    public void loadFromUser(User user) {
        for (Page page : mRootPageList)
            page.loadFromUser(user);
    }


	@Override
	public void onPageDataChanged(Page page) {
		// can't use for each because of concurrent modification (review
		// fragment
		// can get added or removed and will register itself as a listener)
		for (int i = 0; i < mListeners.size(); i++) {
			mListeners.get(i).onPageDataChanged(page);
		}
	}

	@Override
	public void onPageTreeChanged() {
		// can't use for each because of concurrent modification (review
		// fragment
		// can get added or removed and will register itself as a listener)
		for (int i = 0; i < mListeners.size(); i++) {
			mListeners.get(i).onPageTreeChanged();
		}
	}

	public Page findByKey(String key) {
		return mRootPageList.findByKey(key);
	}

	public void load(Bundle savedValues) {
		for (String key : savedValues.keySet()) {
			mRootPageList.findByKey(key).setData(savedValues.getBundle(key));
		}
	}

	public void registerListener(ModelCallbacks listener) {
		mListeners.add(listener);
	}

	public Bundle save() {
		Bundle bundle = new Bundle();
		for (Page page : getCurrentPageSequence()) {
			bundle.putBundle(page.getKey(), page.getData());
		}
		return bundle;
	}

	/**
	 * Gets the current list of wizard steps, flattening nested (dependent)
	 * pages based on the user's choices.
	 */
	public List<Page> getCurrentPageSequence() {
		ArrayList<Page> flattened = new ArrayList<Page>();
		mRootPageList.flattenCurrentPageSequence(flattened);
		return flattened;
	}

	public void unregisterListener(ModelCallbacks listener) {
		mListeners.remove(listener);
	}
}
