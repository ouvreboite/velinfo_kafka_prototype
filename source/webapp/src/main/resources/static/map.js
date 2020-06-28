function setupMap(stationsJs){
    var lonLat = ol.proj.fromLonLat([2.3488, 48.8534]); //Paris' center
    var zoom=15;

    var map = buildBaseMap(lonLat, zoom);

    if(stationsJs){
        addStationsOnMap(map, stationsJs);
        addPopupForStations(map);
    }
}

function buildBaseMap(lonLat, zoom){
    var baseMapLayer = new ol.layer.Tile({
        source: new ol.source.OSM()
    });

    var map = new ol.Map({
        target: 'map',
        layers: [ baseMapLayer],
        view: new ol.View({
            center: lonLat,
            zoom: zoom
        })
    });

    return map;
}

function addStationsOnMap(map, stations){
    var iconStyle = new ol.style.Style({
      image: new ol.style.Icon({
        color: '#ffcd46',
        src: 'marker.png'
      })
    });

    var stationsMarkers = stations.map(station => {
        var stationLonLat = ol.proj.fromLonLat([station.longitude, station.latitude])
        var stationMarker = new ol.Feature({
            geometry: new ol.geom.Point(stationLonLat),
            station: station,

        });
        stationMarker.setStyle(iconStyle);
        return stationMarker;
    });

    map.addLayer(
        new ol.layer.Vector({
            source: new ol.source.Vector({features: stationsMarkers})
        })
    );
}

function addPopupForStations(map){
    var popupHtml = document.getElementById('popup');
    var popup = new ol.Overlay({
      element: popupHtml,
      positioning: 'bottom-center',
      stopEvent: false,
      offset: [0, -20]
    });

    map.addOverlay(popup);

    map.on('click', function(evt) {
        var stationMarker = map.forEachFeatureAtPixel(evt.pixel,
            function(feature) {
              return feature;
            });
        $(popupHtml).popover('dispose');
        if (stationMarker && stationMarker.get("station")) {
            var coordinates = stationMarker.getGeometry().getCoordinates();
            popup.setPosition(coordinates);
            $(popupHtml).popover({
              placement: 'top',
              html: true,
              content: popupTemplate(stationMarker.get("station"))
            });
            $(popupHtml).popover('show');
        }
    });

    var mapHtml = document.getElementById('map');
    map.on('pointermove', function(evt) {
        if (evt.dragging) {
            $(popupHtml).popover('dispose');
            return;
        }
        var pixel = map.getEventPixel(evt.originalEvent);
        var stationMarker = map.forEachFeatureAtPixel(pixel,function(feature) {
          return feature;
        });
        if (stationMarker && stationMarker.get("station")) {
            mapHtml.style.cursor = 'pointer';
        }else{
            mapHtml.style.cursor = '';
        }
    });
}

function popupTemplate(station){
    return "<strong>"+station.stationName+"</strong><br/>"+station.totalCapacity;
}
