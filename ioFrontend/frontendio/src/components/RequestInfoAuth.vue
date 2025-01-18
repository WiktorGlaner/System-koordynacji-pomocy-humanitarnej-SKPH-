
<template>
  <div class="carddiv">
    <div v-for="(req, index) in requests" :key="index" :class="`req${req.requestId}`">
      <div class="card h-100 mx-5">
        <ul class="list-group list-group-flush">
          <li class="list-group-item">
            <div class="edit">
              <button
                  class="btn btn-outline-dark"
                  type="button"
                  :disabled="req.status !== 'REGISTERED'"
                  @click="deleteRequest(req.reporter.user.username, req.requestId)">
                <font-awesome-icon icon="remove"/>
              </button>
            </div>
          </li>
          <li class="list-group-item">
            <strong>ID:</strong> {{ req.requestId }}
          </li>
          <li class="list-group-item">
            <strong>{{$t('req-desc')}}:</strong> {{ req.description }}
          </li>
          <li class="list-group-item">
            <strong>{{$t('req-amount')}}:</strong> {{ req.amount }}
          </li>
          <li class="list-group-item">
            <strong>{{$t('req-type')}}:</strong> {{ $t(`req-${req.resourceType}`) }}
          </li>
          <li class="list-group-item">
            <strong>{{$t('req-name')}}:</strong> {{ req.resourceName }}
          </li>
          <li class="list-group-item">
            <strong>Status:</strong> {{ $t(`req-${req.status}`) }}
          </li>
          <li class="list-group-item">
            <strong>Reporter:</strong>
            <ul class="">
              <li>
                <strong>{{$t('req-name')}}:</strong> {{ req.reporter ? req.reporter.name : $t('req-none') }}
              </li>
              <li>
                <strong>{{$t('req-surname')}}:</strong> {{ req.reporter ? req.reporter.surname : $t('req-none') }}
              </li>
              <li>
                <strong>Email:</strong> {{ req.reporter ? req.reporter.user.email : $t(`req-none`) }}
              </li>
            </ul>
          </li>
          <li class="list-group-item">
            <div :id="`map-container${req.requestId}`" :class="`map-container`"></div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import RequestService from "@/services/request.service.js";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {toRaw} from "vue";
import { useToast } from 'vue-toastification';

export default {
  components: {FontAwesomeIcon},
  data() {
    return {
      requests: [],
      types: RequestService.types.reduce((acc, type) => ({ ...acc, ...type }), {}),
      statuses: RequestService.statuses.reduce((acc, status) => ({ ...acc, ...status }), {}),
      resourceTypes: [],
      username: null,
    };
  },
  created() {
    this.fetchRequests();
    this.getUser();
    this.getResourceTypes();
  },
  methods: {
    async fetchRequests() {
      const response = await RequestService.getAllRequests();
      this.requests = response.data.sort((a, b) => a.requestId - b.requestId);
      this.requests.forEach(r => this.getMap(r));
    },
    getUser() {
      const userData = localStorage.getItem('user');
      const user = JSON.parse(userData);
      this.username = user.username;
    },
    async deleteRequest(username, id) {
      const toast = useToast();
      try {
        await RequestService.deleteRequest(username, id);
        const response = await RequestService.getAllRequests();
        this.requests = response.data.sort((a, b) => a.requestId - b.requestId);;
        toast.success(this.$t('req-delete-succ'))
      } catch(error) {
        toast.error(this.$t('req-delete-fail'));
      }
    },
    async getResourceTypes() {
      const response = await RequestService.getRequestTypes();
      this.resourceTypes = response.data;
    },
    getMap(req) {
      console.log(req);
      this.$nextTick(() => {
        const mapContainer = document.getElementById(`map-container${req.requestId}`);
        const map = L.map(mapContainer).setView([req.latitude, req.longitude], 14);
        L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
          maxZoom: 19,
          attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        }).addTo(map);
        L.circle([req.latitude, req.longitude],
            {
              color: 'red',
              fillColor: '#f03',
              fillOpacity: 0.5,
              radius: 75
            })
            .addTo(toRaw(map));
      })
    },
  }
};
</script>

<style scoped>
  .map-container {
    width: 100%;
    height: 200px;
  }
</style>