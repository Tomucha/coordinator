package cz.clovekvtisni.coordinator.server.georss;

import org.simpleframework.xml.*;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 23.09.13
 * Time: 12:52
 * To change this template use File | Settings | File Templates.
 */
@Root(strict = true)
@NamespaceList({
        @Namespace(reference="http://www.w3.org/2003/01/geo/wgs84_pos#", prefix="geo"),
        @Namespace(reference="http://purl.org/dc/elements/1.1/", prefix="dc")
})
public class Rss {

    public Rss(Channel channel) {
        this.channel = channel;
    }

    @Attribute
    private String version="2.0";

    @Element(name="channel")
    private Channel channel = null;

    /*
     <?xml version="1.0"?>
 <?xml-stylesheet href="/eqcenter/catalogs/rssxsl.php?feed=eqs7day-M5.xml" type="text/xsl"
                  media="screen"?>
 <rss version="2.0"
      xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
      xmlns:dc="http://purl.org/dc/elements/1.1/">
  <channel>
     <title>USGS M5+ Earthquakes</title>
     <description>Real-time, worldwide earthquake list for the past 7 days</description>
     <link>http://earthquake.usgs.gov/eqcenter/</link>
     <dc:publisher>U.S. Geological Survey</dc:publisher>
     <pubDate>Thu, 27 Dec 2007 23:56:15 PST</pubDate>
     <item>
       <pubDate>Fri, 28 Dec 2007 05:24:17 GMT</pubDate>
       <title>M 5.3, northern Sumatra, Indonesia</title>
       <description>December 28, 2007 05:24:17 GMT</description>
       <link>http://earthquake.usgs.gov/eqcenter/recenteqsww/Quakes/us2007llai.php</link>
       <geo:lat>5.5319</geo:lat>
       <geo:long>95.8972</geo:long>
     </item>
   </channel>
 </rss>

     */



}
