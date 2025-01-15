<template>
  <div class="container">
    <div v-if="!allowedRole">
      <p style="color: red; text-align: center;">{{ $t('mapInfo') }}</p>
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
      const map = L.map("map").setView([51.75, 19.45], 14);
      L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      }).addTo(map);

      this.loadResources();

      if (this.currentUser.roles.includes("ROLE_ORGANIZATION") || this.currentUser.roles.includes("ROLE_AUTHORITY")) {
        this.loadAllRequests();
      }

      if (this.currentUser.roles.includes("ROLE_VICTIM")) {
        this.loadUserRequests();
      }
    }
  },
  methods: {
    async loadResources() {
      try {
        const response = await fetch("http://localhost:8080/resource");
        if (!response.ok) {
          throw new Error("Błąd podczas ładowania zasobów");
        }
        const resources = await response.json();
        console.log("Resources:", resources);

        resources.forEach((resource) => {
          L.marker([resource.latitude, resource.longitude])
              .addTo(map)
              .bindPopup(`<strong>${resource.name}</strong><br>${resource.description}`);
        });
      } catch (error) {
        console.error("Błąd ładowania zasobów:", error);
      }
    },
    // async loadRequests() {
    //   try {
    //     const response = await fetch(
    //         `http://localhost:8080/api/test/${this.currentUser.username}/requests`
    //     );
    //     if (!response.ok) {
    //       throw new Error("Błąd podczas ładowania próśb");
    //     }
    //
    //     const requests = await response.json();
    //     console.log("Requests:", requests);
    //
    //   } catch (error) {
    //     console.error("Błąd ładowania próśb:", error);
    //   }
    // }
  }
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
