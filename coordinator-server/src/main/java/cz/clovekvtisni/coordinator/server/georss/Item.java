package cz.clovekvtisni.coordinator.server.georss;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 23.09.13
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
@Root(name = "item")
public class Item {

    /*
    <item>
    <pubDate>Fri, 28 Dec 2007 05:24:17 GMT</pubDate>
    <title>M 5.3, northern Sumatra, Indonesia</title>
    <description>December 28, 2007 05:24:17 GMT</description>
    <link>http://earthquake.usgs.gov/eqcenter/recenteqsww/Quakes/us2007llai.php</link>
    <geo:lat>5.5319</geo:lat>
    <geo:long>95.8972</geo:long>
    </item>
    */

    public Item(String pubDate, String title, String description, String link, String lat, String longitude, String guid) {
        this.pubDate = pubDate;
        this.title = title;
        this.description = description;
        this.link = link;
        this.lat = lat;
        this.longitude = longitude;
        this.guid = guid;
    }

    @Element(name = "pubDate")
    private String pubDate = null;

    @Element(name = "title")
    private String title = null;

    @Element(name = "description")
    private String description = null;

    @Element(name = "link")
    private String link = null;

    @Element(name = "guid")
    private String guid = null;

    @Element(name = "lat")
    @Namespace(reference="http://www.w3.org/2003/01/geo/wgs84_pos#")
    private String lat = null;

    @Element(name = "long")
    @Namespace(reference="http://www.w3.org/2003/01/geo/wgs84_pos#")
    private String longitude = null;



}
