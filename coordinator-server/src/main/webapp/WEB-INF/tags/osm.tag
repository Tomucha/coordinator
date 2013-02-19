<%@
        attribute name="width" required="true" %><%@
        attribute name="height" required="true" %><%@
        attribute name="zoom" required="false" type="java.lang.Integer" %><%@
        attribute name="longitude" required="false" %><%@
        attribute name="latitude" required="false" %><%@
        attribute name="onMapChange" required="false" %><%@
        attribute name="onLoad" required="false" %><%@
        attribute name="onNewPoint" required="false" %><%@
        attribute name="buttons" required="false" %><%@
        attribute name="maxPoints" required="false" %><%@
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

<script>
    var map, mapLayer, markerLayer, fromProjection, toProjection;

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
    var currentPointType;
    var points = {};

    var CoordinatorMap = {

        clickHandlers: {},

        limits: {},

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

        disablePopup: function(pointType) {
            CoordinatorMap.clickHandlers[pointType] = function(point) {
                return null;
            };
        },

        addPoint: function(point) {
            var lonLat = CoordinatorMap.position(point.longitude, point.latitude);
            var marker = new OpenLayers.Marker(lonLat, point.icon.clone());
            point.id = marker.id = "point" + (idCounter++);

            if (point.popupUrl) {
                marker.events.register("click", marker, function (event) {
                    CoordinatorMap.closePopup();
                    $("#mapPopupContainer").load(point.popupUrl, function () {
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

            <c:if test="${!empty onNewPoint}">
            ${onNewPoint}(point);
            </c:if>

            return point;
        },

        clearMarkers: function() {
            var length = markerLayer.markers.length;
            var marker = null;
            for (var i = 0 ; i < length ; i++) {
                markerLayer.removeMarker(markerLayer.markers[0]);
            }
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

        trimLocations: function() {
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
        },

        getPoints: function() {
            return points;
        },

        closePopup: function() {
            if (popup != null) {
                map.removePopup(popup);
            }
        },

        startSetLocation: function(type) {
            if (CoordinatorMap.getState() != STATE_SET_LOCATION) {
                CoordinatorMap.setState(STATE_SET_LOCATION);
                currentPointType = type;
            }
        }
    };

//    CoordinatorMap.clickHandlers[TYPE_LOCATION] = function(point) {
//        return "#locationEditForm";
//    }
//    CoordinatorMap.clickHandlers[TYPE_USER] = function(point) {
//        return "#userForm";
//    }
//    CoordinatorMap.clickHandlers[TYPE_POI] = function(point) {
//        return "#poiForm";
//    }

    <c:if test="${!empty maxPoints}">
    var tok = "<c:out value="${maxPoints}"/>".split(",");
    for (i in tok) {
        var vals = tok[i].split("=");
        if (vals.length == 2) {
            CoordinatorMap.limits[vals[0].replace(/\s*/, "")] = parseInt(vals[1].replace(/\s*/, ""));
        }
    }
    </c:if>

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
                        'click': this.trigger
                    }, this.handlerOptions
            );
        },

        trigger: function(event) {
            if (CoordinatorMap.getState() == STATE_SET_LOCATION) {
                var lonLat = CoordinatorMap.fromProjection(map.getLonLatFromPixel(event.xy));
                var point = {
                    type: currentPointType,
                    latitude: lonLat.lat,
                    longitude: lonLat.lon,
                    icon: ICON_GENERIC
                };
                CoordinatorMap.addPoint(point);
                CoordinatorMap.trimLocations();
            }
        }

    });

    $(document).ready(function() {
        map             = new OpenLayers.Map("mapContainer");
        mapLayer        = new OpenLayers.Layer.OSM();
        markerLayer     = new OpenLayers.Layer.Markers("markers");
        fromProjection  = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
        toProjection    = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection

        map.addLayer(mapLayer);
        map.addLayer(markerLayer);

        <c:if test="${not empty onMapChange}">
        map.events.register('zoomend', null, function() { ${onMapChange} });
        map.events.register('moveend', null, function() { ${onMapChange} });
        </c:if>


/*
        <c:if test="${!empty buttons}">

            var panel = new OpenLayers.Control.Panel({
                type: OpenLayers.Control.TYPE_BUTTON,
                    createControlMarkup: function(control) {
                    var button = document.createElement('button');
                    if (control.title) {
                        button.innerHTML = control.title;
                    }
                    return button;
                }
            });

            var controls = [];
            var buttons = "<c:out value="${buttons}"/>".split(",");
            for (var index in buttons) {
                var buttonType = buttons[index];
                switch (buttonType.replace(/\s*//*
, "")) {
                    case "addLocation":
                        controls[controls.length] = new OpenLayers.Control.Button({
                            title: "+location",
                            trigger: function() {
                                CoordinatorMap.startSetLocation(TYPE_LOCATION);
                            }
                        });
                        break;

                    case "addPoi":
                        controls[controls.length] = new OpenLayers.Control.Button({
                            title: "+poi",
                            trigger: function() {
                                CoordinatorMap.startSetLocation(TYPE_POI);
                            }
                        });
                        break;
                }
            }

            panel.addControls(controls);
            map.addControl(panel);
        </c:if>
*/

        var click = new OpenLayers.Control.Click();
        map.addControl(click);
        click.activate();

        map.setCenter(CoordinatorMap.position(${!empty longitude ? longitude : 14.4489967}, ${!empty latitude ? latitude : 50.0789306}), <c:out value="${!empty zoom ? zoom : 15}"/>);

        <c:if test="${!empty onLoad}">
            <c:out value="${onLoad}"/>;
        </c:if>
    });

    function searchAddress(address) {
        $.getJSON("${root}/admin/event/map/api/address?query="+address, function(response) {
            map.setCenter(CoordinatorMap.position(response.longitude, response.latitude, 15));
        });
    }

</script>

<p>
    <input type="text"
           class="search-query" placeholder="<s:message code="label.searchAddress"/>"
           onkeypress="if (event.keyCode == 13) searchAddress($(this).val())"/>
</p>

<div id="mapContainer"></div>
<div id="mapPopupWindow" style="display: none;">
<p style="background-color: #ccc;"><span class="icon-remove" onclick="CoordinatorMap.closePopup();"></span></p>
<div id="mapPopupContainer">

</div>
</div>

<%--
<div id="locationEditForm" style="display: none;">
    <div>
        <p><b><s:message code="label.eventLocation"/></b></p>
        <input type="hidden" name="id"/>
        <input name="radius" size="4"/> km
    </div>
    <div>
        <button type="button" onclick="CoordinatorMap.closePopup()"><s:message code="button.cancel"/></button>
        <button type="button" onclick="CoordinatorMap.closeAndSavePopup()"><s:message code="button.ok"/></button>
    </div>
</div>

<div id="userForm" style="display: none;">
    <div>
        <p><b><s:message code="label.userLastLocation"/></b></p>
        <input type="hidden" name="id"/>
        <input name="name" readonly="readonly" size="4"/>
        <form action="${root}/admin/event/user/edit">
            <div>
                <input type="hidden" name="userId"/>
                <button type="button" onclick="CoordinatorMap.closePopup()"><s:message code="button.cancel"/></button>
                <button type="submit"><s:message code="button.edit"/></button>
            </div>
        </form>
    </div>
</div>


<div id="poiForm" style="display: none;">
    <div>
        <p><b><s:message code="label.poi"/></b></p>
        <input type="hidden" name="id"/>
        <input name="description" readonly="readonly"/>
        <form action="${root}/admin/event/poi/edit">
            <div>
                <input type="hidden" name="poiId"/>
                <input type="hidden" name="eventId"/>
                <button type="button" onclick="CoordinatorMap.closePopup()"><s:message code="button.cancel"/></button>
                <button type="submit"><s:message code="button.edit"/></button>
            </div>
        </form>
    </div>
</div>--%>
