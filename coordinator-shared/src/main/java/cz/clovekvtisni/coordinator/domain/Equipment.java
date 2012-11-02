package cz.clovekvtisni.coordinator.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root
public class Equipment extends AbstractStaticEntity {

    @Attribute
    private String id;

    @Attribute
    private String name;

    @Text(required = false)
    private String description;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
