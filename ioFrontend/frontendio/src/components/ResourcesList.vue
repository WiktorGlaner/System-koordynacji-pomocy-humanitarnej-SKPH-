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

        <BRow class="mb-3">
          <BCol>
            <BFormGroup :label="$t('resources-search-label')">
              <BFormInput
                  v-model="nameFilter"
                  :placeholder="$t('resources-search-placeholder')"
              />
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
              {{ data.item.assignedQuantity ? data.item.quantity + "/" + (data.item.assignedQuantity + Number(data.item.quantity)) : data.item.quantity  }} {{ data.item.unit }}
            </template>

            <template #cell(dynamicId)="data">
              <div v-if="currentUser.roles.includes('ROLE_AUTHORITY')">
                {{this.organizationInfo[data.item.organisationId] ? this.organizationInfo[data.item.organisationId] : data.item.organisationId}}
              </div>
            </template>

            <template #cell(actions)="data">
              <BButton
                  v-if="currentUser.roles.includes('ROLE_DONOR')"
                  variant="info"
                  size="sm"
                  @click="openDonorInfoModal(data.item)"
                  class="ms-2"
              >
                {{ $t('resource-info-button') }}
              </BButton>
              <BButton
                  v-if="currentUser.roles.includes('ROLE_ORGANIZATION')"
                  variant="primary"
                  size="sm"
                  @click="openEditModal(data.item)"
                  :disabled="data.item.status !== 'AVAILABLE' && data.item.status !== 'DAMAGED'"
              >
                <font-awesome-icon icon="edit" />
                {{ $t('resource-edit') }}
              </BButton>
              <BButton
                  v-if="currentUser.roles.includes('ROLE_ORGANIZATION')"
                  variant="danger"
                  size="sm"
                  @click="deleteResource(data.item)"
                  class="ms-2"
              >
                {{ $t('resource-delete') }}
              </BButton>
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
  <BModal
      id="donor-info-modal"
      v-model="isDonorInfoModalVisible"
      :title="$t('resource-donation-usage')"
      hide-footer
      size="lg"
  >
    <div v-if="selectedResourceInfo !== null && selectedResourceInfo.length > 0">
      <ul>
        <li v-for="(info, index) in selectedResourceInfo" :key="index" class="mb-3">
          <strong>{{ $t('resource-info-request-id') }}:</strong> {{ info.requestId }} &nbsp;
          <strong>{{ $t('resource-info-quantity') }}:</strong> {{ info.assignedQuantity }} {{ info.resource.unit }}
        </li>
      </ul>
    </div>
    <div v-else>
      <p>{{ $t('resource-info-no-usage-found') }}</p>
    </div>
  </BModal>
  <ResourceEdit
      :isVisible="isEditModalVisible"
      :resourceData="resourceToEdit"
      @close="closeEditModal"
      @save="saveResourceChanges"
  />
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
  BTable,
  BButton,
  BFormInput,
  BModal
} from 'bootstrap-vue-next';
import ResourceService from "@/services/resource.service.js";
import UserService from "@/services/user.service.js";
import ResourceEdit from "@/components/ResourceEdit.vue";
import {useToast} from 'vue-toastification';

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
    BButton,
    ResourceEdit,
    BFormInput,
    BModal
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
      isEditModalVisible: false,
      resourceToEdit: {
        description: '',
        quantity: 0,
        status: null,
        location: {
          latitude: null,
          longitude: null,
        }
      },
      nameFilter: '',
      errorMessage: '',
      organizationInfo: [],
      isDonorInfoModalVisible: false,
      selectedResourceInfo: null,
    };
  },
  computed: {
    filteredResources() {
      return this.resources.filter((resource) => {
        const matchesType =
            this.typeFilter.length === 0 || this.typeFilter.includes(resource.resourceType);
        const matchesStatus =
            this.statusFilter.length === 0 || this.statusFilter.includes(resource.status);
        const matchesName =
            !this.nameFilter || resource.name.toLowerCase().includes(this.nameFilter.toLowerCase());
        return matchesType && matchesStatus && matchesName;
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
        baseFields.push({ key: "dynamicId", label: this.$t('resources-table-organisation') });
      }

      baseFields.push({ key: "actions", label: '', class: "text-center" });

      return baseFields;
    },
  },
  async mounted() {
    if (this.currentUser.roles.includes("ROLE_AUTHORITY")) {
      await this.fetchOrganizations();
    }
    await this.fetchResourcesAndAssignments();
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
    async openDonorInfoModal(resource) {
      try {
        this.isDonorInfoModalVisible = true;
        const response = await ResourceService.getResourceUsage(resource.id);
        this.selectedResourceInfo = response.data;
      } catch (error) {
        this.selectedResourceInfo = {
          requestId: 'N/A',
          quantity: 'N/A',
          usageDetails: [this.$t('resource-info-fetch-error')],
        };
      }
    },
    closeDonorInfoModal() {
      this.isDonorInfoModalVisible = false;
      this.selectedResourceInfo = null;
    },
    openEditModal(resource) {
      this.resourceToEdit = { ...resource };
      this.isEditModalVisible = true;
    },
    closeEditModal() {
      this.isEditModalVisible = false;
    },
    saveResourceChanges(updatedResource) {
      const index = this.resources.findIndex(r => r.id === updatedResource.id);
      if (index !== -1) {
        this.resources[index] = { ...updatedResource };
      }
      this.closeEditModal();
    },
    onPageChange(page) {
      this.currentPage = page;
    },
    translateStatus(status) {
      return this.$t(this.translations[status] || status);
    },
    resetPagination() {
      this.currentPage = 1;
    },
    async fetchOrganizations() {
      try {
        const response = await UserService.getAllOrganizations();
        this.organizationInfo = response.data.reduce((map, org) => {
          map[org.id] = org.name;
          return map;
        }, {});
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t('organization-fetch-error');
      }
    },
    async deleteResource(resource) {
      const toast = useToast();
      try {
        if (resource.assignedQuantity > 0) {
          toast.error(this.$t('resource-delete-error'), {
            title: this.$t('resource-error'),
            variant: 'danger',
            solid: true,
          });
          return;
        }
        if (confirm(this.$t('resource-delete-confirm'))) {
          await ResourceService.deleteResource(resource.id);
          this.resources = this.resources.filter((r) => r.id !== resource.id);
          toast.success(this.$t('resource-delete-success'), {
            title: this.$t('resource-success'),
            variant: 'success',
            solid: true,
          });
        }
      } catch (error) {
        console.error(error);
        toast.error(this.$t('resource-delete-failure'), {
          title: this.$t('resource-error'),
          variant: 'danger',
          solid: true,
        });
      }
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