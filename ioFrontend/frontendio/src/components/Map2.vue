<template>
  <div class="container">
    <div id="map"></div>
  </div>
</template>

<script>
// Importowanie odpowiednich zależności
import 'leaflet-routing-machine/dist/leaflet-routing-machine.css';
import L from 'leaflet';
import 'leaflet-routing-machine';
import router from "@/router.js";

export default {
  name: 'Map2',
  props: {
    resourceLatitude: {
      type: String,  // Możesz użyć Number, jeśli preferujesz liczby
      required: true
    },
    resourceLongitude: {
      type: String,  // Możesz użyć Number, jeśli preferujesz liczby
      required: true
    },
    requestLatitude: {
      type: String,  // Możesz użyć Number, jeśli preferujesz liczby
      required: true
    },
    requestLongitude: {
      type: String,  // Możesz użyć Number, jeśli preferujesz liczby
      required: true
    }
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }

    // Inicjalizacja mapy
    var map = L.map('map').setView([57.74, 11.94], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(map);


    console.log('Latitude:', this.resourceLatitude);
    console.log('Longitude:', this.resourceLongitude);
    console.log('Longitude:', this.requestLatitude);
    console.log('Longitude:', this.requestLongitude);

    // Inicjalizacja kontrolera tras
    L.Routing.control({
      waypoints: [
        L.latLng(this.resourceLatitude, this.resourceLongitude),
        L.latLng(this.requestLatitude, this.requestLongitude)
      ],
      router: L.Routing.mapbox('pk.eyJ1IjoicGF0cnlrc2FseWdhIiwiYSI6ImNtNjJ5Zjc1ZzBzeW0yd3NlZmE3bHU1cTAifQ.eN4Te8gS1ZsriC9bq_Tvjg'),
      routeWhileDragging: true
    }).addTo(map);
  },
};
</script>

<style>
#map {
  height: 80vh;
  width: 54vw;
  margin: 5vh;
}
</style>
