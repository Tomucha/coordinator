package cz.clovekvtisni.coordinator.domain.config;

import cz.clovekvtisni.coordinator.domain.config.AbstractStaticEntity;
import org.simpleframework.xml.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root(name = "poi_category")
public class PoiCategory extends AbstractStaticEntity {

    @Attribute
    private String id;

    @Attribute
    private String name;

    @Element(required = false)
    private String description;

    @Attribute
    private String icon;

    @Attribute(name = "workflow_id", required = false)
    private String workflowId;

    @Attribute(required = false)
    private boolean important;

    @ElementList(type = SubCategory.class, name = "sub_category", inline = true, required = false)
    private List<SubCategory> subCategories;

    @Attribute(required = false)
    private boolean publicExport = false;

    public List<SubCategory> getSubCategories() {
        if (subCategories == null) return Collections.EMPTY_LIST;
        return subCategories;
    }

    private Map<String, SubCategory> subCategoriesMap = null;

    public Map<String, SubCategory> getSubCategoriesMap() {
        if (subCategoriesMap == null) {
            subCategoriesMap = new HashMap<String, SubCategory>();
            if (subCategories != null) {
                for (SubCategory subCategory : subCategories) {
                    subCategoriesMap.put(subCategory.getId(), subCategory);
                }
            }
        }
        return subCategoriesMap;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public boolean isImportant() {
        return important;
    }

    public boolean isPublicExport() {
        return publicExport;
    }

    public void setPublicExport(boolean publicExport) {
        this.publicExport = publicExport;
    }

    @Override
    public String toString() {
        return "PoiCategory{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", important=" + important +
                ", subCategories=" + getSubCategories().size() +
                '}';
    }

}
