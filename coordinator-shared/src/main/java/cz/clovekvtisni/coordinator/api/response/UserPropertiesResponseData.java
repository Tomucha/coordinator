package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.domain.config.Skill;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
public class UserPropertiesResponseData implements ApiResponseData {

    private List<Equipment> equipmentList;

    private List<Skill> skillList;

    public UserPropertiesResponseData(List<Equipment> equipmentList, List<Skill> skillList) {
        this.equipmentList = equipmentList;
        this.skillList = skillList;
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }
}
