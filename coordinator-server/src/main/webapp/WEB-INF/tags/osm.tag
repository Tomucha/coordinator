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

    var CoordinatorMap = {
        position: function(lon, lat) {
            return new OpenLayers.LonLat(lon, lat).transform( fromProjection, toProjection);
        },

        addLocation: function(lon, lat) {

        },

        startSetLocation: function() {
            state = STATE_SET_LOCATION;
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
            if (state == STATE_SET_LOCATION) {
                var lonLat = map.getLonLatFromPixel(event.xy);
                marker = new OpenLayers.Marker(lonLat, icon.clone());
                markerLayer.addMarker(marker);
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