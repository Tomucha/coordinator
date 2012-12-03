package cz.clovekvtisni.coordinator.android.register.wizard.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.config.Organization;

/**
 * Represents a wizard model, including the pages/steps in the wizard, their
 * dependencies, and their currently populated choices/values/selections.
 */
public class WizardModel implements ModelCallbacks {
	protected Context mContext;

	private List<ModelCallbacks> mListeners = new ArrayList<ModelCallbacks>();
	private PageList mRootPageList;

	public WizardModel(Context context, Organization organization) {
		mContext = context;

		Page personalInfo = new PersonalInfoPage(this, "Osobní údaje").setRequired(true);
		Page address = new AddressPage(this, "Bydliště").setRequired(true);
		Page equipment = new MultipleFixedChoicePage(this, "Vybavení").setChoices(organization
				.getPreRegistrationEquipment());
		Page skills = new MultipleFixedChoicePage(this, "Dovednosti").setChoices(organization
				.getPreRegistrationSkills());
		mRootPageList = new PageList(personalInfo, address, equipment, skills);
	}

	public void saveToUser(User user) {
		for(Page page: mRootPageList) page.saveToUser(user);
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
