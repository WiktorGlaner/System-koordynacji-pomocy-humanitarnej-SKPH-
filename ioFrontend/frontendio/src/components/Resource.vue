<template>
<div class="container mt-3">
  <div class="mb-100">
    <BCard>
      <BRow class="mb-3">
        <BCol md="6">
          <BFormGroup label="Filter by Type">
            <BFormCheckboxGroup v-model="statusFilter" stacked>
              <BFormCheckbox value="EXPIRED">Expired</BFormCheckbox>
              <BFormCheckbox value="AVAILABLE">Available</BFormCheckbox>
              <BFormCheckbox value="FULLY_ASSIGNED">Fully Assigned</BFormCheckbox>
              <BFormCheckbox value="DAMAGED">Damaged</BFormCheckbox>
            </BFormCheckboxGroup>
          </BFormGroup>
        </BCol>

        <BCol md="6">
          <BFormGroup label="Filter by Status">
            <BFormCheckboxGroup v-model="typeFilter" stacked>
              <BFormCheckbox value="FOOD">Food</BFormCheckbox>
              <BFormCheckbox value="TRANSPORT">Transport</BFormCheckbox>
              <BFormCheckbox value="CLOTHING">Clothing</BFormCheckbox>
              <BFormCheckbox value="MEDICAL">Medical</BFormCheckbox>
              <BFormCheckbox value="FINANCIAL">Financial</BFormCheckbox>
              <BFormCheckbox value="EQUIPMENT">Equipment</BFormCheckbox>
              <BFormCheckbox value="HOUSING">Housing</BFormCheckbox>
              <BFormCheckbox value="OTHER">Other</BFormCheckbox>
            </BFormCheckboxGroup>
          </BFormGroup>
        </BCol>
      </BRow>

      <div v-if="isLoading" class="d-flex justify-content-center align-items-center" style="height: 200px;">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>

      <BTable
          v-else
          :items="paginatedResources"
          :fields="fields"
          striped
          hover
          responsive
          :sort-by.sync="[{key: 'addedDate', order: 'desc'}]"
          :sort-desc.sync="sortDesc"
      >
        <template #cell(name)="data">
          <span class="d-flex align-items-center">
            <i :class="getTypeIcon(data.item.resourceType)" class="me-2"></i>
            {{data.item.name}}
          </span>
        </template>

        <template #cell(description)="data">
          <span :title="data.item.description">
            {{ truncateText(data.item.description, 50) }}
          </span>
        </template>

        <template #cell(status)="data">
          <span  class="badge"
                 :class="getStatusClass(data.item.status)">
            {{ data.item.status }}
          </span>
        </template>

        <template #cell(quantity)="data">
          {{ data.item.quantity }} {{ data.item.unit }}
        </template>
      </BTable>

      <BPagination
          v-model="currentPage"
          :total-rows="filteredResources.length"
          :per-page="perPage"
          align="center"
          class="mt-3"
      />
    </BCard>

  </div>
</div>
</template>

<script>
import { BCard, BRow, BCol, BFormGroup, BFormCheckboxGroup, BFormCheckbox, BTable, BPagination} from 'bootstrap-vue-next';
import ResourceService from "@/services/resource.service.js";

export default {
  components: {
    BCard,
    BRow,
    BCol,
    BFormGroup,
    BFormCheckboxGroup,
    BFormCheckbox,
    BTable,
    BPagination,
  },
  data() {
    return {
      resources: [],
      isLoading: true,
      typeFilter: [],
      statusFilter: [],
      fields: [
        { key: "name", label: "Name", sortable: true },
        { key: "description", label: "Description" },
        { key: "quantity", label: "Quantity", sortable: true},
        { key: "status", label: "Status"},
        { key: "addedDate", label: "Added Date", sortable: true },
        { key: "expDate", label: "Expiration Date", sortable: true },
      ],
      sortDesc: false,
      perPage: 5,
      currentPage: 1,
    };
  },
  computed: {
    filteredResources() {
      return this.resources.filter((resource) => {
        const matchesType =
            this.typeFilter.length === 0 || this.typeFilter.includes(resource.resourceType);
        const matchesStatus =
            this.statusFilter.length === 0 || this.statusFilter.includes(resource.status);
        return matchesType && matchesStatus;
      });
    },
    paginatedResources() {
      const start = (this.currentPage - 1) * this.perPage;
      const end = start + this.perPage;
      return this.filteredResources.slice(start, end);
    },
  },
  mounted() {
    this.fetchResources();
  },
  methods: {
    async fetchResources() {
      this.isLoading = true;
      try {
        const response = await ResourceService.getAllResources();
        this.resources = response.data;
      } catch (error) {
        console.error(error);
      } finally {
        this.isLoading = false;
      }
    },
    getTypeIcon(type) {
      const icons = {
        FOOD: "fas fa-utensils",
        TRANSPORT: "fas fa-car",
        CLOTHING: "fas fa-tshirt",
        MEDICAL: "fas fa-briefcase-medical",
        FINANCIAL: "fas fa-dollar-sign",
        EQUIPMENT: "fas fa-tools",
        HOUSING: "fas fa-home",
        OTHER: "fas fa-ellipsis-h",
      };
      return icons[type] || "fas fa-question";
    },
    getStatusClass(type) {
      const classes = {
        EXPIRED: "bg-danger",
        AVAILABLE: "bg-success",
        FULLY_ASSIGNED: "bg-warning",
        DAMAGED: "bg-secondary",
      };
      return classes[type] || "badge-secondary";
    },
    truncateText(text, length) {
      if (!text) return '';
      return text.length > length ? text.substring(0, length) + '...' : text;
    }
  },
};
</script>

<style scoped>
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css";
</style>