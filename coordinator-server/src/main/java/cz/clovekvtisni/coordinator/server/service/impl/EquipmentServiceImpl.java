package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEquipmentEntity;
import cz.clovekvtisni.coordinator.server.filter.EquipmentFilter;
import cz.clovekvtisni.coordinator.server.service.EquipmentService;
import cz.clovekvtisni.coordinator.server.service.SystemService;
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Service("equipmentService")
public class EquipmentServiceImpl extends AbstractEntityServiceImpl implements EquipmentService {

    @Autowired
    private CoordinatorConfig config;

    @Autowired
    private SystemService systemService;

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

    @Override
    public UserEquipmentEntity addUserEquipment(final UserEquipmentEntity equipmentEntity) {
        final MaObjectify ofy = ofy();

        logger.debug("adding " + equipmentEntity);
        return ofy.transact(new Work<UserEquipmentEntity>() {
            @Override
            public UserEquipmentEntity run() {
                equipmentEntity.setId(null);
                ofy.put(equipmentEntity);
                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.USER_EQUIPMENT, "u" + equipmentEntity.getUniqueKey(), equipmentEntity.getKey());

                return equipmentEntity;
            }
        });
    }
}
