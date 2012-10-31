package cz.clovekvtisni.coordinator.server.domain;

import cz.clovekvtisni.coordinator.domain.Role;
import cz.clovekvtisni.coordinator.domain.Skill;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root(name = "coordinator")
public class CoordinatorConfig {

    @ElementList(type = Role.class, name = "role_list")
    private List<Role> roleList;

    @ElementList(type = Skill.class, name = "skill_list")
    private List<Skill> skillList;

    @Override
    public String toString() {
        return "CoordinatorConfig{" +
                "roleList=" + roleList +
                ", skillList=" + skillList +
                '}';
    }
}
