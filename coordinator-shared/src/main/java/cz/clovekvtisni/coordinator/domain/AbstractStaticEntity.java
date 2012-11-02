package cz.clovekvtisni.coordinator.domain;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public abstract class AbstractStaticEntity {

    public abstract String getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractStaticEntity that = (AbstractStaticEntity) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
