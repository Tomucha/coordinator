package cz.clovekvtisni.coordinator.server.util;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 4:42 PM
 */
public class ValueTool {

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isEmpty(Collection<?> value) {
        return value == null || value.isEmpty();
    }


}
