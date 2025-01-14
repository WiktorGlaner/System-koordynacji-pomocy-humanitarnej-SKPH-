<template>
  <div class="container">
    <h2 class="text-center">Aplikacje do organizacji</h2>
    <table class="table table-striped">
      <thead>
      <tr>
        <th>ID</th>
        <th>Imię</th>
        <th>Nazwisko</th>
        <th>Status</th>
        <th>Akcja</th> <!-- Dodajemy kolumnę na przycisk -->
      </tr>
      </thead>
      <tbody v-for="(application, index) in applications" :key="application.id">
      <tr>
        <td>{{ application.id }}</td>
        <td>{{ application.name }}</td>
        <td>{{ application.surname }}</td>
        <td>
          <!-- Status aplikacji -->
          <span v-if="!nullExists[application.id]" class="badge" :class="approvalStatus[application.id] ? 'badge-success' : 'badge-danger'">
                {{ approvalStatus[application.id] ? 'Zatwierdzona' : 'Odrzucona' }}
          </span>
        </td>
        <td>
          <!-- Przycisk wyświetlający szczegóły -->
          <button @click="toogleForm(application.id)" class="btn btn-info">
            {{ showForm[application.id] ? 'Ukryj szczegóły' : 'Pokaż szczegóły' }}
          </button>
          <button v-if="nullExists[application.id]" @click="acceptApplication(application.id)" class="btn btn-success">
            {{ 'Akceptuj' }}
          </button>
          <button v-if="nullExists[application.id]" @click="rejectApplication(application.id)" class="btn btn-danger">
            {{ 'Odrzuć' }}
          </button>
        </td>
      </tr>
      <tr v-if="this.showForm[application.id]">
        <td>Pesel: {{application.pesel}}</td>
        <td>Email: {{application.email}}</td>
        <td>Nazwa użytkownika: {{application.username}}</td>
      </tr>
      </tbody>
    </table>
    <div v-if="successMessage" class="alert alert-success">
      {{ successMessage }}
    </div>
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
  name: "ApplicationsList",
  data() {
    return {
      applications: [], // Lista aplikacji
      expandedInfo: {}, // Zmienna przechowująca informacje o widoczności szczegółów dla aplikacji
      loading: true, // Flaga ładowania
      error: null,
      successMessage: null,// Obsługa błędów
      showForm: {},
      approvalStatus: {},
      nullExists: {},
    };
  },
  mounted() {
    this.fetchApplications();
  },
  methods: {
    async fetchApplications() {
      const API_URL = "http://localhost:8080/api/user"; // URL backendu
      try {
        const response = await axios.get(`${API_URL}/getApplicationByOrganizationId`, {
          headers: authHeader(),
        });
        this.applications = response.data;
        this.loading = false;
        for (const org of this.applications) {
          this.fetchApprovalStatus(org.id);

        }
      } catch (err) {
        this.error = err.message || "Nieznany błąd";
        this.loading = false;
      }
    },
    toogleForm(applicationId) {
      this.showForm[applicationId] = !this.showForm[applicationId];
    },
    async fetchApprovalStatus(applicationId) {
      const API_URL = "http://localhost:8080/api/user"; // Prawidłowy URL backendu
      try {
        const requestData = {
          id: applicationId,
        };
        const response = await axios.post(
            `${API_URL}/getApprovalStatusById`,
            requestData,
            {
              'Content-Type': 'application/json',
              headers: authHeader(),
            }
        );
        this.nullExists[applicationId] = response.data.nullExists;
        this.approvalStatus[applicationId] = response.data.exists;
      } catch (err) {
        console.error("Błąd pobierania statusu approval:", err.message);
      }
    },
    async acceptApplication(applicationId) {
      const API_URL = "http://localhost:8080/api/user"; // Prawidłowy URL backendu
      try {
        const requestData = {
          id: applicationId,
        };
        const response = await axios.post(
            `${API_URL}/acceptApplication`,
            requestData,
            {
              'Content-Type': 'application/json',
              headers: authHeader(),
            }
        );
        this.successMessage = `Aplikacja o ID: ${applicationId} została zaakceptowana`;
        setTimeout(() => (this.successMessage = null), 3000);
        this.nullExists[applicationId] = false;
        this.approvalStatus[applicationId] = true;
      } catch (err) {
        console.error("Błąd pobierania statusu approval:", err.message);
      }
    },
    async rejectApplication(applicationId) {
      const API_URL = "http://localhost:8080/api/user"; // Prawidłowy URL backendu
      try {
        const requestData = {
          id: applicationId,
        };
        const response = await axios.post(
            `${API_URL}/rejectApplication`,
            requestData,
            {
              'Content-Type': 'application/json',
              headers: authHeader(),
            }
        );
        this.successMessage = `Aplikacja o ID: ${applicationId} została odrzucona`;
        setTimeout(() => (this.successMessage = null), 3000);
        this.nullExists[applicationId] = false;
        this.approvalStatus[applicationId] = false;
      } catch (err) {
        console.error("Błąd pobierania statusu approval:", err.message);
      }
    }
  }
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
  color: black;
  font-size: 1em;
  padding: 0.3em 0.8em;
  margin-left: 30px;
  border-radius: 0.2em;
}

.btn {
  margin-left: 5px;
  margin-right: 5px;
}

.badge-success {
  background-color: #28a745;
}

.badge-danger {
  background-color: #dc3545;
}
</style>
