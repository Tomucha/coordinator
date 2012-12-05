<%@
        attribute name="width" required="true" %><%@
        attribute name="height" required="true" %><%@
        attribute name="zoom" required="false" type="java.lang.Integer" %><%@
        attribute name="longitude" required="false" %><%@
        attribute name="latitude" required="false" %><%@
        attribute name="onLoad" required="false" %><%@
        attribute name="onNewMarker" required="false" %><%@
        attribute name="popupType" required="false" %><%@
        attribute name="enableLocations" required="false" type="java.lang.Boolean" %><%@
        attribute name="maxLocations" required="false" type="java.lang.Integer" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"
%><style type="text/css">
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
<script>
    var map, mapLayer, markerLayer, fromProjection, toProjection;

    var STATE_BROWSE = 0;
    var STATE_SET_LOCATION = 1;

    var state = STATE_BROWSE;
    var locationsEnabled = <c:out value="${empty enableLocations or enableLocations == false ? 'false' : 'true'}"/>;
    var maxLocations = <c:out value="${!empty maxLocations ? maxLocations : 0}"/>;
    var popupType= "<c:out value="${!empty popupType ? popupType : ''}"/>";

    size = new OpenLayers.Size(21, 25);
    var icon = new OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));

    var popup = null;
    var editedLocationMarker = null;

    var CoordinatorMap = {
        position: function(lon, lat) {
            return new OpenLayers.LonLat(lon, lat).transform( fromProjection, toProjection);
        },

        setState: function(newState) {
            state = newState;
        },

        getState: function() {
            return state;
        },

        addLocation: function(lonLat, radius) {
            var marker = new OpenLayers.Marker(lonLat, icon.clone());
            marker.id = new Date().getTime();
            marker.radius = radius;
            marker.events.register("click", marker, function(event) {
                editedLocationMarker = marker;
                CoordinatorMap.showLocationEditForm(marker.id);
            });

            markerLayer.addMarker(marker);

            <c:if test="${!empty onNewMarker}">
            ${onNewMarker}(marker);
            </c:if>
        },

        removeLocation: function(markerId) {
            var length = markerLayer.markers.length;
            result = new Array();
            var marker = null;
            for (var i = 0 ; i < length ; i++) {
                marker = markerLayer.markers[i];
            }
            if (marker != null) {
                markerLayer.removeMarker(marker);
            }
        },

        trimLocations: function() {
            if (maxLocations > 0) {
                var deleteCount = markerLayer.markers.length - maxLocations;
                if (deleteCount > 0) {
                    while (markerLayer.markers.length > 0 && deleteCount-- > 0) {
                        markerLayer.removeMarker(markerLayer.markers[0]);
                    }
                }
            }
        },

        getLocations: function() {
            var length = markerLayer.markers.length;
            result = new Array();
            for (var i = 0 ; i < length ; i++) {
                result[i] = CoordinatorMap.toLocation(markerLayer.markers[i]);
                /*
                var lonlat = new OpenLayers.LonLat(marker.lonlat.lon, marker.lonlat.lat).transform( toProjection, fromProjection);
                result[i] = {
                    latitude: lonlat.lat,
                    longitude: lonlat.lon,
                    radius: marker.radius
                };
                */
            }

            return result;
        },

        toLocation: function(marker) {
            var lonlat = new OpenLayers.LonLat(marker.lonlat.lon, marker.lonlat.lat).transform( toProjection, fromProjection);
            return {
                latitude: lonlat.lat,
                longitude: lonlat.lon,
                radius: marker.radius
            };
        },

        saveLocationRadius: function(radius) {
            if (editedLocationMarker != null) {
                var length = markerLayer.markers.length;
                for (var i = 0 ; i < length ; i++) {
                    if (markerLayer.markers[i].id == editedLocationMarker.id) {
                        markerLayer.markers[i].radius = radius;
                        break;
                    }
                }
            }
            CoordinatorMap.closeLocationEditForm();
        },

        closeLocationEditForm: function() {
            if (popup != null) {
                map.removePopup(popup);
            }
        },

        showLocationEditForm: function(markerId) {
            CoordinatorMap.closeLocationEditForm();
            CoordinatorMap.setState(STATE_BROWSE);
            var length = markerLayer.markers.length;
            for (var i = 0 ; i < length ; i++) {
                if (markerLayer.markers[i].id == markerId) {
                    var marker = markerLayer.markers[i];
                    editedLocationMarker = marker;
                    switch (popupType) {
                        case "locationForm":
                            var form = $("#locationEditForm");
                            if (marker && marker.radius) {
                                form.find("#radius").attr("value", marker.radius);
                            }
                            popup = new OpenLayers.Popup(
                                    "Rozsah místa",
                                    marker.lonlat,
                                    new OpenLayers.Size(125, 100),
                                    form.html()
                            );
                            map.addPopup(popup);
                            popup.show();
                            break;
                    }
                }
            }
        },

        startSetLocation: function() {
            if (CoordinatorMap.getState() != STATE_SET_LOCATION) {
                CoordinatorMap.setState(STATE_SET_LOCATION);
            } else {
                CoordinatorMap.setState(STATE_BROWSE);
            }
        },

        stopSetLocation: function() {
            CoordinatorMap.setState(STATE_BROWSE);
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
                        'click': this.trigger
                    }, this.handlerOptions
            );
        },

        trigger: function(event) {
            if (CoordinatorMap.getState() == STATE_SET_LOCATION) {
                var lonLat = map.getLonLatFromPixel(event.xy);
                CoordinatorMap.addLocation(lonLat, null);
                if (maxLocations > 0)
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

        <c:if test="${!empty enableLocations and enableLocations}">

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

            panel.addControls([
                new OpenLayers.Control.Button({
                    title: "Add location",
                    trigger: function() {
                        CoordinatorMap.startSetLocation();
                    }
                })
            ]);
            map.addControl(panel);
        </c:if>

        var click = new OpenLayers.Control.Click();
        map.addControl(click);
        click.activate();

        map.setCenter(CoordinatorMap.position(${!empty longitude ? longitude : 14.4489967}, ${!empty latitude ? latitude : 50.0789306}), <c:out value="${!empty zoom ? zoom : 15}"/>);

        <c:if test="${!empty onLoad}">
            <c:out value="${onLoad}"/>;
        </c:if>
    });
</script>
<div id="mapContainer"></div>
<div id="locationEditForm" style="display: none;">
    <div><input id="radius" size="4"/> km</div>
    <div>
        <button type="button" onclick="CoordinatorMap.closeLocationEditForm()"><s:message code="button.cancel"/></button>
        <button type="button" onclick="CoordinatorMap.saveLocationRadius($('#radius').val())"><s:message code="button.ok"/></button>
    </div>
</div>