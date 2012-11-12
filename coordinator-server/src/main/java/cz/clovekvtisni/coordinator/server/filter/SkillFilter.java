package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.domain.config.Skill;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
public class SkillFilter extends Filter<Skill> {

    @Override
    public Class<Skill> getEntityClass() {
        return Skill.class;
    }
}
