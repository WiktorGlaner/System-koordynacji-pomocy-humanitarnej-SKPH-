<template>
  <div class="container mt-4">
    <h2 class="mb-4">Resources</h2>
    <button
        class="btn btn-primary mb-3"
        @click="toggleAvailableOnly"
    >
      {{ showAvailableOnly ? 'Show All Resources' : 'Show Only Available Resources' }}
    </button>
    <div v-if="isLoading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    <div v-else>
      <div class="row g-3">
        <div
            class="col-md-6"
            v-for="resource in filteredResources"
            :key="resource.id"
        >
          <div class="card h-100">
            <div class="card-body">
              <h5 class="card-title">
                {{ resource.name }}
                <i
                    :class="getIconClass(resource.resourceType)"
                    class="ms-2"
                    aria-hidden="true"
                ></i>
              </h5>
              <ul class="list-group list-group-flush">
                <li class="list-group-item"><strong>Ilość:</strong> {{ resource.quantity }} {{ resource.unit }}</li>
                <li class="list-group-item"><strong>Status:</strong>
                  <i
                      :class="getIconStatusClass(resource.status)"
                      class="ms-2"
                      aria-hidden="true"
                  ></i></li>
                <li class="list-group-item"><strong>Lokalizacja:</strong>
                  Szerokość geo: {{ resource.location.latitude }}, Długość geo: {{ resource.location.longitude }}
                </li>
                <li class="list-group-item"><strong>Data dodania:</strong> {{ resource.addedDate }}</li>
                <li class="list-group-item"><strong>Data przeterminowania:</strong> {{ resource.expDate }}</li>
                <li class="list-group-item"><strong>Przeterminowane:</strong> {{ resource.expired ? 'Tak' : 'Nie' }}</li>
                <li class="list-group-item"><strong>Organisation ID:</strong> {{ resource.organisationId }}</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ResourceService from "@/services/resource.service.js";

export default {
  data() {
    return {
      resources: [],
      isLoading: false,
      showAvailableOnly: false,
    };
  },
  computed: {
    filteredResources() {
      return this.showAvailableOnly
          ? this.resources.filter(resource => resource.status === "AVAILABLE")
          : this.resources;
    },
  },
  created() {
    this.fetchResources();
  },
  methods: {
    async fetchResources() {
      this.isLoading = true;
      try {
        const response = await ResourceService.getAllResources();
        this.resources = response.data;
        console.log(this.resources);
      } catch (error) {
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },
    toggleAvailableOnly() {
      this.showAvailableOnly = !this.showAvailableOnly;
    },
    getIconClass(resourceType) {
      const icons = {
        FOOD: 'fas fa-utensils',
        TRANSPORT: 'fas fa-bus',
        CLOTHING: 'fas fa-tshirt',
        MEDICAL: 'fas fa-briefcase-medical',
        FINANCIAL: 'fas fa-dollar-sign',
        EQUIPMENT: 'fas fa-cogs',
        HOUSING: 'fas fa-home',
        OTHER: 'fas fa-ellipsis-h',
      };
      return icons[resourceType] || 'fas fa-question-circle';
    },
    getIconStatusClass(resourceStatus) {
      const icons = {
        EXPIRED: 'fas fa-clock',
        AVAILABLE: 'fas fa-check-circle',
        UNAVAILABLE: 'fas fa-times-circle'
      };
      return icons[resourceStatus] || 'fas fa-question-circle';
    }
  },
};
</script>

<style>
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css";
</style>
