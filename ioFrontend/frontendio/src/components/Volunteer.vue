<template>
  <div class="container">
    <h2 class="text-center">Lista wolontariuszy</h2>
    <table class="table table-striped">
      <thead>
      <tr>
        <th>ID</th>
        <th>Imię</th>
        <th>Nazwisko</th>
        <th>Status</th>
        <th>Akcja</th>
      </tr>
      </thead>
      <tbody v-for="(volunteers, index) in volunteers" :key="volunteers.id">
      <tr>
        <td>{{ volunteers.id }}</td>
        <td>{{ volunteers.name }}</td>
        <td>{{ volunteers.surname }}</td>
        <td>{{ volunteers.activity ? 'Zajęty' : 'Wolny' }}</td>
        <td>
<!--          &lt;!&ndash; Przycisk wyświetlający szczegóły &ndash;&gt;-->
          <button @click="toogleForm(volunteers.id)" class="btn btn-info">
            {{ showForm[volunteers.id] ? 'Ukryj szczegóły' : 'Pokaż szczegóły' }}
          </button>
          <button @click="deleteVolunteer(volunteers.id)" class="btn btn-danger">
            {{ 'Usuń' }}
          </button>
        </td>
      </tr>
      <tr v-if="this.showForm[volunteers.id]">
        <td>Pesel: {{volunteers.pesel}}</td>
        <td>Email: {{volunteers.email}}</td>
        <td>Nazwa użytkownika: {{volunteers.username}}</td>
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
  name: "VolunteerList",
  data() {
    return {
      volunteers: [], // Lista aplikacji
      expandedInfo: {}, // Zmienna przechowująca informacje o widoczności szczegółów dla aplikacji
      loading: true, // Flaga ładowania
      error: null,
      successMessage: null,// Obsługa błędów
      showForm: {},
    };
  },
  mounted() {
    this.fetchVolunteers();
  },
  methods: {
    async fetchVolunteers() {
      const API_URL = "http://localhost:8080/api/user"; // URL backendu
      try {
        const response = await axios.get(`${API_URL}/allVolunteersByOrganizationId`, {
          headers: authHeader(),
        });
        console.log(response.data);
        this.volunteers = response.data;
        this.loading = false;
      } catch (err) {
        this.error = err.message || "Nieznany błąd";
        this.loading = false;
      }
    },
    toogleForm(volunteersId) {
      this.showForm[volunteersId] = !this.showForm[volunteersId];
    },
    async deleteVolunteer(userId) {
      const API_URL = "http://localhost:8080/api/user"; // Prawidłowy URL backendu
      try {
        const requestData = {
          id: userId,
        };
        const response = await axios.post(
            `${API_URL}/deleteVolunteer`,
            requestData,
            {
              'Content-Type': 'application/json',
              headers: authHeader(),
            }
        );
        this.successMessage = `Wolontariusz o ID: ${userId} została usunięty`;
        setTimeout(() => (this.successMessage = null), 3000);
        this.fetchVolunteers()
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
