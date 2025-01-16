<template>
  <div class="container my-4">
    <div v-if="roles.includes('ROLE_ADMIN')">
      <div class="carddiv">
        <div v-for="(req, index) in requests" :key="index" :class="`req${req.requestId}`">
          <div class="card h-100 mx-5">
              <ul class="list-group list-group-flush">
                <li class="list-group-item">
                  <div class="delete">
                    <input class="btn btn-outline-dark" type="button" value="X"
                           @click="deleteRequest(req.reporter.user.username, req.requestId)"/>
                   </div>
                </li>
                <li class="list-group-item">
                  <strong>ID:</strong> {{ req.requestId }}
                </li>
                <li class="list-group-item">
                  <strong>Opis:</strong> {{ req.description }}
                </li>
                <li class="list-group-item">
                  <strong>Typ:</strong> {{ types[req.resourceType] }}
                </li>
                <li class="list-group-item">
                  <strong>Nazwa:</strong> {{ req.resourceName }}
                </li>
                <li class="list-group-item">
                <strong>Status:</strong> {{ statuses[req.status] }}
                </li>
                <li class="list-group-item">
                  <strong>Location:</strong> Szerokość: {{ req.latitude }}, Długość: {{ req.longitude }}
                </li>
                <li class="list-group-item">
                <strong>Reporter:</strong>
                </li>
                <ul class="mb-2">
                  <li>
                    <strong>Imię:</strong> {{ req.reporter ? req.reporter.name : 'Brak' }}
                  </li>
                  <li>
                  <strong>Nazwisko:</strong> {{ req.reporter ? req.reporter.surname : 'Brak' }}
                  </li>
                  <li>
                  <strong>Email:</strong> {{ req.reporter ? req.reporter.user.email : 'Brak' }}
                  </li>
                </ul>
              </ul>
            </div>
          </div>
        </div>
      </div>
    <div v-else-if="roles.includes('ROLE_VICTIM')">
      <div class="activity">
        <button type="submit" class="btn btn-primary" @click="setMode(1), getMapDefault()">Zgłoś potrzebę</button>
        <button type="submit" class="btn btn-primary" @click="setMode(2), getRequestByUser(username)">Wyświetl listę zgłoszeń</button>
      </div>
      <div class="mode1" v-if="mode === 1">
        <form @submit.prevent="submitRequest">
          <div class="mb-3">
            <label for="resourceType" class="form-label">Typ:</label>
            <select v-model="newRequest.resourceType" id="resourceType" class="form-select" required>
              <option v-for="(type, index) in resourceTypes" :key="index" :value="type">
                {{ type }}
              </option>
            </select>
          </div>

          <div class="mb-3">
            <label for="resourceName" class="form-label">Nazwa:</label>
            <input v-model="newRequest.resourceName" id="resourceName" type="text" class="form-control" required />
          </div>

          <div class="mb-3">
            <label for="amount" class="form-label">Liczba:</label>
            <input v-model.number="newRequest.amount" id="amount" type="number" class="form-control" required />
          </div>

          <div class="mb-3">
            <label for="description" class="form-label">Opis:</label>
            <textarea v-model="newRequest.description" id="description" class="form-control" rows="3" required></textarea>
          </div>
          <div class="mb-3">
            <label for="location" class="form-label">Lokacja:</label>
            <div :id="`map-container`"></div>
          </div>

          <button type="submit" class="btn btn-primary" @click="addRequest(username, newRequest)">Wyślij</button>
        </form>
      </div>
      <div class="mode2" v-else-if="mode === 2">
        <div class="carddiv">
          <div v-for="(req, index) in userRequests" :key="index" :class="`req${req.requestId}`">
            <div class="card h-100 mx-5">
              <ul class="list-group list-group-flush">
                <li class="list-group-item">
                  <div class="edit">
                    <button
                        class="btn btn-outline-dark"
                        type="button"
                        @click="deleteUserRequest(req.reporter.user.username, req.requestId)">
                      <font-awesome-icon icon="remove"/>
                    </button>
                    <button
                        class="btn btn-outline-dark"
                        type="submit"
                        @click="getMap(req)">
                      <font-awesome-icon icon="map" />
                    </button>
                    <button
                        class="btn btn-outline-dark"
                        type="button"
                        @click="enableEditMode(req.requestId)">
                      <font-awesome-icon icon="edit"/>
                    </button>
                    <button
                        v-if="editMode.edit && editMode.requestId === req.requestId"
                        class="btn btn-outline-dark"
                        @click="updateRequest(req.username, req.requestId, req.description, req.resourceType, req.amount, req.resourceName)">
                      <font-awesome-icon icon="check" />
                    </button>
                    <button
                        v-if="editMode.edit && editMode.requestId === req.requestId"
                        class="btn btn-outline-dark"
                        @click="disableEditMode(req.requestId)">
                      <font-awesome-icon icon="cancel" />
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
                      <label for="descId" class="me-2"><strong>Opis:</strong></label>
                      <input v-model="req.description" type="text" class="form-control form-control-sm" id="descId{{ req.requestId }}" />
                    </div>
                  </li>
                    <li class="list-group-item">
                    <div class="form-group d-flex align-items-center">
                      <label for="descId" class="me-2"><strong>Liczba:</strong></label>
                      <input v-model="req.amount" type="number" class="form-control form-control-sm" id="amountId{{ req.requestId }}" />
                    </div>
                  </li>
                    <li class="list-group-item">
                      <div class="form-group d-flex">
                        <label for="resourceType" class="me-2"><strong>Typ:</strong></label>
                        <select v-model="req.resourceType" id="resourceTypeId{{ req.requestId }}" class="form-select form-select-sm" required>
                          <option v-for="(type, index) in resourceTypes" :key="index" :value="type">
                            {{ type }}
                          </option>
                        </select>
                      </div>
                    </li>
                    <li class="list-group-item">
                      <div class="form-group d-flex align-items-center">
                        <label for="nameId{{ req.requestId }}" class="me-2"><strong>Nazwa:</strong></label>
                        <input v-model="req.resourceName" type="text" class="form-control form-control-sm" id="nameId" />
                      </div>
                    </li>
                  </form>
                </div>
                <div v-else>
                  <li class="list-group-item">
                    <strong>Opis:</strong> {{ req.description }}
                  </li>
                  <li class="list-group-item">
                    <strong>Liczba:</strong> {{ req.amount }}
                  </li>
                  <li class="list-group-item">
                    <strong>Typ:</strong> {{ types[req.resourceType] }}
                  </li>
                  <li class="list-group-item">
                    <strong>Nazwa:</strong> {{ req.resourceName }}
                  </li>
                </div>
                  <li class="list-group-item">
                    <strong>Status:</strong> {{ statuses[req.status] }}
                  </li>
                  <li class="list-group-item">
                    <strong>Reporter:</strong>
                    <ul class="">
                      <li>
                        <strong>Imię:</strong> {{ req.reporter ? req.reporter.name : 'Brak' }}
                      </li>
                      <li>
                        <strong>Nazwisko:</strong> {{ req.reporter ? req.reporter.surname : 'Brak' }}
                      </li>
                      <li>
                        <strong>Email:</strong> {{ req.reporter ? req.reporter.user.email : 'Brak' }}
                      </li>
                    </ul>
                  </li>
                  <li class="list-group-item">
                      <div :id="`map-container${req.requestId}`"></div>
                  </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

    </div>
    <div v-else>
      <h1 style="display: flex; justify-content: center; margin-top: 100px;">You are not allowed to use requests</h1>
    </div>
  </div>
