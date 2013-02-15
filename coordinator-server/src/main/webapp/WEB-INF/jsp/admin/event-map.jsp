<%@
    taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
    taglib prefix="s" uri="http://www.springframework.org/tags" %><%@
    taglib prefix="sf" uri="http://www.springframework.org/tags/form" %><%@
    taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %><%@
    taglib prefix="tags" tagdir="/WEB-INF/tags"
%>
<script type="text/javascript">

    var eventId = ${event.id};
    function init() {
        CoordinatorMap.clickHandlers[TYPE_LOCATION] = function(event) {
            return "#locationPopup";
        };
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

        fillPoiMarkers(bounds);
        fillUserMarkers(bounds);

    }
</script>

<div>
    <p class="lead"><b><s:message code="label.event"/>:</b> <c:out value="${event.name}"/></p>

    <p><c:out value="${event.description}"/></p>

    <div>
    <tags:osm
            width="90%"
            height="450px"
            latitude="${event.firstEventLocation.latitude}"
            longitude="${event.firstEventLocation.longitude}"
            onLoad="init()"
            onMapChange="refreshMarkers()"
            />
    </div>
</div>

<div id="locationPopup" style="display: none;">
    <p><b><s:message code="label.eventLocation"/></b></p>
    <input type="hidden" name="id"/>
    <div>
        Radius:<br/>
        <input name="radius" size="2" readonly="readonly"/> km</div>
    <div>
        <button type="button" onclick="CoordinatorMap.closePopup()"><s:message code="button.cancel"/></button>
    </div>
</div>
