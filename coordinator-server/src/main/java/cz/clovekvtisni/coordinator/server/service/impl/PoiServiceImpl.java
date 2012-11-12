package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 10.11.12
 */
@Service("poiService")
public class PoiServiceImpl extends AbstractServiceImpl implements PoiService {

    @Override
    public PoiEntity findById(Long id, long flags) {
        PoiEntity poi = ofy().get(Key.create(PoiEntity.class, id));

        return poi;
    }

    @Override
    public ResultList<PoiEntity> findByFilter(PoiFilter filter, int limit, String bookmark, long flags) {
        MaObjectify ofy = ofy();

        return ofy.findByFilter(filter, bookmark, limit);
    }

    @Override
    public PoiEntity createPoi(final PoiEntity entity) {
        final MaObjectify ofy = ofy();
        logger.debug("creating " + entity);

        return ofy.transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                entity.setId(null);
                updateSystemFields(entity);
                ofy.put(entity);

                return entity;
            }
        });
    }

    @Override
    public PoiEntity updatePoi(final PoiEntity entity) {
        final MaObjectify ofy = ofy();

        return ofy.transact(new Work<PoiEntity>() {
            @Override
            public PoiEntity run() {
                updateSystemFields(entity);
                ofy.put(entity);

                return entity;
            }
        });
    }

    @Override
    public void deletePoi(PoiEntity entity) {
        entity.setDeletedDate(new Date());
        updatePoi(entity);
    }
}