</template>

<script>
import RequestService from "@/services/request.service.js";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {toRaw} from "vue";

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
      mode: null,
      userRequests: [],
      editMode: {
        edit: null,
        requestId: null,
      },
      isMap: [
        {
          shown: false,
          requestId: null,
        }
      ],
      changeLoc: false,
      newCords: {
        lat: null,
        lng: null,
      },
      blueCircle: null,
      redCircle: null,
    };
  },
  created() {
    this.fetchRequests();
    this.checkRole();
    this.getUser();
    this.getResourceTypes();
    this.getRequestByUser(this.username);
  },
  methods: {
    async fetchRequests() {
      const response = await RequestService.getAllRequests();
      this.requests = response.data;
    },
    checkRole() {
      const userData = localStorage.getItem('user');
      const user = JSON.parse(userData);
      this.roles = user.roles;
    },
    getUser() {
      const userData = localStorage.getItem('user');
      const user= JSON.parse(userData);
      this.username = user.username;
    },
    async deleteRequest(username, id) {
      await RequestService.deleteRequest(username, id);
      const response = await RequestService.getAllRequests();
      this.requests = response.data;
    },
    async deleteUserRequest(username, id) {
      await RequestService.deleteRequest(username, id);
      const response = await RequestService.getRequestsByUser(username);
      this.userRequests = response.data;
    },
    async getResourceTypes() {
      const response = await RequestService.getRequestTypes();
      this.resourceTypes = response.data;
    },
    async addRequest(username, newRequestData) {
      try {
        newRequestData.latitude = this.newCords.lat;
        newRequestData.longitude = this.newCords.lng;
        this.newCords.lat = null;
        this.newCords.lng = null;
        const response = await RequestService.addRequest(username, newRequestData);
        alert("Request successfully added: " + response.data.requestId);
      } catch (error) {
        console.error("Error adding request:", error);
        alert("Failed to add request: " + error.response?.data || "Unknown error");
      }
    },
    setMode(mode) {
      this.mode = mode;
    },
    async getRequestByUser(username) {
      const response = await RequestService.getRequestsByUser(username);
      this.userRequests = response.data.sort((a, b) => a.requestId - b.requestId);
    },
    async updateRequest(username, requestId, description, resourceType, amount, resourceName) {
      try {
        const req = this.requests.find(r => r.requestId === requestId);
        let lng = req.longitude;
        let lat = req.latitude;
        if(this.newCords.lng !== null && this.newCords.lat !== null) {
          lng = this.newCords.lng;
          lat = this.newCords.lat;
        }
        const updatedRequest = {
          description,
          longitude: lng,
          latitude: lat,
          resourceType,
          amount,
          resourceName,
        };
        await RequestService.changeRequest(username, updatedRequest, requestId);
        alert("Request updated successfully");
        req.longitude = lng;
        req.latitude = lat;

        // Usuń niebieskie koło
        if (this.blueCircle) {
          this.map.removeLayer(this.blueCircle);
          this.blueCircle = null;
        }

        // Usuń stare czerwone koło (jeśli istnieje)
        if (this.redCircle) {
          this.map.removeLayer(this.redCircle);
        }

        // Dodaj nowe czerwone koło w zaktualizowanej lokalizacji
        req.redCircle = L.circle([lat, lng], {
          color: 'red',
          fillColor: '#f03',
          fillOpacity: 0.5,
          radius: 75,
        }).addTo(this.map);

        // Resetuj nowe współrzędne i tryb edycji
        this.newCords.lng = null;
        this.newCords.lat = null;
        this.disableEditMode(requestId);

        // Upewnij się, że mapa nie zostanie ukryta
        const mapContainer = document.getElementById(`map-container${requestId}`);
        if (mapContainer) {
          mapContainer.style.height = '200px';
          mapContainer.style.width = '100%';
        }
      } catch (error) {
        console.error("Error updating request:", error);
        alert("Failed to update request: " + error.response?.data || "Unknown error");
      }
    },
    enableEditMode(id) {
        this.editMode.edit = true;
        this.editMode.requestId = id;
    },
    disableEditMode(id) {
      if (this.blueCircle) {
        this.map.removeLayer(this.blueCircle);
        this.blueCircle = null;
      }
      this.editMode.edit = false;
      this.editMode.requestId = null;
    },
    async getMapDefault() {
      await this.$nextTick();
      const mapContainer = document.getElementById(`map-container`);
      mapContainer.style.height = '500px';
      mapContainer.style.width = '100%';

      this.map = L.map(mapContainer).setView([51.75, 19.45], 14);

      L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }).addTo(this.map);

      this.map.on('click', (e) => {
        const { lat, lng } = e.latlng;
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
    getMap(req) {
      if (!this.isMap.find(entry => entry.requestId === req.requestId)) {
        this.isMap.push({
          shown: false,
          requestId: req.requestId,
        });
      }
      const mapContainer = document.getElementById(`map-container${req.requestId}`);
      const entry = this.isMap.find(entry => entry.requestId === req.requestId);

      if (!entry.shown) {
        entry.shown = true;
        mapContainer.style.height = '200px';
        mapContainer.style.width = '100%';
        this.map = L.map(mapContainer).setView([req.latitude, req.longitude], 14);

        L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
          maxZoom: 19,
          attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        }).addTo(this.map);

        this.redCircle = L.circle([req.latitude, req.longitude],
            {
              color: 'red',
              fillColor: '#f03',
              fillOpacity: 0.5,
              radius: 75
            })
            .addTo(toRaw(this.map));

        this.map.on('click', (e) => {
          if(this.editMode.edit && this.editMode.requestId === req.requestId) {
            const { lat, lng } = e.latlng;
            this.changeLoc = true;
            if (this.blueCircle) {
              this.map.removeLayer(this.blueCircle);
            }
            this.blue = L.circle([lat, lng], {
              color: 'blue',
              fillColor: '#03f',
              fillOpacity: 0.5,
              radius: 75,
            }).addTo(this.map);
            this.newCords.lat = lat;
            this.newCords.lng = lng;
          }
        })
      } else {
        entry.shown = false;
        mapContainer.style.height = '0px';
      }
    },
  }
};
</script>

<style scoped>
  .delete {
    display: flex;
    text-align: center;
    align-items: center;
    justify-content: right;
    gap: 10px;
  }
  .delete > input {
    font-size: 12px;
    padding: 6px 12px;
  }

  .edit {
    display: flex;
    text-align: center;
    align-items: center;
    justify-content: right;
    flex-direction: row-reverse;
    gap: 10px;
  }
  .edit > input {
  }

  .carddiv {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    width: 100%;
  }

  .activity {
    padding: 30px;
    display: flex;
    gap: 20px;
  }
</style>
