package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:17 PM
 */
public interface CoordinatorEntity<E extends CoordinatorEntity<E>> extends Serializable {

    Long getId();

    Key<E> getKey();

}
