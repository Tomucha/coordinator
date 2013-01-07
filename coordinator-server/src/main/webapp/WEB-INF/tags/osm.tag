<%@
        attribute name="width" required="true" %><%@
        attribute name="height" required="true" %><%@
        attribute name="zoom" required="false" type="java.lang.Integer" %><%@
        attribute name="longitude" required="false" %><%@
        attribute name="latitude" required="false" %><%@
        attribute name="onLoad" required="false" %><%@
        attribute name="onNewPoint" required="false" %><%@
        attribute name="buttons" required="false" %><%@
        attribute name="maxPoints" required="false" %><%@
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

    size = new OpenLayers.Size(21, 25);

    // TODO deprecated
    var icon = new OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));

    var TYPE_POI = "poi";
    var TYPE_LOCATION = "loc";
    var TYPE_USER = "usr";

    var icons = {};
    icons[TYPE_LOCATION] = new OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));
    icons[TYPE_USER] = new OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker-blue.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));
    icons[TYPE_POI] = new OpenLayers.Icon('http://www.openstreetmap.org/openlayers/img/marker-gold.png', size, new OpenLayers.Pixel(-(size.w / 2), -size.h));

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
            var marker = new OpenLayers.Marker(lonLat, icons[point.type].clone());
            point.id = marker.id = "point" + Math.floor(Math.random() * 100000);

            if (CoordinatorMap.clickHandlers[point.type]) {
                marker.events.register("click", marker, function(event) {
                    selectedPointId = point.id;
                    CoordinatorMap.setState(STATE_BROWSE);
                    CoordinatorMap.closePopup();

                    var elementId = CoordinatorMap.clickHandlers[point.type](point);
                    if (elementId != null) {
                        var win = $(elementId);
                        win.find("input, select, textarea").each(function(index, input) {
                            var name = $(input).attr("name");
                            if (name) {
                                $(input).attr("value", point[name] ? point[name] : "");
                            }
                        });

                        popup = new OpenLayers.Popup(
                                "Rozsah místa",
                                marker.lonlat,
                                new OpenLayers.Size(125, 150),
                                win.html()
                        );
                        map.addPopup(popup);
                        popup.show();
                    }
                });
            }

            points[point.id] = point;

            markerLayer.addMarker(marker);

            <c:if test="${!empty onNewPoint}">
            ${onNewPoint}(point);
            </c:if>

            return point;
        },

        getPointById: function(id) {
            return points[id];
        },

        getPoints: function() {
            return points;
        },

        closeAndSavePopup: function() {
            if (popup != null && popup.visible()) {
                var data = {};
                $(popup.div).find("input, select, textarea").each(function(index, input) {
                    var input = $(input);
                    var name = input.attr("name");
                    if (name)
                        data[name] = input.val() != "" ? input.val() : null;
                });
                if (data.id) {
                    for (index in data) {
                        points[data.id][index] = data[index];
                    }
                }
            }
            CoordinatorMap.closePopup();
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
            } else {
                CoordinatorMap.setState(STATE_BROWSE);
                currentPointType = null;
            }
        },

        stopSetLocation: function() {
            CoordinatorMap.setState(STATE_BROWSE);
        }
    };

    CoordinatorMap.clickHandlers[TYPE_LOCATION] = function(point) {
        return "#locationEditForm";
    }
    CoordinatorMap.clickHandlers[TYPE_USER] = function(point) {
        return "#userForm";
    }
    CoordinatorMap.clickHandlers[TYPE_POI] = function(point) {
        return "#poiForm";
    }

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
                    longitude: lonLat.lon
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
                switch (buttonType.replace(/\s*/, "")) {
                    case "addLocation":
                        controls[controls.length] = new OpenLayers.Control.Button({
                            title: "+location",
                            trigger: function() {
                                CoordinatorMap.startSetLocation(TYPE_LOCATION);
                            }
                        });
                        break;

                    case "addPlace":
                        controls[controls.length] = new OpenLayers.Control.Button({
                            title: "+place",
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
        <form action="${root}/admin/event/place/edit">
            <div>
                <input type="hidden" name="placeId"/>
                <button type="button" onclick="CoordinatorMap.closePopup()"><s:message code="button.cancel"/></button>
                <button type="submit"><s:message code="button.edit"/></button>
            </div>
        </form>
    </div>
</div>