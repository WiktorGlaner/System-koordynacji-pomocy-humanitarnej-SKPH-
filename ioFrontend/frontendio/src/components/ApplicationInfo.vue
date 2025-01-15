<template>
  <div class="container-fluid d-flex justify-content-center align-items-center  mt-4">
    <div class="card h-100" style="width: 100%; padding: 20px;">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
          <h1 class="card-title mx-auto">{{  $t('profile-details') }}</h1>
          <button
              class="btn btn-secondary"
              @click="goBack"
              aria-label="Back"
          >
            {{  $t('profile-back') }}
          </button>
        </div>
        <div class="container mt-5">
          <div class="mb-3">
            <label class="form-label">{{  $t('profile-name') }}</label>
            <input
                type="text"
                class="form-control"
                v-model="volunteers.name"
                readonly
            >
          </div>
          <div  class="mb-3">
            <label class="form-label">{{  $t('profile-surname') }}</label>
            <input
                type="text"
                class="form-control"
                v-model="volunteers.surname"
                readonly
            >
          </div>

          <div class="mb-3">
            <label class="form-label">Pesel</label>
            <input
                type="text"
                class="form-control"
                v-model="volunteers.pesel"
                readonly
            >
          </div>
          <div  class="mb-3">
            <label class="form-label">Email</label>
            <input
                type="text"
                class="form-control"
                v-model="volunteers.email"
                readonly
            >
          </div>
          <div  class="mb-3">
            <label class="form-label">{{  $t('profile-username') }}</label>
            <input
                type="text"
                class="form-control"
                v-model="volunteers.username"
                readonly
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import authHeader from "@/services/auth-header.js";

export default {
  props: ['id'],
  name: "VolunteerInfo",
  data() {
    return {
      volunteers: null, // Lista aplikacji
      expandedInfo: {}, // Zmienna przechowująca informacje o widoczności szczegółów dla aplikacji
      loading: true, // Flaga ładowania
      error: null,
      successMessage: null,// Obsługa błędów
    };
  },
  created() {
    this.fetchVolunteers();
  },
  methods: {
    async fetchVolunteers() {
      const API_URL = "http://localhost:8080/api/user"; // URL backendu
      try {
        const response = await axios.get(`${API_URL}/volunteersByOrganizationId`, {
          params: { id: this.id },
          headers: authHeader(),
        });
        this.volunteers = response.data;
        this.loading = false;
      } catch (err) {
        this.error = err.message || $t('organization-error4');
        this.loading = false;
      }
    },
    goBack() {
      this.$router.go(-1);
    }
  },
  watch: {
    '$route.params.id': 'fetchVolunteers',
  },
};
</script>

<style scoped>
.card {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}
.card-title {
  font-size: 24px;
  margin-bottom: 20px;
}
.list-group-item {
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 15px;
}
</style>
