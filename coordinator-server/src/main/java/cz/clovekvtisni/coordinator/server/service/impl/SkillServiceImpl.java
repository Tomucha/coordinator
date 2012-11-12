package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.domain.config.Skill;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.filter.SkillFilter;
import cz.clovekvtisni.coordinator.server.service.SkillService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Service("skillService")
public class SkillServiceImpl implements SkillService {

    @Autowired
    private CoordinatorConfig config;

    @Override
    public Skill findById(String id) {
        if (id == null) return null;

        for (Skill skill : config.getSkillList()) {
            if (id.equals(skill.getId())) {
                return skill;
            }
        }

        return null;
    }

    @Override
    public ResultList<Skill> findByFilter(SkillFilter filter) {
        List<Skill> skillList = config.getSkillList();

        return new ResultList<Skill>(skillList, null);
    }
}
