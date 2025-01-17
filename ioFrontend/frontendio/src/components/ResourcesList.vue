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

        <div v-else>
          <BTable
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
            <font-awesome-icon :icon="getTypeIcon(data.item.resourceType)" class="me-2" />

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
              {{ data.item.assignedQuantity ? data.item.quantity + "/" + (data.item.assignedQuantity + data.item.quantity) : data.item.quantity  }} {{ data.item.unit }}
            </template>

            <template #cell(dynamicId)="data">
              {{ currentUser.roles.includes('ROLE_AUTHORITY') ? data.item.organisationId : data.item.donorId }}
            </template>
          </BTable>
          <div v-if="errorMessage" class="alert alert-danger">
            {{ errorMessage }}
          </div>
          <BPagination
              v-model="currentPage"
              :total-rows="filteredResources.length"
              :per-page="perPage"
              align="center"
              class="mt-3"
          />
        </div>
      </BCard>

    </div>
  </div>
</template>

<script>
import {
  BCard,
  BCol,
  BFormCheckbox,
  BFormCheckboxGroup,
  BFormGroup,
  BPagination,
  BRow,
  BTable
} from 'bootstrap-vue-next';
import ResourceService from "@/services/resource.service.js";
import UserService from "@/services/user.service.js";

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
      errorMessage: '',
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
    currentUser() {
      return this.$store.state.auth.user;
    },
    fields() {
      const baseFields = [
        { key: "id", label: "id", sortable: true },
        { key: "name", label: this.$t('resources-table-name'), sortable: true },
        { key: "description", label: this.$t('resources-table-description') },
        { key: "quantity", label: this.$t('resources-table-quantity'), sortable: true },
        { key: "status", label: this.$t('resources-table-status')},
        { key: "addedDate", label: this.$t('resources-table-addedDate'), sortable: true },
        { key: "expDate", label: this.$t('resources-table-expDate'), sortable: true },
      ];

      if (this.currentUser.roles.includes("ROLE_AUTHORITY")) {
        baseFields.push({ key: "dynamicId", label: this.$t('resources-table-organisationId') });
      } else if (this.currentUser.roles.includes("ROLE_ORGANIZATION")) {
        baseFields.push({ key: "dynamicId", label: this.$t('resources-table-donorId') });
      }

      return baseFields;
    },
  },
  mounted() {
    this.fetchResourcesAndAssignments();
    this.startAutoUpdate();
  },
  beforeDestroy() {
    this.stopAutoUpdate();
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
    async fetchResourcesAndAssignments() {
      try {
        this.errorMessage = '';
        let response;

        if (this.currentUser.roles.includes("ROLE_ORGANIZATION")) {
          const res = await UserService.getOrganizationInfo();
          response = await ResourceService.getOrganisationResources(res.data.id);
        } else if (this.currentUser.roles.includes("ROLE_AUTHORITY")) {
          response = await ResourceService.getAllResources();
        } else if (this.currentUser.roles.includes("ROLE_DONOR")) {
          response = await ResourceService.getDonorResources(this.$store.state.auth.user.id);
        }

        this.resources = response.data;
        const responseAssignments = await ResourceService.getTotalAssignedQuantity();
        this.resources = this.resources.map(resource => {
          resource.assignedQuantity = responseAssignments[resource.id] || 0;
          return resource;
        });
      } catch (error) {
        console.error(error);
        this.resources = []
        this.errorMessage = this.$t('resources-fetch-error');
      } finally {
        this.isLoading = false;
      }
    },
    startAutoUpdate() {
      this.autoUpdateInterval = setInterval(() => {
        this.fetchResourcesAndAssignments();
      }, 60000);
    },
    stopAutoUpdate() {
      if (this.autoUpdateInterval) {
        clearInterval(this.autoUpdateInterval);
      }
    },
    getTypeIcon(type) {
      const icons = {
        FOOD: "utensils",
        TRANSPORT: "car",
        CLOTHING: "tshirt",
        MEDICAL: "briefcase-medical",
        FINANCIAL: "dollar-sign",
        EQUIPMENT: "tools",
        HOUSING: "home",
        OTHER: "ellipsis-h",
      };
      return icons[type] || "question";
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

</style>