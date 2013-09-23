package cz.clovekvtisni.coordinator.domain.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root(name = "sub_category")
public class SubCategory extends AbstractStaticEntity {

    @Attribute
    private String id;

    @Text(required = true)
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SubCategory{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
