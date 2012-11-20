package cz.clovekvtisni.coordinator.android.util;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import android.widget.EditText;

/**
 * Par beznych operaci nad hodnotami - je prazdna, je to email? 
 * 
 * Created by IntelliJ IDEA.
 * TODELETEUser: tomas
 * Date: Nov 19, 2010
 * Time: 2:42:38 PM
 */
public class ValueTool {

    public static final Pattern EMAIL_PATTERN = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static Date parseISO8601DateTime(String dateString) throws ParseException {
        if (dateString == null) return null;
        return ISO8601.parseDate(dateString);
    }

    public static Map<String, String> renderStringMap(String... keyValuePairs) {
        Map<String, String> map = new HashMap<String, String>();
        int l = keyValuePairs.length;
        if (l % 2 != 0) throw new IllegalArgumentException("key without value");
        for (int i = 0; i < l; i += 2) {
            map.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return map;
    }

    public static boolean isEmail(EditText editText) {
        return isEmail(editText.getText().toString());
    }

    public static boolean isEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
