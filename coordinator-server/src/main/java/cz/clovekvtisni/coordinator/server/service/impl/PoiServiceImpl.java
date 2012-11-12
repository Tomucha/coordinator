package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.EventLocationEntity;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.filter.PoiFilter;
import cz.clovekvtisni.coordinator.server.filter.result.NoDeletedFilter;
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
        PoiEntity poi = noTransactionalObjectify().get(Key.create(PoiEntity.class, id));

        return poi;
    }

    @Override
    public ResultList<PoiEntity> findByFilter(PoiFilter filter, int limit, String bookmark, long flags) {
        MaObjectify ofy = noTransactionalObjectify();

        return ofy.getResult(PoiEntity.class, filter, bookmark, limit, new NoDeletedFilter());
    }

    @Override
    public PoiEntity createPoi(final PoiEntity entity) {
        return transactionWithResult("creating " + entity, new TransactionWithResultCallback<PoiEntity>() {
            @Override
            public PoiEntity runInTransaction(Objectify ofy) {
                entity.setId(null);
                updateSystemFields(entity);
                ofy.put(entity);

                return entity;
            }
        });
    }

    @Override
    public PoiEntity updatePoi(final PoiEntity entity) {
        return transactionWithResult("creating " + entity, new TransactionWithResultCallback<PoiEntity>() {
            @Override
            public PoiEntity runInTransaction(Objectify ofy) {
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
