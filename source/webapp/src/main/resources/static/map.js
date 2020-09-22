function setupMap(stationsJs){
    var lonLat = ol.proj.fromLonLat([2.3488, 48.8534]); //Paris' center
    var zoom=15;

    var map = buildBaseMap(lonLat, zoom);
    addUserOnMapAndCenter(map);

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

function addUserOnMapAndCenter(map){
    if (!"geolocation" in navigator)
        return;

    navigator.geolocation.getCurrentPosition(function(position) {
        var userLonLat = ol.proj.fromLonLat([position.coords.longitude, position.coords.latitude]);
        map.getView().setCenter(userLonLat);

        var userStyle = new ol.style.Style({
          image: new ol.style.Icon({
            color: '#ff0000',
            anchor: [0.5, 1],
            src: 'user_marker.png'
          })
        });

        var userMarker = new ol.Feature({
            geometry: new ol.geom.Point(userLonLat)
        });
        userMarker.setStyle(userStyle);

        map.addLayer(
            new ol.layer.Vector({
                source: new ol.source.Vector({features: [userMarker]})
            })
        );
    });
}

function addStationsOnMap(map, stations){
    var okStationStyle = new ol.style.Style({
      image: new ol.style.Icon({
        color: '#46ff49',
        src: 'station_marker.png'
      })
    });

    var warningStationStyle = new ol.style.Style({
      image: new ol.style.Icon({
        color: '#ff7f00',
        src: 'station_marker.png'
      })
    });

    var koStationStyle = new ol.style.Style({
          image: new ol.style.Icon({
            color: '#ff0000',
            src: 'station_marker.png'
          })
        });

    var stationsMarkers = stations.map(station => {
        var stationLonLat = ol.proj.fromLonLat([station.longitude, station.latitude])
        var stationMarker = new ol.Feature({
            geometry: new ol.geom.Point(stationLonLat),
            station: station,

        });
        if(station.status == "OK")
            stationMarker.setStyle(okStationStyle);
        else if (station.status == "LOCKED")
            stationMarker.setStyle(warningStationStyle);
        else
            stationMarker.setStyle(koStationStyle);

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
            var station = stationMarker.get("station")
            $(popupHtml).popover({
                placement: 'top',
                html: true,
                sanitize: false,
                title: station.stationName,
                content: popupTemplate(station)
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
    var electricShare = station.electricBikes/station.totalCapacity*100;
    var mechanicalShare = station.mechanicalBikes/station.totalCapacity*100;
    var tfnLastChange = timeFromNow(station.lastChange);

    var htmlContent =
`<div class="progress">
    <div class="progress-bar bg-success" role="progressbar" style="width: ${mechanicalShare}%" aria-valuenow=${station.mechanicalBikes}  aria-valuemin="0" aria-valuemax=${station.totalCapacity}></div>
    <div class="progress-bar" role="progressbar" style="width: ${electricShare}%" aria-valuenow=${station.electricBikes} aria-valuemin="0" aria-valuemax=${station.totalCapacity}></div>
    </div>
<p>
    ${station.mechanicalBikes} mechanical bike(s)<br/>
    ${station.electricBikes} electric bike(s)<br/>
    ${station.emptySlots} empty slot(s)<br/>
    Status : ${station.status} <br/>
    <em>Last change : ${tfnLastChange.time} ${tfnLastChange.unitOfTime} ago</em>
    <hr/>
</p>`;

    return htmlContent;
}

function timeFromNow(time) {

	// Get timestamps
	var unixTime = new Date(time).getTime();
	if (!unixTime) return;
	var now = new Date().getTime();

	// Calculate difference
	var difference = (unixTime / 1000) - (now / 1000);

	// Setup return object
	var tfn = {};

	// Check if time is in the past, present, or future
	tfn.when = 'now';
	if (difference > 0) {
		tfn.when = 'future';
	} else if (difference < -1) {
		tfn.when = 'past';
	}

	// Convert difference to absolute
	difference = Math.abs(difference);

	// Calculate time unit
	if (difference / (60 * 60 * 24 * 365) > 1) {
		// Years
		tfn.unitOfTime = 'years';
		tfn.time = Math.floor(difference / (60 * 60 * 24 * 365));
	} else if (difference / (60 * 60 * 24 * 45) > 1) {
		// Months
		tfn.unitOfTime = 'months';
		tfn.time = Math.floor(difference / (60 * 60 * 24 * 45));
	} else if (difference / (60 * 60 * 24) > 1) {
		// Days
		tfn.unitOfTime = 'days';
		tfn.time = Math.floor(difference / (60 * 60 * 24));
	} else if (difference / (60 * 60) > 1) {
		// Hours
		tfn.unitOfTime = 'hours';
		tfn.time = Math.floor(difference / (60 * 60));
	} else if (difference / 60 > 1) {
      		// Hours
      		tfn.unitOfTime = 'minutes';
      		tfn.time = Math.floor(difference / 60);
    } else {
		// Seconds
		tfn.unitOfTime = 'seconds';
		tfn.time = Math.floor(difference);
	}

	// Return time from now data
	return tfn;
}
