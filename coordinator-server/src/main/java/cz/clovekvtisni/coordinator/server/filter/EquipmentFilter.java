package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
public class EquipmentFilter extends Filter<Equipment> {
    @Override
    public Class<Equipment> getEntityClass() {
        return Equipment.class;
    }
}
