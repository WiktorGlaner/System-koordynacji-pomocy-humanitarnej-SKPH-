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
      <tr v-for="organization in organizations" :key="organization.id" >
        <td>{{ organization.id }}</td>
        <td>{{ organization.name }}</td>
        <td>
          <div v-if="applicationExists[organization.id]">
            <button
                @click="removeApplication(organization.id)"
                class="btn btn-danger"
            >
              Usuń aplikację
            </button>
            <!-- Wyświetlanie statusu dla złożonych aplikacji -->
            <span v-if="!nullExists[organization.id]" class="badge" :class="approvalStatus[organization.id] ? 'badge-success' : 'badge-danger'">
                {{ approvalStatus[organization.id] ? 'Zatwierdzona' : 'Odrzucona' }}
            </span>
          </div>
          <div v-else>
            <!-- Przycisk składania aplikacji -->
            <button
                @click="addApplication(organization.id)"
                class="btn btn-primary"
            >
              Złóż aplikację
            </button>
          </div>
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
      approvalStatus: {},
      nullExists: {},
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
        this.approvalStatus = {}; // Czyści approvalStatus
        this.nullExists = {};

        // Sprawdzamy aplikację dla każdej organizacji
        for (const org of this.organizations) {
          await this.checkIfApplicationExists(org.id);
          if (this.applicationExists[org.id]) {
            this.fetchApprovalStatus(org.id);
          }
        }
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
        for (const org of this.organizations) {
          if (this.applicationExists[org.id]) {
            this.fetchApprovalStatus(org.id);
          }
        }
      } catch (err) {
        this.error = err.response?.data || "Wystąpił błąd podczas dodawania aplikacji";
        setTimeout(() => (this.error = null), 3000); // Ukryj komunikat błędu po 3 sekundach
      }
    },
    async removeApplication(organizationId) {
      const API_URL = "http://localhost:8080/api/user";
      try {
        const requestData = {
          id: organizationId // Wartość organizationId przesyłana jako JSON
        };
        const response = await axios.post(
            `${API_URL}/deleteApplication`,
            requestData,
            {
              'Content-Type': 'application/json',
              headers: authHeader(),
            }
        );
        this.successMessage = `Aplikacja została usunięta z organizacji o ID: ${organizationId}`;
        setTimeout(() => (this.successMessage = null), 3000);

        this.applicationExists[organizationId] = false;
        for (const org of this.organizations) {
          await this.checkIfApplicationExists(org.id);
          if (this.applicationExists[org.id]) {
            this.fetchApprovalStatus(org.id);
          }
        }
        // this.approvalStatus[organizationId] = null; // Czyści approvalStatus
        // this.nullExists[organizationId] = null;
      } catch (err) {
        this.error = err.response?.data || "Wystąpił błąd podczas usuwania aplikacji";
        setTimeout(() => (this.error = null), 3000);
      }
    },
    async fetchApprovalStatus(organizationId) {
      const API_URL = "http://localhost:8080/api/user"; // Prawidłowy URL backendu
      try {
        const requestData = {
          id: organizationId,
        };
        const response = await axios.post(
            `${API_URL}/getApprovalStatus`,
            requestData,
            {
              'Content-Type': 'application/json',
              headers: authHeader(),
            }
        );
        this.nullExists[organizationId] = response.data.nullExists;
        this.approvalStatus[organizationId] = response.data.exists;
      } catch (err) {
        console.error("Błąd pobierania statusu approval:", err.message);
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

.badge {
  color: black; /* Kolor tekstu na czarny */
  font-size: 1em; /* Powiększenie tekstu */
  padding: 0.3em 0.8em;
  margin-left: 15px;
  border-radius: 0.2em;
}

.badge-success {
  background-color: #28a745; /* Kolor zielony dla zatwierdzonej aplikacji */
}

.badge-danger {
  background-color: #dc3545; /* Kolor czerwony dla odrzuconej aplikacji */
}

.button-container {
  display: inline-block; /* Wyrównanie przycisków w jednej linii */
  white-space: nowrap; /* Zapewnia, że elementy nie będą się zawijały */
}

.btn-danger {
  margin-left: 250px;
}

.btn-primary{
  margin-left: 250px;
}
</style>



