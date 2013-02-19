(function ($) {

    $.fn.showMap = function(options) {

        var self = this;

        //config
        var config = {
            latitude : 49.038138,
            longitude : 13.482742,
            zoom : 14,
            zoomControl : true,
            layerControl : 'none',
            showMarkers : false,
            showInfo : true,
            customLayers: true,
            apiParamsCallBackFunction: function() {
                return { dummy: 1 };
            },
            dragMarkerCallBack : null,
            addMarker : null,
            moveCallBack : null,
            zoomCallBack : null,
            drawing: false
        };

        config = $.extend(config, options);

        var icons = [
            'bird',
            'bug_blue',
            'bug_green',
            'bug_grey',
            'bug_late_blue',
            'bug_late_green',
            'bug_late_grey',
            'bug_late_orange',
            'bug_late_red',
            'bug_orange',
            'bug_red',
            'cocoon_blue',
            'cocoon_green',
            'cocoon_grey',
            'cocoon_orange',
            'cocoon_red',
            'control',
            'culture',
            'damage',
            'egg_blue',
            'egg_green',
            'egg_grey',
            'egg_orange',
            'egg_red',
            'chamber_blue',
            'chamber_green',
            'chamber_grey',
            'chamber_orange',
            'chamber_red',
            'glade',
            'helicopter',
            'helicopter_level_1',
            'helicopter_level_2',
            'helicopter_level_3',
            'helicopter_level_4',
            'helicopter_level_5',
            'helicopter_level_6',
            'helicopter_level_7',
            'lapac_b',
            'lapac',
            'lapac_r',
            'lapac_v',
            'lapak_brought_b',
            'lapak_brought',
            'lapak_brought_poisoned_b',
            'lapak_brought_poisoned',
            'lapak_brought_poisoned_r',
            'lapak_brought_poisoned_v',
            'lapak_brought_r',
            'lapak_brought_v',
            'lapak_poisoned_b',
            'lapak_poisoned',
            'lapak_poisoned_r',
            'lapak_poisoned_v',
            'lapak_wind_b',
            'lapak_wind',
            'lapak_wind_r',
            'lapak_wind_v',
            'larva_blue',
            'larva_green',
            'larva_grey',
            'larva_orange',
            'larva_red',
            'mammal',
            'no_record',
            'other',
            'plant',
            'project_lapac',
            'project_lapak_brought',
            'project_lapak_brought_poisoned',
            'project_lapak_poisoned',
            'project_lapak_wind',
            'project_tripod',
            'shield',
            'spraycan',
            'tripod_b',
            'tripod',
            'tripod_r',
            'tripod_v',
            'unknown',
            'windsock'
        ];

        var names = {
            'entomopatogen' : 'Entomopatogen',
            'kurovec_bezzas' : 'Kurovec: bezzasahovy',//'KÅ¯rovec: bezzÃ¡sahovÃ½',
            'kurovec_nevyz' : 'Kurovec: nevyznaceny',//'KÅ¯rovec: nevyznaÄenÃ½',
            'kurovec_zas' : 'Kurovec: zasahovy',//'KÅ¯rovec: zÃ¡sahovÃ½',
            'lapace' : 'Lapace',//'LapaÄe',
            'lapaky' : 'Lapaky', //'LapÃ¡ky',
            'polom_bezzas' : 'Polom: bezzasahovy',//'Polom: bezzÃ¡sahovÃ½',
            'polom_zas' : 'Polom: zasahovy', //'Polom: zÃ¡sahovÃ½',
            'trojnozky' : 'Trojnozky' // 'TrojnoÅ¾ky'
        };

        var stateNames = {
            'todo' : '<b>k Å™eÅ¡enÃ­</b>',
            'mt' : 'v minitendru',
            'vkp' : 've VKP',
            'done' : 'vyÅ™eÅ¡en'
        };

        var markersLayer = null;
        var markers = [];

        var aggregation = false;

        var timerRunning = false;
        var timer = null;

        var infoPopup = null;

        var baseLayer;
        // var customLayers = [];

        //controls
        var panZoomBar = new OpenLayers.Control.PanZoomBar();

        // function to be called whenever a marker is dragged
        var draggableMarkers = false;

        function getMyUrl (bounds) {
            var res = this.map.getResolution();
            var x = Math.round ((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
            var y = Math.round ((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
            var z = this.map.getZoom();

            var path = z + "/" + x + "/" + y + "." + this.type;
            var url = this.url;
            if (url instanceof Array) {
                url = this.selectUrl(path, url);
            }
            return url + path;
        }

        self.map = null;

        setup();

        // initialize the map
        function setup() {

            // wait image
            var topPadding = self.height()/2 - 16; // imgHeight/2 = 16
            var rightPadding = self.width()/2 - 16; // imgWidth/2 = 16
            var html = '<div id="mapsLoaderImg" style="z-index:99999; position: absolute; width:20px; ' +
                'height : 20px; padding: ' + topPadding + 'px ' + rightPadding +
                'px; "><img src="' + root + '/images/ajax-loader.gif" /></div>';
            self.append(html);
            $("#mapsLoaderImg").hide();

            if (self.map == null) {
                self.map = new OpenLayers.Map(self.attr('id'), {
                    controls: [],
                    projection : new OpenLayers.Projection("EPSG:4326"),
                    displayProjection : new OpenLayers.Projection("EPSG:4326"),
                    numZoomLevels: 17
                });

                var baseLayer = null;
                baseLayer = new OpenLayers.Layer.OSM("Base layer");

                // markers layer
                if (config.showMarkers) {
                    addMarkersLayer();
                }

                self.map.addLayers([baseLayer]);
                self.map.setBaseLayer(baseLayer);

                self.map.events.register('zoomend', null, function(event) { onZoomChange(event) });
                self.map.events.register('moveend', null, function(event) { onMoveEnd(event) });

            }

            var projection = new OpenLayers.Projection("EPSG:4326");
            var center = new OpenLayers.LonLat(config.longitude, config.latitude);
            center.transform(projection, self.map.getProjectionObject());
            self.map.setCenter(center, config.zoom);

            // controls
            self.map.addControl(new OpenLayers.Control.Navigation()); //navigate by dragging, scrolling wheel, etc
            self.map.addControl(new OpenLayers.Control.MousePosition()); //show coords on bottom-right corner
            self.map.addControl(new OpenLayers.Control.OverviewMap());

            // zoom control
            if (config.zoomControl) {
                self.map.addControl(panZoomBar);
            }

            if (config.layerControl == 'maximized') {
                var layerControl = new OpenLayers.Control.LayerSwitcher();
                // layer switcher control
                self.map.addControl(layerControl);
                layerControl.maximizeControl();
            }

            // customlayers
            if (config.customLayers) {
                addCustomLayers();
            }

            // init markers
            addMarkers();
            if (config.dragMarkerCallBack != null || config.addMarker) {
                addDraggableMarker(config.dragMarkerCallBack != null);
            }
            addHeatmaps();
        }

        // event callbacks
        function onZoomChange(event) {
            if (config.showMarkers) {
                startAddMarkersTimer();
            }
            if (config.zoomCallBack != null) {
                config.zoomCallBack(self.map.getZoom());
            }
            // Fixme: Event.stop(event);
        }

        function onMoveEnd(event) {
            if (infoPopup != null) {
                self.map.removePopup(infoPopup);
                infoPopup.destroy();
                infoPopup = null;
            }
            if (config.showMarkers) {
                startAddMarkersTimer();
            }
            var proj = new OpenLayers.Projection("EPSG:4326");
            var lonlat = self.map.getCenter();
            lonlat.transform(self.map.getProjectionObject(), proj);
            if (config.moveCallBack != null) {
                config.moveCallBack(lonlat.lon, lonlat.lat);
            }
            //
            // Fixme: Event.stop(event);
        }

        function onFeatureSelect(feature) {
            var html = '<div id="recordDetail"><img src="' + root + '/images/ajax-loader.gif" /></div>';
            infoPopup = new OpenLayers.Popup.AnchoredBubble(null,
                new OpenLayers.LonLat(feature.geometry.x, feature.geometry.y),
                new OpenLayers.Size(300,250), html, null, true, function () { onPopupClose(feature) });
            feature.popup = infoPopup;
            feature.attributes.poppedup=true;
            self.map.addPopup(infoPopup);

            if (feature.attributes.typeId == "siteControl") {
                $('#recordDetail').load(root+"/admin/call_for_duty/site_control_edit.action?detailMaps=true&siteControl.siteControlId="+feature.attributes.recordId);
            }
            else if (feature.attributes.typeId == "aggregation") {
                $('#recordDetail').load(root+"/admin/location/discovery/detail.action?discoveryIds="+feature.attributes.recordIds);
            } else {
                $('#recordDetail').load(root+"/admin/location/discovery/detail.action?discovery.discoveryId="+feature.attributes.recordId);
            }
        }

        function onPopupClose(feature) {
            selectControl.unselect(feature);
        }

        function onFeatureUnselect(feature) {
            self.map.removePopup(feature.popup);
            feature.popup.destroy();
            feature.attributes.poppedup=false;
            feature.popup = null;
            infoPopup = null;
        }

        function addDraggableMarker(draggable) {

            var style = new OpenLayers.StyleMap({
                graphicWidth: 24,
                graphicHeight: 24,
                externalGraphic: root+"/images/maps/pin.png"
            });
            var draggableMarkerLayer = new OpenLayers.Layer.Vector('marker', {styleMap: style});
            self.map.addLayer(draggableMarkerLayer);

            var dragControl = new OpenLayers.Control.DragFeature(draggableMarkerLayer, {onComplete : function(feature) {
                var point = feature.geometry;
                var proj = new OpenLayers.Projection("EPSG:4326");
                var lonlat = new OpenLayers.LonLat(point.x, point.y);
                lonlat.transform(self.map.getProjectionObject(), proj);
                if (config.dragMarkerCallBack != null) {
                    config.dragMarkerCallBack(lonlat.lon, lonlat.lat);
                }
            }});

            self.map.addControl(dragControl);
            if (draggable) dragControl.activate();

            var coords = self.map.getCenter();

            draggableMarker = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(coords.lon, coords.lat), { 'icon' : 'draggable' });
            draggableMarkerLayer.addFeatures([draggableMarker]);

        }

        function addCustomLayers() {
            addCustomLayer("Obrysovky", "obrysovky");
            addCustomLayer("Obrysovky orto", "obrysovky-orto");
            addCustomLayer("Porostky", "porostky");
            addCustomLayer("Managementy", "managementy");
            addCustomLayer("Typologie", "typologie");
        }

        function addCustomLayer(name, layers) {
            var layer = new OpenLayers.Layer.OSM(name, root+"/wms-adapter/${z}/${x}/${y}.jpg?layer="+layers);
            self.map.addLayer(layer);
            return layer;
        }

        function addHeatmaps() {
            //var heatKurovecPhase = new OpenLayers.Layer.OSM("KÅ¯rovec - stÃ¡dium", "http://localhost/heatmaps/1.0.0/bugPhase/${z}/${x}/${y}.png",
            var heatKurovecPhase = new OpenLayers.Layer.OSM("KÅ¯rovec - stÃ¡dium", "http://lesis-maps.m-atelier.cz/heatmaps/1.0.0/bugPhase/${z}/${x}/${y}.png",
                {isBaseLayer: false, visibility: false}
            );
            self.map.addLayer(heatKurovecPhase);

            //var heatKurovecQuantity = new OpenLayers.Layer.OSM("KÅ¯rovec - mnoÅ¾stvÃ­", "http://localhost/heatmaps/1.0.0/bugQuantity/${z}/${x}/${y}.png",
            var heatKurovecQuantity = new OpenLayers.Layer.OSM("KÅ¯rovec - mnoÅ¾stvÃ­", "http://lesis-maps.m-atelier.cz/heatmaps/1.0.0/bugQuantity/${z}/${x}/${y}.png",
                {isBaseLayer: false, visibility: false}
            );
            self.map.addLayer(heatKurovecQuantity);

            //var heatTezba = new OpenLayers.Layer.OSM("TÄ›zba - mnoÅ¾stvÃ­", "http://localhost/heatmaps/1.0.0/harvestQuantity/${z}/${x}/${y}.png",
            var heatTezba = new OpenLayers.Layer.OSM("TÄ›zba - mnoÅ¾stvÃ­", "http://lesis-maps.m-atelier.cz/heatmaps/1.0.0/harvestQuantity/${z}/${x}/${y}.png",
                {isBaseLayer: false, visibility: false}
            );
            self.map.addLayer(heatTezba);

            var heatTezbaTotal2010 = new OpenLayers.Layer.OSM("TÄ›zba - celkem 2010", "http://lesis-maps.m-atelier.cz/heatmaps/1.0.0/totalHarvestQuantity2010/${z}/${x}/${y}.png",
                {isBaseLayer: false, visibility: false}
            );
            self.map.addLayer(heatTezbaTotal2010);

            var heatTezbaTotal2009 = new OpenLayers.Layer.OSM("TÄ›zba - celkem 2009", "http://lesis-maps.m-atelier.cz/heatmaps/1.0.0/totalHarvestQuantity2009/${z}/${x}/${y}.png",
                {isBaseLayer: false, visibility: false}
            );
            self.map.addLayer(heatTezbaTotal2009);
        }

        // creates the markers layer
        function addMarkersLayer() {

            if (markersLayer == null) {
                var style = new OpenLayers.StyleMap({
                    graphicWidth : 24,
                    graphicHeight : 24
                });

                var lookup = {};
                $.each(icons, function(i) {
                    lookup[icons[i]] = {externalGraphic : root+"/images/maps/"+icons[i]+".png"};
                });
                $.each(['bug', 'windsock'], function(i, v) {
                    for (var percentage = 0; percentage <= 100; percentage++) {
                        lookup[v + percentage] = {externalGraphic : root+"/map/?icon=" + v +"&percentage=" + percentage};
                    }
                });

                style.addUniqueValueRules("default", "icon", lookup);
                markersLayer = new OpenLayers.Layer.Vector('Nalezy', {styleMap: style});
            }
            self.map.addLayer(markersLayer);

            selectControl = new OpenLayers.Control.SelectFeature(markersLayer, {onSelect: onFeatureSelect,
                onUnselect: onFeatureUnselect,
                hover: false});
            self.map.addControl(selectControl);
            if (config.showInfo) {
                selectControl.activate();
            }

        }

        // Adds a 0.75 sec timer before requesting data,
        // to avoid large amounts of petitions to the server
        function startAddMarkersTimer() {
            $("#mapsLoaderImg").show();
            if (timerRunning) {
                clearTimeout(timer);
            }
            clearMarkers();
            timerRunning = true;
            timer = setTimeout(function () {
                addMarkers();
            }, 750);
        }

        function addMarkers() {
            if (markersLayer == null) {
                addMarkersLayer();
            }
            if (config.showMarkers) {
                if (self.map.getZoom() >= 14) {
                    $("#mapMarkersWarning").hide();
                    var bounds = self.map.getExtent();
                    var proj = new OpenLayers.Projection("EPSG:4326");
                    bounds.transform(self.map.getProjectionObject(), proj);
                    var arrBounds = bounds.toArray(); //array order: left, bottom, right, top
                    var request = {
                        minLatitude : arrBounds[1], maxLatitude : arrBounds[3],
                        minLongitude: arrBounds[0], maxLongitude : arrBounds[2]
                    };
                    if (self.map.aggregation) {
                        request.aggregation = 'TRUE';
                    }

                    request = $.extend(request, config.apiParamsCallBackFunction() );

                    var decodedRequest = $.param(request);
                    var url = root+'/api/maps/search?callback=?';

                    $.getJSON(url, decodedRequest, function(response, txt) {
                        if (response.status != 'ok') {
                            alert('Server error: ' + response.msg);
                        } else {
                            if (response.data != null) {
                                $.each(response.data, function(i, item) {
                                    // add data
                                    createMarker(item);
                                });
                            }
                        }
                        $("#mapsLoaderImg").hide();
                    });
                } else {
                    $("#mapMarkersWarning").show();
                    $("#mapsLoaderImg").hide();
                }
            } else {
                $("#mapsLoaderImg").hide();
            }
        }

        function clearMarkers() {
            if (markersLayer != null) {
                markersLayer.destroyFeatures(markers);
                markers = [];
            }
            if (config.drawing) {
                olmapDrawing.removeImmutableItems(null);
            }
        }

        // creates and adds marker to the layer
        function createMarker(item) {
            var coords = new OpenLayers.LonLat(item.longitude, item.latitude);
            var proj = new OpenLayers.Projection("EPSG:4326");
            coords.transform(proj, self.map.getProjectionObject());
            var feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(coords.lon, coords.lat), item);
            markers[markers.length] = feature;
            markersLayer.addFeatures([feature]);
            if (config.drawing && item.area != null) {
                olmapDrawing.addImmutableItems(item.typeId, item.area.geometryItems);
            }
        }

        function refreshMarkers() {
            showMarkers(false);
            showMarkers(true);
        }

        function showMarkers(show) {
            config = $.extend(config, {showMarkers : show});
            if (!show) {
                clearMarkers();
            } else {
                addMarkers();
            }
        }

        function showInfoWindow(show) {
            config = $.extend(config, {showInfo : show});
            if (!show) {
                selectControl.deactivate();
            } else {
                selectControl.activate();
            }
        }

        function setDragMarkerCallBack(f) {
            config = $.extend(config, {dragMarkerCallBack : f});
        }

        function setMoveCallBack(f) {
            config = $.extend(config, {moveCallBack : f});
        }

        function setZoomCallBack(f) {
            config = $.extend(config, {zoomCallBack : f});
        }

        // 'public' functions
        $.extend(self, {

            //move map to given position and zoom level
            moveTo : function(lat, lon, zoom) {
                var projection = new OpenLayers.Projection("EPSG:4326");
                var center = new OpenLayers.LonLat(lon, lat);
                center.transform(projection, self.map.getProjectionObject());
                self.map.setCenter(center, zoom);
            },

            //add (true) or remove (false) zoom control
            setZoomControl : function(activated) {
                self.map.removeControl(panZoomBar);
                if (activated) {
                    self.map.removeControl(panZoomBar);
                }
            },

            showMarkers : function (show) {
                showMarkers(show);
            },

            refreshMarkers : function () {
                refreshMarkers();
            },

            showInfoWindow : function (show) {
                showInfoWindow(show);
            },

            getLayers : function () {
                return self.map.getLayersByName('*');
            },

            addControl : function (control) {
                return self.map.addControl(control);
            },

            addLayers : function (layers) {
                return self.map.addLayers(layers);
            },

            getLonLatFromViewPortPx : function (xy) {
                return self.map.getLonLatFromViewPortPx(xy);
            },

            getProjectionObject : function () {
                return self.map.getProjectionObject();
            },
            setLayer : function (layerName) {
                var layers = self.map.getLayersByName(layerName);
                self.map.setBaseLayer(layers[0]);
            },

            setAggregation :    function (enabled) {
                self.map.aggregation = true == enabled;
            },

            showHeatmap : function (show, layerName) {
                var layers = [];
                if (layerName.startsWith('helicopter-')) {
                    if (show) {
                        //var l = new OpenLayers.Layer.OSM(layerName, 'http://localhost/heatmaps/1.0.0/' + layerName + '/${z}/${x}/${y}.png',
                        var l = new OpenLayers.Layer.OSM(layerName, 'http://lesis-maps.m-atelier.cz/heatmaps/1.0.0/' + layerName + '/${z}/${x}/${y}.png',
                            {isBaseLayer: false, visibility: true}
                        );
                        self.map.addLayer(l);
                        l.setVisibility(true);
                    } else {
                        layers = self.map.getLayersByName(layerName);
                        if (layers[0]) self.map.removeLayer(layers[0])
                    }
                    return;
                } else if (layerName == 'heatmapBugPhase') {
                    layers = self.map.getLayersByName('KÅ¯rovec - stÃ¡dium');
                } else if (layerName == 'heatmapBugQuantity') {
                    layers = self.map.getLayersByName('KÅ¯rovec - mnoÅ¾stvÃ­');
                } else if (layerName == 'heatmapHarvestQuantity') {
                    layers = self.map.getLayersByName('TÄ›zba - mnoÅ¾stvÃ­');
                } else if (layerName == 'heatmapHarvestTotalQuantity2010') {
                    layers = self.map.getLayersByName('TÄ›zba - celkem 2010');
                } else if (layerName == 'heatmapHarvestTotalQuantity2009') {
                    layers = self.map.getLayersByName('TÄ›zba - celkem 2009');
                }
                if (layers[0]) layers[0].setVisibility(show);
            },

            setDragMarkerCallBack : function(f) {
                setDragMarkerCallBack(f);
            },

            setMoveCallBack : function(f) {
                setMoveCallBack(f);
            },

            setZoomCallBack : function(f) {
                setZoomCallBack(f);
            }

        });
        return this;
    };
})(jQuery);
