package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public class PoiFilter extends NoDeletedFilter<PoiEntity> {

    @Override
    public Class<PoiEntity> getEntityClass() {
        return PoiEntity.class;
    }
}
