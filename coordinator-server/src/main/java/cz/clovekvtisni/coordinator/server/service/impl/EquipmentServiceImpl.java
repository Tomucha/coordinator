package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.filter.EquipmentFilter;
import cz.clovekvtisni.coordinator.server.service.EquipmentService;
import cz.clovekvtisni.coordinator.server.service.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Service("equipmentService")
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private CoordinatorConfig config;

    @Override
    public Equipment findById(String id) {
        if (id == null) return null;

        for (Equipment equipment : config.getEquipmentList()) {
            if (id.equals(equipment.getId())) {
                return equipment;
            }
        }

        return null;
    }

    @Override
    public ResultList<Equipment> findByFilter(EquipmentFilter filter) {
        List<Equipment> equipmentList = config.getEquipmentList();

        return new ResultList<Equipment>(equipmentList, null);
    }
}
