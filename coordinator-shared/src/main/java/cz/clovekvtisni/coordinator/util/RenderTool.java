package cz.clovekvtisni.coordinator.util;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 10.11.12
 */
public class RenderTool {
    public static String renderHumanAgo(Date date) {
        long diff = System.currentTimeMillis() - date.getTime();
        diff = diff / 60000; // mins
        if (diff < 10) return "just now";
        if (diff < 30) return ((diff/5)*5)+" min ago";
        if (diff < 120) return ((diff/10)*10)+" min ago";
        if (diff < (24 * 60)) return (diff/60)+" hrs ago";
        return (diff / (24 * 60)) + " days ago";
    }

}
