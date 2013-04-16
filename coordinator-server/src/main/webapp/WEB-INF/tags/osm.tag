<%@
        attribute name="width" required="true" %><%@
        attribute name="height" required="true" %><%@
        attribute name="zoom" required="false" type="java.lang.Integer" %><%@
        attribute name="longitude" required="false" %><%@
        attribute name="latitude" required="false" %><%@
        attribute name="hideMarkers" required="false" type="java.lang.Boolean" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"
%>
<style type="text/css">
    #mapContainer {
        width: ${!empty width ? width : "100%"};
        height: ${!empty height ? height : "100%"};
    }
    .olControlPanel {
        right: 8px;
        top: 8px;
    }
</style>
<!--<script type="text/javascript" src="${root}/js/osm/OpenLayers.js"></script>-->
<script type="text/javascript" src="http://openlayers.org/api/OpenLayers.js"></script>
<script type="text/javascript" src="${root}/js/openlayers-plugin.js"></script>


<script type="text/javascript">

    var eventId = ${empty event.id ? "null" : event.id};

    var hideMarkers = ${(not empty hideMarkers && hideMarkers) ? "true" : "false"};

    function refreshMarkers() {
        if (eventId == null) return;
        if (hideMarkers) return;

        CoordinatorMap.clearMarkers();
        var bounds = map.getExtent();
        var proj = new OpenLayers.Projection("EPSG:4326");
        bounds.transform(map.getProjectionObject(), proj);

        if ($("#showpois").prop("checked")) {
            fillPoiMarkers(bounds);
            //$("#poisFilter").slideDown();
        } else {
            //$("#poisFilter").slideUp();
        }

        if ($("#showusers").prop("checked")) {
            fillUserMarkers(bounds);
            //$("#usersFilter").slideDown();
        } else {
            //$("#usersFilter").slideUp();
        }
    }

    function fillPoiMarkers(bounds) {
        if (eventId == null) return;
        if (hideMarkers) return;
        var url = root+"/admin/event/map/api/poi";

        var arrBounds = bounds.toArray(); //array order: left, bottom, right, top
        var request = {
            eventId: eventId,
            latS : arrBounds[1], latN : arrBounds[3],
            lonW: arrBounds[0], lonE : arrBounds[2],
            // let's add also the filter
            workflowId: $("#workflowId").val(),
            workflowStateId: $("#workflowStateId").val()
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
        if (eventId == null) return;
        if (hideMarkers) return;
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

/*
    //osmCallback.onLoad = function() {
        //CoordinatorMap.setOnClickAddPoint(TYPE_POI, "${root}/admin/event/map/popup/poi?eventId=${event.id}");
        //refreshMarkers();
    }

    osmCallback.onMapChange = function() {
        refreshMarkers();
    }
*/

</script>

<script>
    var map, mapLayer, markerLayer, singleMarkerLayer, fromProjection, toProjection;

    var idCounter = 0;

    var STATE_BROWSE = 0;
    var STATE_SET_LOCATION = 1;

    var state = STATE_BROWSE;

    size = new OpenLayers.Size(25, 29);

    // TODO deprecated
    var icon = new OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));

    var TYPE_POI = "poi";
    var TYPE_LOCATION = "loc";
    var TYPE_USER = "usr";

    var ICON_GENERIC = new OpenLayers.Icon('${root}/images/icons/shootingrange.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));
    var ICON_LOCATION = new OpenLayers.Icon('${root}/images/icons/shootingrange.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));
    var ICON_USER = new OpenLayers.Icon('${root}/images/icons/male-2.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));
    var ICON_POI = {};
    <%--@elvariable id="config" type="cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig"--%>
    <c:forEach items="${config.poiCategoryList}" var="category">
    ICON_POI["${category.id}"] = new OpenLayers.Icon('${root}${category.icon}', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));
    </c:forEach>


    var popup = null;
    var editedLocationMarker = null;
    var selectedPointId = null;
    var currentPopupUrl;
    var points = {};

    var DONT_SAVE = true;

    var CoordinatorMap = {

        clickHandlers: {},

        limits: {},

        saveUserPreset: function() {
            var zoom = map.getZoom();
            var lonLat = map.getCenter();
            $.cookie(eventId+"mapPreset", zoom+"/"+lonLat.lon+"/"+lonLat.lat, { expires: 30, path: '/' });
        },

        goTo: function(lon, lat, zoom, dontSave) {
            map.setCenter(CoordinatorMap.position(lon, lat), zoom);
            if (dontSave != DONT_SAVE) {
                CoordinatorMap.saveUserPreset();
            }
        },

        position: function(lon, lat) {
            return new OpenLayers.LonLat(lon, lat).transform( fromProjection, toProjection);
        },

        fromProjection: function(lonLat) {
            return new OpenLayers.LonLat(lonLat.lon, lonLat.lat).transform(toProjection, fromProjection);
        },

        setState: function(newState) {
            state = newState;
        },

        getState: function() {
            return state;
        },

        addPoint: function(point) {
            var lonLat = CoordinatorMap.position(point.longitude, point.latitude);
            var marker = new OpenLayers.Marker(lonLat, point.icon.clone());
            point.id = marker.id = "point" + (idCounter++);

            if (point.popupUrl) {
                marker.events.register("click", marker, function (event) {
                    CoordinatorMap.closePopup();
                    var url = point.popupUrl;
                    url += url.substring("?") == -1 ? "?" : "&";
                    url += "latitude=" + point.latitude + "&longitude=" + point.longitude;
                    $("#mapPopupContainer").load(url, function () {
                        popup = new OpenLayers.Popup(
                                point.name,
                                marker.lonlat,
                                new OpenLayers.Size(250, 200),
                                $("#mapPopupWindow").html()
                        );
                        map.addPopup(popup);
                        popup.show();
                    });
                });
            }

            points[point.id] = point;

            markerLayer.addMarker(marker);

            return point;
        },

        addSinglePoint: function(point) {
            singleMarkerLayer.clearMarkers();
            var lonLat = CoordinatorMap.position(point.longitude, point.latitude);
            var marker = new OpenLayers.Marker(lonLat, ICON_GENERIC.clone());
            point.id = marker.id = "pointSingle" + (idCounter++);
            point.icon = ICON_GENERIC;

            if (point.popupUrl) {
                marker.events.register("click", marker, function (event) {
                    CoordinatorMap.closePopup();
                    var url = point.popupUrl;
                    url += url.substring("?") == -1 ? "?" : "&";
                    url += "latitude=" + point.latitude + "&longitude=" + point.longitude;
                    $("#mapPopupContainer").load(url, function () {
                        popup = new OpenLayers.Popup(
                                point.name,
                                marker.lonlat,
                                new OpenLayers.Size(250, 200),
                                $("#mapPopupWindow").html()
                        );
                        map.addPopup(popup);
                        popup.show();
                    });
                });
            }

            singleMarkerLayer.addMarker(marker);

            osmCallback.onNewPoint(point);

            return point;
        },

        clearMarkers: function() {
            markerLayer.clearMarkers();
        },

        getPointById: function(id) {
            return points[id];
        },

        getPoints: function() {
            return points;
        },

        removeLocation: function(id) {
            var length = markerLayer.markers.length;
            result = new Array();
            var marker = null;
            for (var i = 0 ; i < length ; i++) {
                if (markerLayer.markers[i].id == id) {
                    marker = markerLayer.markers[i];
                    break;
                }
            }
            if (marker != null) {
                markerLayer.removeMarker(marker);
            }

            delete points[id];
        },

/*        trimLocations: function() {
            var counting = {};
            for (var i in points) {
                var type = points[i].type;
                if (!counting[type])
                    counting[type] = [points[i].id];
                else
                    counting[type][counting[type].length] = points[i].id;
            }
            for (var key in CoordinatorMap.limits) {
                var limit = CoordinatorMap.limits[key];
                if (limit && counting[key] && limit > 0 && limit < counting[key].length) {
                    var deleteCount = counting[key].length - limit;
                    while (counting[key].length > 0 && deleteCount-- > 0) {
                        var id = counting[key].shift();
                        var length = markerLayer.markers.length;
                        for (var i = 0 ; i < length ; i++) {
                            if (markerLayer.markers[i].id == id) {
                                marker = markerLayer.removeMarker(markerLayer.markers[i]);
                                delete points[id];
                                break;
                            }
                        }
                    }
                }
            }
        },*/

        getPoints: function() {
            return points;
        },

        closePopup: function() {
            if (popup != null) {
                map.removePopup(popup);
                popup = null;
                return true;
            } else {
                return false;
            }
        },

        setOnClickAddPoint: function(popupUrl) {
            CoordinatorMap.setState(STATE_SET_LOCATION);
            currentPopupUrl = popupUrl;
        },

        onMapChanged: function() {
            CoordinatorMap.saveUserPreset();
            if (eventId != null) {
                refreshMarkers();
            }
            osmCallback.onMapChange();
        },

        goToUserPreset: function() {
            var value = $.cookie(eventId+"mapPreset");
            if (value == undefined) return;
            var values = value.split("/");
            map.setCenter(new OpenLayers.LonLat(parseFloat(values[1]), parseFloat(values[2])), parseInt(values[0]));
        }

    };

    // click control
    OpenLayers.Control.Click = OpenLayers.Class(OpenLayers.Control, {
        defaultHandlerOptions: {
            'single': true,
            'double': false,
            'pixelTolerance': 0,
            'stopSingle': false,
            'stopDouble': false
        },

        initialize: function(options) {
            this.handlerOptions = OpenLayers.Util.extend({}, this.defaultHandlerOptions);
            OpenLayers.Control.prototype.initialize.apply(this, arguments);
            this.handler = new OpenLayers.Handler.Click(
                    this, {
                        'click': this.onMapClick
                    }, this.handlerOptions
            );
        },

        onMapClick: function(event) {
            if (CoordinatorMap.closePopup()) return;
            if (CoordinatorMap.getState() == STATE_SET_LOCATION) {
                var lonLat = CoordinatorMap.fromProjection(map.getLonLatFromPixel(event.xy));
                var point = {
                    latitude: lonLat.lat,
                    longitude: lonLat.lon
                };
                if (currentPopupUrl) {
                    point.popupUrl = currentPopupUrl;
                }
                CoordinatorMap.addSinglePoint(point);
            }
        }

    });

    $(document).ready(function() {
        map             = new OpenLayers.Map("mapContainer");
        mapLayer        = new OpenLayers.Layer.OSM();

        // layer for loaded markers
        markerLayer     = new OpenLayers.Layer.Markers("markers");

        // layer for one and only editable marker
        singleMarkerLayer = new OpenLayers.Layer.Markers("singleMarker");

        fromProjection  = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
        toProjection    = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection

        map.addLayer(mapLayer);
        map.addLayer(markerLayer);
        map.addLayer(singleMarkerLayer);

        var click = new OpenLayers.Control.Click();
        map.addControl(click);
        click.activate();

        CoordinatorMap.goTo(${!empty longitude ? longitude : 14.4489967}, ${!empty latitude ? latitude : 50.0789306}, ${!empty zoom ? zoom : 15}, DONT_SAVE);

        CoordinatorMap.goToUserPreset();

        osmCallback.onLoad();

        refreshMarkers();

        map.events.register('zoomend', null, CoordinatorMap.onMapChanged );
        map.events.register('moveend', null, CoordinatorMap.onMapChanged );

    });

    function searchAddress(address) {
        $.getJSON("${root}/admin/event/map/api/address?query="+address, function(response) {
            CoordinatorMap.goTo(response.longitude, response.latitude, 15);
        });
    }

</script>

<p>
    <input type="text"
           class="search-query" placeholder="<s:message code="label.searchAddress"/>"
           onkeypress="if (event.keyCode == 13) searchAddress($(this).val())"/>
</p>

<div id="mapContainer" class="well"></div>
<div id="mapPopupWindow" style="display: none;">
    <!--<p style="background-color: #ccc;"><span class="icon-remove" onclick="CoordinatorMap.closePopup();"></span></p>-->
    <p><span class="icon-remove" onclick="CoordinatorMap.closePopup();"></span></p>
    <div id="mapPopupContainer"></div>
</div>

<div class="row-fluid">
    <div class="span2">
        <label class="checkbox"><input type="checkbox" id="showusers" onchange="refreshMarkers()" checked="checked"/> <s:message code="label.showUsers"/></label>
    </div>
    <div class="span2">
        <label class="checkbox"><input type="checkbox" id="showpois" onchange="refreshMarkers()" checked="checked"/> <s:message code="label.showPois"/></label>
    </div>
</div>