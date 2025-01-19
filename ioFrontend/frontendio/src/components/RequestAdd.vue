<template>
  <form @submit.prevent="submitRequest" class="border p-4 rounded shadow-sm">
    <h3 class="mb-4 text-center">{{ $t('req-form-title') }}</h3>

    <div class="mb-3">
      <label for="resourceType" class="form-label">{{ $t('req-type') }}:</label>
      <select v-model="newRequest.resourceType" id="resourceType" class="form-select" required>
        <option v-for="(type, index) in resourceTypes" :key="index" :value="type">
          {{ $t(`req-${type}`) }}
        </option>
      </select>
    </div>

    <div class="mb-3">
      <label for="resourceName" class="form-label">{{ $t('req-name') }}:</label>
      <input v-model="newRequest.resourceName" id="resourceName" type="text" class="form-control" required />
    </div>

    <div class="mb-3">
      <label for="amount" class="form-label">{{ $t('req-amount') }}:</label>
      <input v-model.number="newRequest.amount" id="amount" type="number" class="form-control" required />
    </div>

    <div class="mb-3">
      <label for="description" class="form-label">{{ $t('req-desc') }}:</label>
      <textarea v-model="newRequest.description" id="description" class="form-control" rows="4"></textarea>
    </div>

    <div class="mb-3">
      <label for="location" class="form-label">{{ $t('req-loc') }}:</label>
      <div :id="`map-container`" class="map-container"></div>
    </div>

    <div class="d-grid">
      <button type="submit" class="btn btn-primary btn-lg" @click="addRequest(this.username, newRequest)">{{ $t('req-send') }}</button>
    </div>
  </form>
</template>

<script>
import RequestService from "@/services/request.service.js";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import { useToast } from 'vue-toastification';

export default {
  components: {FontAwesomeIcon},
  data() {
    return {
      map: "",
      requests: [],
      types: RequestService.types.reduce((acc, type) => ({ ...acc, ...type }), {}),
      statuses: RequestService.statuses.reduce((acc, status) => ({ ...acc, ...status }), {}),
      roles: [],
      resourceTypes: [],
      newRequest: {
        description: "",
        longitude: 20.323,
        latitude: 64.343,
        resourceType: "",
        amount: null,
        resourceName: "",
      },
      username: null,
      newCords: {
        lat: null,
        lng: null,
      },
      blueCircle: null,
    };
  },
  created() {
    this.getUser();
    this.getResourceTypes();
  },
  methods: {
    getUser() {
      const userData = localStorage.getItem('user');
      const user= JSON.parse(userData);
      this.username = user.username;
    },
    async getResourceTypes() {
      const response = await RequestService.getRequestTypes();
      this.resourceTypes = response.data;
      await this.getMapDefault();
    },
    async addRequest(username, newRequestData) {
      const toast = useToast();
      try {
        if (!newRequestData.resourceName.trim()) {
          toast.error(this.$t(`req-name-error`));
          return;
        }
        if (newRequestData.amount <= 0 || isNaN(newRequestData.amount)) {
          toast.error(this.$t(`req-amount-error`));

          return;
        }
        if(this.newCords.lat === null || this.newCords.lng === null) {
          toast.error(this.$t(`req-loc-error`));

          return
        }
        if(newRequestData.resourceType === "") {
          toast.error(this.$t(`req-type-error`));
          return
        }

        newRequestData.latitude = this.newCords.lat;
        newRequestData.longitude = this.newCords.lng;
        this.newCords.lat = null;
        this.newCords.lng = null;
        await RequestService.addRequest(username, newRequestData);
        this.newRequest.description = "";
        this.newRequest.amount = null;
        this.newRequest.resourceName = "";
        this.newRequest.resourceType = "";
        this.newRequest.latitude = null;
        this.newRequest.longitude = null;

        this.map.removeLayer(this.blueCircle);
        this.map.setView([51.75, 19.45], 14);

        window.scrollTo({
          top: 0,
          behavior: 'smooth'
        });
        
        toast.success(this.$t('req-add-succ'));
      } catch (error) {
        toast.error(this.$t('req-add-fail'));
      }
    },
    async getMapDefault() {
      await this.$nextTick();
      const mapContainer = document.getElementById(`map-container`);
      if ("geolocation" in navigator) {
        const toast = useToast();
        navigator.geolocation.getCurrentPosition(
            (position) => {
              const { latitude, longitude } = position.coords;
              this.map = L.map(mapContainer).setView([latitude, longitude], 14);

              L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
                maxZoom: 19,
                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
              }).addTo(this.map);

              this.map.on('click', (ee) => {
                const { lat, lng } = ee.latlng;
                if (this.blueCircle) {
                  this.map.removeLayer(this.blueCircle);
                }
                this.blueCircle = L.circle([lat, lng], {
                  color: 'blue',
                  fillColor: '#03f',
                  fillOpacity: 0.5,
                  radius: 75,
                }).addTo(this.map);

                this.newCords.lat = lat;
                this.newCords.lng = lng;
              });
            },
            () => {
              toast.info(this.$t('req-loc-map-error'));
              this.map = L.map(mapContainer).setView([51.75, 19.45], 14);
              L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
                maxZoom: 19,
                attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
              }).addTo(this.map);

              this.map.on('click', (ee) => {
                const { lat, lng } = ee.latlng;
                if (this.blueCircle) {
                  this.map.removeLayer(this.blueCircle);
                }
                this.blueCircle = L.circle([lat, lng], {
                  color: 'blue',
                  fillColor: '#03f',
                  fillOpacity: 0.5,
                  radius: 75,
                }).addTo(this.map);

                this.newCords.lat = lat;
                this.newCords.lng = lng;
              });
            }
        );
      } else {
        console.warn("Geolocation not supported by your browser.");
        this.map = L.map(mapContainer).setView([51.75, 19.45], 14);
        L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
          maxZoom: 19,
          attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        }).addTo(this.map);
      }
    },
  }
};
</script>


<style scoped>
  .map-container {
    width: 100%;
    height: 500px;
  }
</style>