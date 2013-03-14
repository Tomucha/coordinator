<%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
    taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
    taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<script type="text/javascript">

    var eventId = ${event.id};
    function initialize() {
        CoordinatorMap.setOnClickAddPoint(TYPE_POI, "${root}/admin/event/map/popup/poi?eventId=${event.id}");
        refreshMarkers();
    }

    function fillPoiMarkers(bounds) {
        var url = root+"/admin/event/map/api/poi";
        var arrBounds = bounds.toArray(); //array order: left, bottom, right, top
        var request = {
            eventId: eventId,
            latS : arrBounds[1], latN : arrBounds[3],
            lonW: arrBounds[0], lonE : arrBounds[2]
        };
        var decodedRequest = $.param(request);

        $.getJSON(url, decodedRequest, function(response, txt) {
            $.each(response, function(i, item) {
                item.popupUrl = "${root}/admin/event/map/popup/poi?poiId="+item.id+"&eventId=${event.id}",
                item.type = TYPE_POI;
                item.icon = ICON_POI[item.poiCategoryId];
                CoordinatorMap.addPoint(item);
            });
        });
    }

    function fillUserMarkers(bounds) {
        var url = root+"/admin/event/map/api/user";
        var arrBounds = bounds.toArray(); //array order: left, bottom, right, top
        var request = {
            eventId: eventId,
            latS : arrBounds[1], latN : arrBounds[3],
            lonW: arrBounds[0], lonE : arrBounds[2]
        };
        var decodedRequest = $.param(request);

        $.getJSON(url, decodedRequest, function(response, txt) {
            $.each(response, function(i, item) {
                item.name = item.userEntity.fullName;
                item.popupUrl = "${root}/admin/event/map/popup/user?userId="+item.userId+"&eventId=${event.id}",
                item.type = TYPE_USER;
                item.icon = ICON_USER;
                item.latitude = item.lastLocationLatitude;
                item.longitude = item.lastLocationLongitude;
                CoordinatorMap.addPoint(item);
            });
        });
    }


    function refreshMarkers() {
        CoordinatorMap.clearMarkers();
        var bounds = map.getExtent();
        var proj = new OpenLayers.Projection("EPSG:4326");
        bounds.transform(map.getProjectionObject(), proj);

        if ($("#showpois").prop("checked")) {
            fillPoiMarkers(bounds);
        }
        if ($("#showusers").prop("checked")) {
            fillUserMarkers(bounds);
        }

    }
</script>

<div>
    <div>
    <tags:osm
            width="90%"
            height="500px"
            latitude="${event.firstEventLocation.latitude}"
            longitude="${event.firstEventLocation.longitude}"
            onLoad="initialize()"
            onMapChange="refreshMarkers()"
            />
    <form>
        <label class="checkbox"><input type="checkbox" id="showusers" onchange="refreshMarkers()" checked="checked"/> <s:message code="label.showUsers"/></label>
        <label class="checkbox"><input type="checkbox" id="showpois" onchange="refreshMarkers()" checked="checked"/> <s:message code="label.showPois"/></label>
    </form>
    </div>
</div>
