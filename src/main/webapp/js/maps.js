/*
Copyright 2015 Google Inc. All rights reserved.
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

/* Initializes Google Maps */
function initializeMap() {
    // Create an array of styles.
    var styles = [{"featureType": "landscape", "stylers": [{"saturation": -100}, {"lightness": 65}, {"visibility": "on"}]}, {"featureType": "poi", "stylers": [{"saturation": -100}, {"lightness": 51}, {"visibility": "simplified"}]}, {"featureType": "road.highway", "stylers": [{"saturation": -100}, {"visibility": "simplified"}]}, {"featureType": "road.arterial", "stylers": [{"saturation": -100}, {"lightness": 30}, {"visibility": "on"}]}, {"featureType": "road.local", "stylers": [{"saturation": -100}, {"lightness": 40}, {"visibility": "on"}]}, {"featureType": "transit", "stylers": [{"saturation": -100}, {"visibility": "simplified"}]}, {"featureType": "administrative.province", "stylers": [{"visibility": "off"}]}, {"featureType": "water", "elementType": "labels", "stylers": [{"visibility": "on"}, {"lightness": -25}, {"saturation": -100}]}, {"featureType": "water", "elementType": "geometry", "stylers": [{"hue": "#ffff00"}, {"lightness": -25}, {"saturation": -97}]}];

    // Create a new StyledMapType object, passing it the array of styles,
    // as well as the name to be displayed on the map type control.
    var styledMap = new google.maps.StyledMapType(styles,
            {name: "Styled Map"});

    // Get the location as a Google Maps latitude-longitude object
    var loc = new google.maps.LatLng(center_stations[0], center_stations[1]);

    // Create the Google Map
    map = new google.maps.Map(document.getElementById("map-canvas"), {
        center: loc,
        zoom: 11,
        mapTypeId: google.maps.MapTypeId.SATELLITE
    });

    // set the map style to active
    map.mapTypes.set('map_style', styledMap);
    map.setMapTypeId('map_style');

    map.controls[google.maps.ControlPosition.TOP].push(
            document.getElementById('userlegend'));

    // Create a draggable circle centered on the map
    var circle = new google.maps.Circle({
        strokeColor: "#6D3099",
        strokeOpacity: 0.7,
        strokeWeight: 1,
        fillColor: "#a3c2ee",
        fillOpacity: 0.35,
        map: map,
        center: loc,
        radius: ((radiusInKm_stations) * 1000),
        draggable: true
    });

    //Update the query's criteria every time the circle is dragged
    var updateCriteria = _.debounce(function () {
        var latLng = circle.getCenter();
        geoQuery_stations.updateCriteria({
            center: [latLng.lat(), latLng.lng()],
            radius: radiusInKm_stations
        });
    }, 10);
    google.maps.event.addListener(circle, "drag", updateCriteria);
}

function createStationMarker(station) {
    var bgcolor, fgcolor;
    if (station.owner !== "") {
        bgcolor = "888888";
        fgcolor = "EEE";
    } else {
        bgcolor = "50B1FF";
        fgcolor = "222";
    }
    // have fun with https://developers.google.com/chart/image/docs/gallery/dynamic_icons
    var marker = new google.maps.Marker({
        icon: "https://chart.googleapis.com/chart?chst=d_bubble_icon_text_small&chld=flag|bbT|" + encodeURI(station.name) + "|" + bgcolor + "|" + fgcolor,
        position: new google.maps.LatLng(station.latitude, station.longitude),
        optimized: true,
        map: map
    });

    var infowindow = new google.maps.InfoWindow({
        content: '<div style="overflow:hidden">'+station.owner+'</div>'
    });

    google.maps.event.addListener(marker, 'click', function () {
        infowindow.open(map, marker);
    });

    return marker;
}
