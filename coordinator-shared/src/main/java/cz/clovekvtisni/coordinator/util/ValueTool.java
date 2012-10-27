package cz.clovekvtisni.coordinator.util;

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

    public static String normalizeEmail(String email) {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        return email;
    }

    public static String normalizeLogin(String login) {
        return login == null ? null : login.toLowerCase();
    }
}
