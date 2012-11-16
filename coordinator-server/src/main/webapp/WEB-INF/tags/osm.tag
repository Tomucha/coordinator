<%@
        attribute name="width" required="true" %><%@
        attribute name="height" required="true" %><%@
        attribute name="zoom" required="false" type="java.lang.Integer" %><%@
        attribute name="longitude" required="true" type="java.lang.Double" %><%@
        attribute name="latitude" required="true" type="java.lang.Double" %><%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@
        taglib prefix="s" uri="http://www.springframework.org/tags"
%><style type="text/css">
    #mapContainer {
        width: ${!empty width ? width : "100%"};
        height: ${!empty height ? height : "100%"};
    }
</style>
<!-- <script src="${root}/js/osm/OpenLayers.js"></script> -->
<script type="text/javascript" src="http://openlayers.org/api/OpenLayers.js"></script>
<script>
    var map, mapLayer, markerLayer, fromProjection, toProjection;

    var STATE_BROWSE = 0;
    var STATE_SET_LOCATION = 1;

    var state = STATE_BROWSE;

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

        addLocation: function(lonLat) {
            var marker = new OpenLayers.Marker(lonLat, icon.clone());
            marker.id = new Date().getTime();
            marker.events.register("click", marker, function(event) {
                editedLocationMarker = marker;
                CoordinatorMap.showLocationEditForm(marker.id);
            });

            markerLayer.addMarker(marker);
        },

        getLocations: function() {
            var length = markerLayer.markers.length;
            result = new Array();
            for (var i = 0 ; i < length ; i++) {
                var marker = markerLayer.markers[i];
                var lonlat = new OpenLayers.LonLat(marker.lonlat.lon, marker.lonlat.lat).transform( toProjection, fromProjection);
                result[i] = {
                    latitude: lonlat.lat,
                    longitude: lonlat.lon,
                    radius: marker.radius
                };
            }

            return result;
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
            var length = markerLayer.markers.length;
            for (var i = 0 ; i < length ; i++) {
                if (markerLayer.markers[i].id == markerId) {
                    var marker = markerLayer.markers[i];
                    editedLocationMarker = marker;
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
                }
            }
        },

        startSetLocation: function() {
            CoordinatorMap.setState(STATE_SET_LOCATION);
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
                CoordinatorMap.addLocation(lonLat);
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
        map.setCenter(CoordinatorMap.position(<c:out value="${longitude}"/>, <c:out value="${latitude}"/>), <c:out value="${!empty zoom ? zoom : 15}"/>);
        var click = new OpenLayers.Control.Click();
        map.addControl(click);
        click.activate();
    });
</script>
<div id="mapContainer"></div>
<div id="locationEditForm" style="display: none;">
    <div><input id="radius" size="4"/> km</div>
    <div>
        <button type="button" onclick="CoordinatorMap.closeLocationEditForm()">Zrušit</button>
        <button type="button" onclick="CoordinatorMap.saveLocationRadius($('#radius').val())">Ok</button>
    </div>
</div>