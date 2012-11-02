package cz.clovekvtisni.coordinator.domain;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 2.11.12
 */
public abstract class IdentifiableEntity extends Entity {

    protected Long id;

    final public Long getId() {
        return id;
    }

    final public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentifiableEntity that = (IdentifiableEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
