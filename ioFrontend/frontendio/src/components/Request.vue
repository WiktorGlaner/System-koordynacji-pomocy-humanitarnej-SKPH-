<template>
  <div class="container mt-4">
    <h2>Requests</h2>
    <div v-if="roles.includes('ROLE_ORGANIZATION') || roles.includes('ROLE_ADMIN')">
      <div v-for="(req, index) in requests" :key="index" :class="`req${req.requestId}`">
          <div class="card h-100 w-50">
            <ul class="list-group list-group-flush">
              <li class="list-group-item">
                <div class="delete">
                  <input class="btn btn-outline-dark" type="button" value="X"
                         @click="deleteRequest(req.reporter.user.username, req.requestId)"/>
                </div>
              </li>
              <li class="list-group-item">
                <strong>ID:</strong> {{ req.requestId }}
              </li>
              <li class="list-group-item">
                <strong>Opis:</strong> {{ req.description }}
              </li>
              <li class="list-group-item">
                <strong>Typ zasobu:</strong> {{ types[req.resourceType] }}
              </li>
              <li class="list-group-item">
                <strong>Nazwa zasobu:</strong> {{ req.resourceName }}
              </li>
              <li class="list-group-item">
              <strong>Status:</strong> {{ statuses[req.status] }}
              </li>
              <li class="list-group-item">
                <strong>Location:</strong> Szerokość: {{ req.latitude }}, Długość: {{ req.longitude }}
              </li>
              <li class="list-group-item">
              <strong>Reporter:</strong>
              </li>
              <ul class="mb-2">
                <li>
                  <strong>Imię:</strong> {{ req.reporter ? req.reporter.name : 'Brak' }}
                </li>
                <li>
                <strong>Nazwisko:</strong> {{ req.reporter ? req.reporter.surname : 'Brak' }}
                </li>
                <li>
                <strong>Email:</strong> {{ req.reporter ? req.reporter.user.email : 'Brak' }}
                </li>
              </ul>
            </ul>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
import RequestService from "@/services/request.service.js";

export default {
  data() {
    return {
      requests: [],
      types: RequestService.types.reduce((acc, type) => ({ ...acc, ...type }), {}),
      statuses: RequestService.statuses.reduce((acc, status) => ({ ...acc, ...status }), {}),
      roles: [],
    };
  },
  created() {
    this.fetchRequests();
    this.checkRole();
  },
  methods: {
    async fetchRequests() {
      const response = await RequestService.getAllRequests();
      this.requests = response.data;
    },
    checkRole() {
      const userData = localStorage.getItem('user');
      const user = JSON.parse(userData);
      this.roles = user.roles;
    },
    async deleteRequest(username, id) {
      console.log(id);
      await RequestService.deleteRequest(username, id);
      const response = await RequestService.getAllRequests();
      this.requests = response.data;
    }
  }
};
</script>

<style scoped>
  .delete {
    display: flex;
    text-align: center;
    align-items: center;
    justify-content: right;
  }
  .delete > input {
    font-size: 12px;
    padding: 6px 12px;
  }
</style>
