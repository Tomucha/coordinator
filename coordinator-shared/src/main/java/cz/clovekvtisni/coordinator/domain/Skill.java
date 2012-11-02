package cz.clovekvtisni.coordinator.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root
public class Skill extends AbstractStaticEntity {

    @Attribute(name = "id")
    protected String id;

    @Attribute
    private String name;

    @Text(required = false)
    private String description;

    @Attribute(name = "must_verify", required = false)
    private boolean mustVerify;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMustVerify() {
        return mustVerify;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mustVerify=" + mustVerify +
                '}';
    }
}
