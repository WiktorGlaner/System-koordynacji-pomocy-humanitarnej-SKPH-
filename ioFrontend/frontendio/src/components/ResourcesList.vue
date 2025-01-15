<template>
  <div class="container mt-3">
    <div class="mb-100">
      <BCard>
        <BRow class="mb-3 align-items-start">
          <BCol md="6">
              <BFormGroup :label="$t('resources-status-label')" class="mb-4">
                <BFormCheckboxGroup v-model="statusFilter" stacked>
                  <BFormCheckbox value="EXPIRED" class="mb-2">{{ $t('resources-expired') }}</BFormCheckbox>
                  <BFormCheckbox value="AVAILABLE" class="mb-2">{{ $t('resources-available') }}</BFormCheckbox>
                  <BFormCheckbox value="FULLY_ASSIGNED" class="mb-2">{{ $t('resources-fullyassigned') }}</BFormCheckbox>
                  <BFormCheckbox value="DAMAGED" class="mb-2">{{ $t('resources-damaged') }}</BFormCheckbox>
                </BFormCheckboxGroup>
              </BFormGroup>
          </BCol>

          <BCol md="6">
              <BFormGroup :label="$t('resources-type-label')">
                <BFormCheckboxGroup v-model="typeFilter" stacked>
                  <BFormCheckbox value="FOOD" class="mb-2">{{$t('map-form-food')}}</BFormCheckbox>
                  <BFormCheckbox value="TRANSPORT" class="mb-2">{{$t('map-form-transport')}}</BFormCheckbox>
                  <BFormCheckbox value="CLOTHING" class="mb-2">{{$t('map-form-clothing')}}</BFormCheckbox>
                  <BFormCheckbox value="MEDICAL" class="mb-2">{{$t('map-form-medical')}}</BFormCheckbox>
                  <BFormCheckbox value="FINANCIAL" class="mb-2">{{$t('map-form-financial')}}</BFormCheckbox>
                  <BFormCheckbox value="EQUIPMENT" class="mb-2">{{$t('map-form-eq')}}</BFormCheckbox>
                  <BFormCheckbox value="HOUSING" class="mb-2">{{$t('map-form-housing')}}</BFormCheckbox>
                  <BFormCheckbox value="OTHER" class="mb-2">{{$t('map-form-other')}}</BFormCheckbox>
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
            :items="filteredResources"
            :fields="fields"
            :per-page="perPage"
            :current-page="currentPage"
            striped
            hover
            responsive
            :sort-by.sync="sortBy"
            @page-change="onPageChange"
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
            {{ translateStatus(data.item.status) }}
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
      isLoading: true,
      typeFilter: [],
      resources: [],
      statusFilter: [],
      sortBy: [{ key: 'name', order: 'desc' }],
      perPage: 10,
      currentPage: 1,
      translations: {
        AVAILABLE: 'resources-available',
        FULLY_ASSIGNED: 'resources-fullyassigned',
        EXPIRED: 'resources-expired',
        DAMAGED: 'resources-damaged'
      },
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
    fields() {
      return [
        { key: "name", label: this.$t('resources-table-name'), sortable: true },
        { key: "description", label: this.$t('resources-table-description') },
        { key: "quantity", label: this.$t('resources-table-quantity'), sortable: true },
        { key: "status", label: this.$t('resources-table-status')},
        { key: "addedDate", label: this.$t('resources-table-addedDate'), sortable: true },
        { key: "expDate", label: this.$t('resources-table-expDate'), sortable: true },
      ]
    },
  },
  mounted() {
    this.fetchResources();
  },
  watch: {
    typeFilter() {
      this.resetPagination();
    },
    statusFilter() {
      this.resetPagination();
    },
  },
  methods: {
    onPageChange(page) {
      this.currentPage = page;
    },
    translateStatus(status) {
      return this.$t(this.translations[status] || status);
    },
    resetPagination() {
      this.currentPage = 1;
    },
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
    },
    updateResources(newResource) {
      this.resources.push({...newResource});
    },
  },
};
</script>

<style scoped>
@import "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css";
</style>