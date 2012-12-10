package cz.clovekvtisni.coordinator.android.register.wizard.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import android.support.v4.app.Fragment;
import cz.clovekvtisni.coordinator.android.register.wizard.ui.EquipmentFragment;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserEquipment;
import cz.clovekvtisni.coordinator.domain.config.Equipment;

public class EquipmentPage extends Page {

	private List<Equipment> equipmentsList;

	public EquipmentPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return EquipmentFragment.create(getKey());
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
				sb.append(equipmentsList.get(selection).getName());
			}
		}

		dest.add(new ReviewItem(getTitle(), sb.toString(), getKey()));
	}

	public List<Equipment> getEquipmentsList() {
		return equipmentsList;
	}

	public EquipmentPage setEquipments(List<Equipment> equipmentsList) {
		this.equipmentsList = equipmentsList;
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
		UserEquipment[] selectedEquipment = new UserEquipment[selections.size()];
		for (int i = 0; i < selections.size(); i++) {
			UserEquipment e = new UserEquipment();
			e.setEquipmentId(equipmentsList.get(selections.get(i)).getId());
			selectedEquipment[i] = e;
		}

		user.setEquipmentList(selectedEquipment);
	}

}
