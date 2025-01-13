<template>
  <div class="container">
    <div v-if="!allowedRole">
      <p style="color: red; text-align: center;">{{ $t('map-Info') }}</p>
    </div>
    <div v-else id="map"></div>
  </div>
</template>

<script>
export default {
  name: "Map",
  data() {
    return {
      allowedRoles: ["ROLE_ORGANIZATION", "ROLE_AUTHORITY", "ROLE_VICTIM"],
      map: "",
      refreshInterval: 1000
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

      this.refreshInterval = setInterval(() => {
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
      }
      }, 1000);

      if (this.currentUser.roles.includes("ROLE_VICTIM")) {
        this.enableRequestCreation(); // Włączanie możliwości dodawania żądań
      }
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
        console.log("Resources:", resources);

        resources.forEach((resource) => {
          L.marker([resource.location.latitude, resource.location.longitude])
              .addTo(this.map)
              .bindPopup(`<strong>${resource.name}</strong><br>${resource.quantity}`);
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
        console.log("Requests:", requests);

        requests.forEach((request) => {
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
        console.log("Requests:", requests);

        requests.forEach((request) => {
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
        console.log("Resources:", resources);

        resources.forEach((resource) => {
          L.marker([resource.location.latitude, resource.location.longitude])
              .addTo(this.map)
              .bindPopup(`<strong>${resource.name}</strong><br>${resource.quantity}`);
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
            <input type="text" id="resourceType" name="resourceType"><br>
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

                alert("Prośba została stworzona!");
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
  align-items: center;
}
</style>
