<template>
  <div class="carddiv">
    <div v-for="(req, index) in userRequests.request" :key="index" :class="`req${req.requestId}`">
      <div class="card h-100 mx-5">
        <ul class="list-group list-group-flush">
          <li class="list-group-item">
            <div class="edit">
              <button
                  class="btn btn-outline-dark"
                  :disabled="req.status !== 'REGISTERED'"
                  type="button"
                  @click="deleteUserRequest(req.reporter.user.username, req.requestId)">
                <font-awesome-icon icon="remove"/>
              </button>
              <button
                  class="btn btn-outline-dark"
                  type="button"
                  :disabled="req.status !== 'REGISTERED'"
                  @click="enableEditMode(req.requestId)">
                <font-awesome-icon icon="edit"/>
              </button>
              <button
                  v-if="editMode.edit && editMode.requestId === req.requestId"
                  class="btn btn-outline-dark"
                  @click="cancel(req)">
                <font-awesome-icon icon="cancel" />
              </button>
              <button
                  v-if="editMode.edit && editMode.requestId === req.requestId"
                  class="btn btn-outline-dark"
                  @click="updateRequest(req.username, req.requestId, req.description, req.resourceType, req.amount, req.resourceName)">
                <font-awesome-icon icon="check" />
              </button>
            </div>
          </li>
          <li class="list-group-item">
            <strong>ID:</strong> {{ req.requestId }}
          </li>
          <div v-if="editMode.edit && editMode.requestId === req.requestId">
            <form action="">
              <li class="list-group-item">
                <div class="form-group d-flex align-items-center">
                  <label for="descId" class="me-2"><strong>{{$t('req-desc')}}:</strong></label>
                  <input v-model="req.description" type="text" class="form-control form-control-sm" id="descId{{ req.requestId }}" />
                </div>
              </li>
              <li class="list-group-item">
                <div class="form-group d-flex align-items-center">
                  <label for="descId" class="me-2"><strong>{{$t('req-amount')}}:</strong></label>
                  <input v-model="req.amount" type="number" class="form-control form-control-sm" id="amountId{{ req.requestId }}" />
                </div>
              </li>
              <li class="list-group-item">
                <div class="form-group d-flex">
                  <label for="resourceType" class="me-2"><strong>{{$t('req-type-form')}}:</strong></label>
                  <select v-model="req.resourceType" id="resourceTypeId{{ req.requestId }}" class="form-select form-select-sm" required>
                    <option v-for="(type, index) in resourceTypes" :key="index" :value="type">
                      {{ $t(`req-${type}`) }}
                    </option>
                  </select>
                </div>
              </li>
              <li class="list-group-item">
                <div class="form-group d-flex align-items-center">
                  <label for="nameId{{ req.requestId }}" class="me-2"><strong>{{$t('req-name')}}:</strong></label>
                  <input v-model="req.resourceName" type="text" class="form-control form-control-sm" id="nameId" />
                </div>
              </li>
            </form>
          </div>
          <div v-else>
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
          </div>
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
      types: RequestService.types.reduce((acc, type) => ({ ...acc, ...type }), {}),
      statuses: RequestService.statuses.reduce((acc, status) => ({ ...acc, ...status }), {}),
      resourceTypes: [],
      username: null,
      userRequests: [
        {
          request: {
            id: null,
            map: null,
            blueCircle: null,
            redCircle: null,
            latitude: null,
            longitude: null,
            clear: false,
          }
        }
      ],
      editMode: {
        edit: null,
        requestId: null,
      },
      init: true,
      initCount: 0,
    };
  },
  created() {
    this.getUser();
    this.checkRole();
    this.getRequestByUser(this.username);
    this.getResourceTypes();
  },
  methods: {
    getUser() {
      const userData = localStorage.getItem('user');
      const user = JSON.parse(userData);
      this.username = user.username;
    },
    checkRole() {
      const userData = localStorage.getItem('user');
      const user = JSON.parse(userData);
      this.roles = user.roles;
    },
    async getResourceTypes() {
      const response = await RequestService.getRequestTypes();
      this.resourceTypes = response.data;
    },
    async deleteUserRequest(username, id) {
      const toast = useToast();
      try {
      await RequestService.deleteRequest(username, id);
      const response = await RequestService.getRequestsByUser(username);
      this.userRequests.request = response.data.sort((a, b) => a.requestId - b.requestId);
      this.userRequests.forEach(req => this.getMap(req));
      toast.success(this.$t('req-delete-succ'))
      } catch(error) {
        toast.error(this.$t('req-delete-fail'));
      }
    },
    async getRequestByUser(username) {
      const response = await RequestService.getRequestsByUser(username);
      this.userRequests.request = response.data.sort((a, b) => a.requestId - b.requestId);
      if (this.init) {
        this.init = false;
        this.userRequests.request.forEach(req => {
          this.getMap(req);
        })
      }
    },
    async revertRequestData(req) {
      const response = await RequestService.getRequestsByUser(this.username);
      const temp = response.data.find(r => r.requestId === req.requestId);
      req.description = temp.description;
      req.amount = temp.amount;
      req.resourceType = temp.resourceType;
      req.resourceName = temp.resourceName;
      req.latitude = temp.latitude;
      req.longitude = temp.longitude;
    },
    async cancel(req) {
      this.disableEditMode(req);
      await this.revertRequestData(req);
    },
    async updateRequest(username, requestId, description, resourceType, amount, resourceName) {
      const toast = useToast();
      try {
        if (!resourceName.trim()) {
          toast.error(this.$t('req-name-error'));
          return;
        }
        if (amount <= 0 || isNaN(amount)) {
          toast.error(this.$t('req-amount-error'));
          return;
        }

        const req = this.userRequests.request.find(r => r.requestId === requestId);

        let lng = req.longitude;
        let lat = req.latitude;
        const updatedRequest = {
          description,
          longitude: lng,
          latitude: lat,
          resourceType,
          amount,
          resourceName,
        };
        await RequestService.changeRequest(username, updatedRequest, requestId);
        toast.success(this.$t('req-update-succ'));
        if(req.blueCircle) {
          req.mapp.removeLayer(req.redCircle);
          req.redCircle = L.circle([lat, lng], {
            color: 'red',
            fillColor: '#f03',
            fillOpacity: 0.5,
            radius: 75,
          }).addTo(req.mapp);
        }
        const center = req.redCircle.getLatLng();
        req.mapp.setView(center, req.mapp.getZoom());
        this.disableEditMode(req);
      } catch (error) {
        toast.error(this.$t('req-update-fail'));
      }
    },
    enableEditMode(id) {
      if(this.editMode.edit) {
        this.userRequests.request.forEach(r => this.disableEditMode(r));
      }
      this.editMode.edit = true;
      this.editMode.requestId = id;
    },
    disableEditMode(req) {
      if (req.blueCircle) {
        req.clear = true;
        req.longitude = null;
        req.latitude = null;
      }
      this.getMap(req);
      this.editMode.edit = false;
      this.editMode.requestId = null;
    },
    getMap(req) {
      this.$nextTick(() => {
        if(this.initCount < this.userRequests.request.length) {
          this.initCount += 1;
        const mapContainer = document.getElementById(`map-container${req.requestId}`);
        req.mapp = L.map(mapContainer).setView([req.latitude, req.longitude], 14);
        L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
          maxZoom: 19,
          attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        }).addTo(req.mapp);
          req.redCircle = L.circle([req.latitude, req.longitude],
              {
                color: 'red',
                fillColor: '#f03',
                fillOpacity: 0.5,
                radius: 75
              })
              .addTo(toRaw(req.mapp));
          req.mapp.on('click', (e) => {
            if (this.editMode.edit && this.editMode.requestId === req.requestId) {
              const {lat, lng} = e.latlng;
              if (req.blueCircle) {
                req.mapp.removeLayer(req.blueCircle);

              }
              req.blueCircle = L.circle([lat, lng], {
                color: 'blue',
                fillColor: '#03f',
                fillOpacity: 0.5,
                radius: 75,
              }).addTo(toRaw(req.mapp));
              req.latitude = lat;
              req.longitude = lng;
            }
          })
        }

        if (req.clear) {
          req.clear = false;
          req.mapp.removeLayer(req.blueCircle);
          const center = req.redCircle.getLatLng();
          req.mapp.setView(center, req.mapp.getZoom());
          req.blueCircle = null;
        }
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