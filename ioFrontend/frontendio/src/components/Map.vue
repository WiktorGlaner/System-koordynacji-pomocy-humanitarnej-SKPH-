<template>
   <div class="container">
    <div v-if="!allowedRole">
      <p style="color: red; text-align: center;">{{ $t('map-Info') }}</p>
    </div>
    <div class="content" v-else>
      <div class="table-container">
        <h3>{{ $t('map-resources') }}</h3>
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>{{ $t('map-1table-name') }}</th>
                <th>{{ $t('map-1table-type') }}</th>
                <th>{{ $t('map-1table-coordinates') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(resource, index) in resourcePoints" :key="index">
                <td>{{ resource.name }}</td>
                <td>{{ resource.quantity }}</td>
                <td>
                  <button @click="centerMap(resource.location.latitude, resource.location.longitude)">
                    {{ $t('map-center') }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <h3>{{ $t('map-requests') }}</h3>
        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>{{ $t('map-2table-name') }}</th>
                <th>{{ $t('map-2table-type') }}</th>
                <th>{{ $t('map-2table-coordinates') }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(request, index) in requestPoints" :key="index">
                <td>{{request.reporter.user.username }}</td>
                <td>{{ request.resourceName }}</td>
                <td>
                  <button @click="centerMap(request.latitude, request.longitude)">
                    {{ $t('map-center') }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="map-container">
        <div id="map"></div>
      </div>
    </div>
  </div>
</template>


<script>
import isEqual from "lodash/isEqual";
import {toRaw} from "vue";

export default {
  name: "Map",
  data() {
    return {
      allowedRoles: ["ROLE_ORGANIZATION", "ROLE_AUTHORITY", "ROLE_VICTIM"],
      map: "",
      refreshInterval: 1000,
      message: "",
      successful: false,
      resourcePoints: [],
      requestPoints: [],
      newResourcePoints: [],
      newRequestPoints: []
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    allowedRole() {
      // Sprawdzamy, czy rola użytkownika znajduje się w dozwolonych rolach
      return this.allowedRoles.some(role => this.currentUser.roles.includes(role));
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push("/login");
      return;
    }

    if (this.allowedRole) {
      // Inicjalizacja mapy, tylko jeśli użytkownik ma dozwoloną rolę
      this.map = L.map("map").setView([51.75, 19.45], 14);
      L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      }).addTo(this.map);

      if (this.currentUser.roles.includes("ROLE_ORGANIZATION")) {
        this.loadOrganisationResources();
        this.loadAllRequests();
      }

      if (this.currentUser.roles.includes("ROLE_AUTHORITY")) {
        this.loadAllResources();
        this.loadAllRequests();
      }

      if (this.currentUser.roles.includes("ROLE_VICTIM")) {
        this.loadAllResources();
        this.loadVictimsRequests();
        this.enableRequestCreation();
      }

      this.refreshInterval = setInterval(() => {
        this.loadUpdates();
      }, 1000);
    }
  },
  methods: {
    async loadOrganisationResources() {
      try {
        const response = await fetch("http://localhost:8080/resource?status=unavailable&status=available&organisationId=1");
        if (!response.ok) {
          throw new Error("Błąd podczas ładowania zasobów");
        }
        const resources = await response.json();
        //console.log("Resources:", resources);

        resources.forEach((resource) => {
          this.newResourcePoints.push(resource);
        });
      } catch (error) {
        console.error("Błąd ładowania zasobów:", error);
      }
    },
    async loadAllRequests() {
      try {
        const response = await fetch(`http://localhost:8080/api/request/requests`);
        if (!response.ok) {
          throw new Error("Błąd podczas ładowania próśb");
        }

        const requests = await response.json();
        //console.log("Requests:", requests);

        requests.forEach((request) => {
          this.newRequestPoints.push(request);
        });
      } catch (error) {
        console.error("Błąd ładowania próśb:", error);
      }
    },
    async loadVictimsRequests() {
      try {
        const response = await fetch(`http://localhost:8080/api/request/${this.currentUser.username}/requests`);
        if (!response.ok) {
          throw new Error("Błąd podczas ładowania próśb");
        }

        const requests = await response.json();
        //console.log("Requests:", requests);

        requests.forEach((request) => {
          this.newRequestPoints.push(request);
        });
      } catch (error) {
        console.error("Błąd ładowania próśb:", error);
      }
    },
    async loadAllResources() {
      try {
        const response = await fetch("http://localhost:8080/resource?&status=available");
        if (!response.ok) {
          throw new Error("Błąd podczas ładowania zasobów");
        }
        const resources = await response.json();
        //console.log("Resources:", resources);

        resources.forEach((resource) => {
          this.newResourcePoints.push(resource);
        });
      } catch (error) {
        console.error("Błąd ładowania zasobów:", error);
      }
    },
    enableRequestCreation() {
      this.map.on("click", (e) => {
        const { lat, lng } = e.latlng;

        const popupContent = `
          <form id="request-form">
            <label for="description">${this.$t('map-form-desc')}</label><br>
            <textarea id="description" name="description"></textarea><br>
            <label for="resourceType">${this.$t('map-form-rt')}</label><br>
            <select id="resourceType" name="resourceType">
            <option value="FOOD">${this.$t('map-form-food')}</option>
            <option value="FINANCIAL">${this.$t('map-form-financial')}</option>
            <option value="EQUIPMENT">${this.$t('map-form-eq')}</option>
            <option value="HOUSING">${this.$t('map-form-housing')}</option>
            <option value="TRANSPORT">${this.$t('map-form-transport')}</option>
            <option value="CLOTHING">${this.$t('map-form-clothing')}</option>
            <option value="MEDICAL">${this.$t('map-form-medical')}</option>
            <option value="OTHER">${this.$t('map-form-other')}</option>
            </select><br>
            <label for="resourceName">${this.$t('map-form-rn')}</label><br>
            <input type="text" id="resourceName" name="resourceName"><br>
            <label for="amount">${this.$t('map-form-am')}</label><br>
            <input type="number" min="1" id="amount" name="amount"><br>
            <button type="button" id="submit-request">${this.$t('map-form-button')}</button>
          </form>
        `;

        const popup = L.popup()
            .setLatLng([lat, lng])
            .setContent(popupContent)
            .openOn(toRaw(this.map));

        setTimeout(() => {
          document.getElementById("submit-request").addEventListener("click", async () => {
            const description = document.getElementById("description").value;
            const resourceType = document.getElementById("resourceType").value;
            const resourceName = document.getElementById("resourceName").value;
            const amount = document.getElementById("amount").value;

            if (description && resourceType && resourceName && amount) {
              try {
                const response = await fetch(`http://localhost:8080/api/request/${this.currentUser.username}/requests/addrequest`, {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/json",
                  },
                  body: JSON.stringify({
                    description,
                    latitude: lat,
                    longitude: lng,
                    resourceType,
                    resourceName,
                    amount: parseInt(amount, 10),
                    status: "REGISTERED",
                    reporterId: this.$store.state.auth.user.id,
                  }),
                });

                if (!response.ok) {
                  throw new Error("Błąd podczas tworzenia prośby.");
                }

                this.message = `${this.$t('map-form-success')}`;
                this.successful = true;

                setTimeout(() => {
                  this.message = "";
                  this.successful = false;
                }, 5000);

                this.map.closePopup();
              } catch (error) {
                console.error("Błąd podczas tworzenia prośby:", error);
              }
            } else {
              alert("Proszę uzupełnić wszystkie pola.");
            }
          });
        }, 0);
      });
    },
    async loadUpdates() {
      this.newRequestPoints = []
      this.newResourcePoints = []

      if (this.currentUser.roles.includes("ROLE_ORGANIZATION")) {
        await this.loadOrganisationResources();
        await this.loadAllRequests();
      }

      if (this.currentUser.roles.includes("ROLE_AUTHORITY")) {
        await this.loadAllResources();
        await this.loadAllRequests();
      }

      if (this.currentUser.roles.includes("ROLE_VICTIM")) {
        await this.loadAllResources();
        await this.loadVictimsRequests();
      }

      if(!isEqual(this.newRequestPoints, this.requestPoints)){
        this.requestPoints = this.newRequestPoints
        console.log(this.requestPoints)
        this.requestPoints.forEach((request) => {
          L.circle([request.latitude, request.longitude],
              {
                color: 'red',
                fillColor: '#f03',
                fillOpacity: 0.5,
                radius: 75
              })
              .addTo(toRaw(this.map))
              .bindPopup(`<strong>${request.reporter.user.username}</strong><br>${request.amount} ${request.resourceName}`);
        });
      }

      if(!isEqual(this.newResourcePoints, this.resourcePoints)){
        this.resourcePoints = this.newResourcePoints
        console.log(this.resourcePoints)

        this.resourcePoints.forEach((resource) => {
          L.marker([resource.location.latitude, resource.location.longitude])
              .addTo(toRaw(this.map))
              .bindPopup(`<strong>${resource.name}</strong><br>${resource.quantity}`);
        });
      }

    },
    centerMap(latitude, longitude) {
      if (this.map) {
        this.map.setView([latitude, longitude], 14);
      }
    },
    beforeRouteLeave(to, from, next) {
      if (this.refreshInterval) {
        clearInterval(this.refreshInterval);
      }
      next();
    }
  },
  watch: {
    "$i18n.locale": function () {
      if (this.map) {
        this.map.closePopup();
      }
    },
  },
};

</script>

<style scoped>
#map {
  height: 80vh;
  width: 100%;
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.content {
  display: flex;
  width: 100%;
  height: 80vh; /* Ensure the content height matches the map height */
}

.table-container {
  flex: 1;
  margin-right: 20px;
  display: flex;
  flex-direction: column;
}

.table-wrapper {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
}

.map-container {
  flex: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #ddd;
  padding: 4px; /* Reduced padding for smaller row height */
}

th {
  background-color: #f2f2f2;
  text-align: left;
}

tbody tr:nth-child(odd) {
  background-color: #f9f9f9;
}

tbody tr:hover {
  background-color: #f1f1f1;
}

button {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 5px 10px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 14px;
  margin: 4px 2px;
  cursor: pointer;
  border-radius: 4px;
}

button:hover {
  background-color: #45a049;
}
</style>

