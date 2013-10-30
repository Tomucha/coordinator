package cz.clovekvtisni.coordinator.server.georss;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 23.09.13
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
@Root(name = "channel")
public class Channel {

    /*
    <title>USGS M5+ Earthquakes</title>
    <description>Real-time, worldwide earthquake list for the past 7 days</description>
    <link>http://earthquake.usgs.gov/eqcenter/</link>
    <dc:publisher>U.S. Geological Survey</dc:publisher>
    <pubDate>Thu, 27 Dec 2007 23:56:15 PST</pubDate>
    */

    public Channel(String title, String description, String link, String publisher, String pubDate, List<Item> item) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.publisher = publisher;
        this.pubDate = pubDate;
        this.item = item;
    }

    @Element(name="title")
    private String title;

    @Element(name="description")
    private String description;

    @Element(name="link", required = false)
    private String link;

    @Element(name="publisher")
    @Namespace(reference="http://purl.org/dc/elements/1.1/")
    private String publisher;

    @Element(name="pubDate")
    private String pubDate;

    @ElementList(name = "item", inline = true)
    private List<Item> item;

}
