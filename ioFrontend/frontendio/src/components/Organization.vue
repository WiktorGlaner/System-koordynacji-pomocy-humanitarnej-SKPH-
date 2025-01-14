<template>
  <div class="container">
    <h2 class="text-center">Lista Organizacji</h2>
    <table class="table table-striped">
      <thead>
      <tr>
        <th>ID</th>
        <th>Nazwa</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="organization in organizations" :key="organization.id">
        <td>{{ organization.id }}</td>
        <td>{{ organization.name }}</td>
        <td>
          <button
              :disabled="applicationExists[organization.id] === true"
              @click="addApplication(organization.id)"
              class="btn btn-primary"
          >
            {{ applicationExists[organization.id] ? 'Aplikacja już złożona' : 'Złóż aplikację' }}
          </button>
        </td>
      </tr>
      </tbody>
    </table>
    <div v-if="loading" class="text-center">
      <i class="fas fa-spinner fa-spin"></i> Ładowanie danych...
    </div>
    <div v-if="error" class="alert alert-danger">
      Wystąpił błąd podczas ładowania danych: {{ error }}
    </div>
  </div>
</template>

<script>
import axios from "axios";
import authHeader from "@/services/auth-header.js";

export default {
  name: "OrganizationsTable",
  data() {
    return {
      organizations: [], // Lista organizacji
      loading: true, // Flaga ładowania
      error: null, // Obsługa błędów
      successMessage: null, // Obsługa komunikatów sukcesu
      applicationExists: {}, // Obiekt śledzący stan aplikacji dla każdej organizacji
    };
  },
  mounted() {
    this.fetchOrganizations();
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  methods: {
    async checkIfApplicationExists(organizationId) {
      const API_URL = "http://localhost:8080/api/user"; // Prawidłowy URL backendu
      try {
        const requestData = {
          id: organizationId // Wartość organizationId przesyłana jako JSON
        };
        const response = await axios.post(
            `${API_URL}/checkApplicationExists`,
            requestData,
            {
              'Content-Type': 'application/json',
              headers: authHeader(),
            }
        );
        this.applicationExists[organizationId] = response.data.exists; // Ustawia stan dla danej organizacji
      } catch (err) {
        console.error("Błąd sprawdzania aplikacji:", err.message);
      }
    },
    async fetchOrganizations() {
      const API_URL = "http://localhost:8080/api/user"; // Prawidłowy URL backendu
      try {
        const response = await axios.get(`${API_URL}/allOrganizations`, {
          headers: authHeader(),
        });
        this.organizations = response.data; // Przypisz dane do listy organizacji
        this.loading = false;

        // Sprawdzamy aplikację dla każdej organizacji
        this.organizations.forEach(org => {
          this.checkIfApplicationExists(org.id);
        });
      } catch (err) {
        this.error = err.message || "Nieznany błąd";
        this.loading = false;
      }
    },
    async addApplication(organizationId) {
      const API_URL = "http://localhost:8080/api/user"; // Prawidłowy URL backendu
      try {
        const requestData = {
          id: organizationId // Wartość organizationId przesyłana jako JSON
        };
        const response = await axios.post(
            `${API_URL}/makeApplication`,
            requestData,
            {
              'Content-Type': 'application/json',
              headers: authHeader(),
            }
        );
        this.successMessage = `Aplikacja została dodana do organizacji o ID: ${organizationId}`;
        setTimeout(() => (this.successMessage = null), 3000); // Ukryj komunikat po 3 sekundach

        // Po dodaniu aplikacji zaktualizuj stan
        this.applicationExists[organizationId] = true;
      } catch (err) {
        this.error = err.response?.data || "Wystąpił błąd podczas dodawania aplikacji";
        setTimeout(() => (this.error = null), 3000); // Ukryj komunikat błędu po 3 sekundach
      }
    },
  },
};
</script>

<style scoped>
.container {
  margin-top: 20px;
}

.table {
  margin-top: 20px;
}

.text-center {
  text-align: center;
}
</style>
