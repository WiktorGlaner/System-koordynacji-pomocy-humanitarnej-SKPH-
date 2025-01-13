<template>
  <div class="container">
    <div v-if="!allowedRole">
      <p style="color: red; text-align: center;">{{ $t('map-Info') }}</p>
    </div>
    <div class="content" v-else>
      <div class="table-container">
        <h3>{{ $t('map-resources') }}</h3>
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
            <td><button @click="centerMap(resource.location.latitude, resource.location.longitude)">
              {{ $t('map-center') }}
            </button></td>
          </tr>
          </tbody>
        </table>

        <h3>{{ $t('map-requests') }}</h3>
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
            <td>{{ request.reporter.user.username }}</td>
            <td>{{ request.resourceName }}</td>
            <td><button @click="centerMap(request.latitude, request.longitude)">
              {{ $t('map-center') }}
            </button></td>
          </tr>
          </tbody>
        </table>
      </div>
      <div id="map"></div>
    </div>

    <div v-if="message" class="alert centered-alert" :class="successful ? 'alert-success' : 'alert-danger'" role="alert">
      {{ message }}
    </div>
  </div>
</template>


<script>
import isEqual from "lodash/isEqual";

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
            .openOn(this.map);

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
              .addTo(this.map)
              .bindPopup(`<strong>${request.reporter.user.username}</strong><br>${request.amount} ${request.resourceName}`);
        });
      }

      if(!isEqual(this.newResourcePoints, this.resourcePoints)){
        this.resourcePoints = this.newResourcePoints
        console.log(this.resourcePoints)
        this.resourcePoints.forEach((resource) => {
          L.marker([resource.location.latitude, resource.location.longitude])
              .addTo(this.map)
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
  width: 50vw;
  margin: 5vh auto;
}
.container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}
.content {
  display: flex;
  flex-direction: row;
  gap: 1rem;
}
.table-container {
  width: 40%;
  margin: 5vh auto;;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 2rem;
}
table, th, td {
  border: 1px solid #ddd;
  width: 100%;
}
th, td {
  padding: 8px;
  text-align: left;
  display:table-cell;
  width:100px;
}
h3 {
  margin: 0 0 1rem 0;
}
.centered-alert {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: 15px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 5px;
  text-align: center;
  z-index: 9999;
  width: 200px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>

