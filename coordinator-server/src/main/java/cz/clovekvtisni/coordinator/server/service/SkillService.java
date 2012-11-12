package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.config.Skill;
import cz.clovekvtisni.coordinator.server.filter.SkillFilter;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

public interface SkillService extends Service {

    @FilterResult("#helper.canRead(#entity)")
    Skill findById(String id);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<Skill> findByFilter(SkillFilter filter);
}
