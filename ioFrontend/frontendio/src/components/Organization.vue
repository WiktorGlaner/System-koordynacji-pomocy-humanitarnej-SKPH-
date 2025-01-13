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
      async fetchOrganizations() {
        const API_URL = "http://localhost:8080/api/user"; // Zamień na prawidłowy URL backendu
        try {
          const response = await axios.get(`${API_URL}/allOrganizations`, {
            headers: authHeader()
          });
          this.organizations = response.data; // Przypisz dane do listy organizacji
          this.loading = false;
        } catch (err) {
          this.error = err.message || "Nieznany błąd";
          this.loading = false;
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