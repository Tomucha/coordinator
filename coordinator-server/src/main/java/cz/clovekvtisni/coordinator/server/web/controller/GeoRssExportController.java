package cz.clovekvtisni.coordinator.server.web.controller;

import com.google.appengine.api.utils.SystemProperty;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.georss.Channel;
import cz.clovekvtisni.coordinator.server.georss.Item;
import cz.clovekvtisni.coordinator.server.georss.Rss;
import cz.clovekvtisni.coordinator.server.web.model.EventFilterParams;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/export/georss")
public class GeoRssExportController extends AbstractController {

    @RequestMapping
    public void export(
            @RequestParam(value = "organizationId", required = false) String organizationId,
            HttpServletResponse response,
            Model model) throws Exception {

        String appId = SystemProperty.applicationId.get();

        Long eventId = null;
        if (appContext.getActiveEvent() != null) {
            eventId = appContext.getActiveEvent().getId();
        }
        if (eventId == null) throw new IllegalStateException("Null eventId");
        if (organizationId == null) throw new IllegalStateException("Null organizationId");

        List<PoiEntity> export = poiService.findPoisForExport(organizationId, eventId);

        List<Item> items = new ArrayList<Item>();

        NumberFormat fmt = new DecimalFormat("#.000000");
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.US);

        for (PoiEntity poiEntity : export) {
            String descrition = poiEntity.getDescription();
            String pubDate = dateFormat.format(poiEntity.getCreatedDate());
            String title = config.getPoiCategoryMap().get(poiEntity.getPoiCategoryId()).getName() + ": " + poiEntity.getName();
            String link = "http://"+appId+".appspot.com/admin/event/poi/edit?eventId="+poiEntity.getEventId()+"&poiId="+poiEntity.getId();
            String lat = fmt.format(poiEntity.getLatitude());
            String longitude = fmt.format(poiEntity.getLongitude());

            String guid = link;

            Item i = new Item(pubDate, title, descrition, link, lat, longitude, guid);
            items.add(i);
        }

        String title = config.getOrganizationMap().get(organizationId).getName();
        String description = config.getOrganizationMap().get(organizationId).getDescription();
        String link = "http://"+appId+".appspot.com/";
        String publisher = config.getOrganizationMap().get(organizationId).getName();
        String pubDate = dateFormat.format(new Date());
        Channel ch = new Channel(title, description, link, publisher, pubDate, items);

        Rss r = new Rss(ch);
        response.setStatus(200);
        response.setContentType("text/xml; charset=UTF-8");
        Format format = new Format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Persister xml = new Persister(format);
        Writer w = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        xml.write(r, w);
    }
}
