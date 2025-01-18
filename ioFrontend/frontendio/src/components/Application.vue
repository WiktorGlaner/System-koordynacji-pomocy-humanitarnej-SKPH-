<template>
  <div class="container">
    <h2 class="text-center">{{ $t('application-title') }}</h2>
    <table class="table table-striped">
      <thead>
      <tr>
        <th>ID</th>
        <th>{{ $t('profile-name') }}</th>
        <th>{{ $t('profile-surname') }}</th>
        <th>{{ $t('application-status') }}</th>
        <th>{{ $t('application-action') }}</th> <!-- Dodajemy kolumnę na przycisk -->
      </tr>
      </thead>
      <tbody v-for="(application) in applications" :key="application.id">
      <tr>
        <td>{{ application.id }}</td>
        <td>{{ application.name }}</td>
        <td>{{ application.surname }}</td>
        <td>
          <!-- Status aplikacji -->
          <span v-if="!nullExists[application.id]" class="badge text-center w-40" :class="approvalStatus[application.id] ? 'badge-success' : 'badge-danger'">
                {{ approvalStatus[application.id] ? $t('application-accept-status') : $t('application-reject-status') }}
          </span>
        </td>
        <td>
          <!-- Przycisk wyświetlający szczegóły -->
          <button
              class="btn btn-primary btn-sm text-center w-40"
              @click="goToVolunteerDetails(application.userId)"
          >
            <font-awesome-icon icon="info-circle" /> {{  $t('profile-info') }}
          </button>
          <button v-if="nullExists[application.id]"
                  @click="acceptApplication(application.id)" class="btn btn-success btn-sm text-center w-40"
          >
            <font-awesome-icon icon="check" /> {{  $t('application-accept') }}
          </button>
          <button v-if="nullExists[application.id]"
                  @click="rejectApplication(application.id)" class="btn btn-danger btn-sm text-center w-40"
          >
            <font-awesome-icon icon="circle-xmark" /> {{  $t('application-reject') }}
          </button>
        </td>
      </tr>
      </tbody>
    </table>
    <div v-if="successMessage" class="alert alert-success">
      {{ successMessage }}
    </div>
    <div v-if="loading" class="text-center">
      <i class="fas fa-spinner fa-spin"></i> Ładowanie danych...
    </div>
    <div v-if="error" class="alert alert-danger ">
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
        this.error = err.message || $t('organiztion-error4'); ;
        this.loading = false;
      }
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
        this.successMessage = `${this.$t('application-message-accept')} ${applicationId}`;
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
        this.successMessage = `${this.$t('application-message-reject')} ${applicationId}`;
        setTimeout(() => (this.successMessage = null), 3000);
        this.nullExists[applicationId] = false;
        this.approvalStatus[applicationId] = false;
      } catch (err) {
        console.error("Błąd pobierania statusu approval:", err.message);
      }
    },
    goToVolunteerDetails(volunteerId) {
      this.$router.push(`/application/info/${volunteerId}`);
    },
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
