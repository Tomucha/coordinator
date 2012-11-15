package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.server.domain.UserEquipmentEntity;
import cz.clovekvtisni.coordinator.server.filter.EquipmentFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

public interface EquipmentService extends Service {

    @FilterResult("#helper.canRead(#entity)")
    Equipment findById(String id);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<Equipment> findByFilter(EquipmentFilter filter);
}
