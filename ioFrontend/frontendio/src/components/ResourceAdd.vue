<template>
  <div class="d-flex justify-content-between align-items-center mt-3">
    <div class="d-flex flex-column align-items-start mb-3">
      <h4 class="mb-2">{{ isDonor ? $t('resources-donate') : $t('resources')}}</h4>
      <button class="btn btn-primary" @click="showAddResourceModal = true">
        {{ isDonor ? $t('resources-donate-add') : $t('resources-add-resource')}}
      </button>
    </div>

    <BModal v-model="showAddResourceModal" :title="$t('resources-add-resource')" @ok="addResource" :ok-disabled="!formValid" @shown="onModalShown">
      <BForm ref="form" @submit.prevent="addResource">
        <BFormGroup :label="$t('resources-table-name')" label-for="resource-name">
          <BFormInput
              id="resource-name"
              v-model="newResource.name"
              required
              :placeholder="$t('resources-table-name-placeholder')"
              :state="isNameValid"
          />
        </BFormGroup>

        <BFormGroup :label="$t('resources-table-description')" label-for="resource-description">
          <BFormTextarea
              id="resource-description"
              v-model="newResource.description"
              maxlength="250"
              :placeholder="$t('resources-table-description-placeholder')"
          />
        </BFormGroup>
        <BRow>
          <BCol md="6">
            <BFormGroup :label="$t('resources-table-quantity')" label-for="resource-quantity">
              <BFormInput
                  id="resource-quantity"
                  v-model="newResource.quantity"
                  type="number"
                  min="0"
                  required
                  :placeholder="$t('resources-table-quantity-placeholder')"
                  :state="isQuantityValid"
              />
            </BFormGroup>
          </BCol>
          <BCol md="6">
            <BFormGroup :label="$t('resources-table-unit')" label-for="resource-unit">
              <BFormSelect
                  id="resource-unit"
                  v-model="newResource.unit"
                  :options="unitOptions"
              />
            </BFormGroup>
          </BCol>
        </BRow>

        <BFormGroup :label="$t('resource-location')" label-for="resource-location">
          <div :id="`resource-add-map-container`"></div>
        </BFormGroup>

        <BFormGroup :label="$t('resources-table-type')" label-for="resource-type">
          <BFormSelect
              id="resource-type"
              v-model="newResource.type"
              :options="typeOptions"
          />
        </BFormGroup>

        <BFormGroup :label="$t('resource-select-organization')" v-if="isDonor">
          <BFormSelect
              id="resource-organisationid"
              v-model="newResource.organizationId"
              :options="formattedOrganizationOptions"
              :placeholder="$t('resource-organization-placeholder')"
              required
              :state="isOrganisationIdValid"
          />
        </BFormGroup>

        <BFormGroup :label="$t('resources-table-expDate')" label-for="resource-expDate">
          <BFormInput
              id="resource-expDate"
              v-model="newResource.expDate"
              type="date"
              :min="todayPlusOneDay"
              :state="isExpDateValid"
          />
        </BFormGroup>

      </BForm>
    </BModal>
  </div>
</template>

<script>
import {BCol, BForm, BFormGroup, BFormInput, BFormSelect, BFormTextarea, BModal, BRow} from "bootstrap-vue-next";
import {useToast} from 'vue-toastification';
import ResourceService from "@/services/resource.service.js";
import UserService from "@/services/user.service.js";

export default {
  components: {
    BModal,
    BForm,
    BFormGroup,
    BFormSelect,
    BFormInput,
    BFormTextarea,
    BRow,
    BCol
  },
  async mounted() {
    if (this.isDonor) {
      await this.fetchOrganizations();
    }
    await this.initMap();
  },
  data() {
    return {
      showAddResourceModal: false,
      newResource: {
        name: '',
        description: '',
        quantity: 0,
        type: 'OTHER',
        latitude: 51.75,
        longitude: 19.45,
        expDate: null,
        unit: this.$t('resources-unit-pcs'),
        organizationId: null,
      },
      organizationOptions: [],
      blueCircle: null,
    }
  },
  watch: {
    '$i18n.locale': function() {
      this.newResource.unit = this.$t('resources-unit-pcs');
    }
  },
  methods: {
    async fetchOrganizations() {
      try {
        const response = await UserService.getAllOrganizations();
        this.organizationOptions = response.data.map(org => ({ id: org.id, name: org.name }));
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t('organization-fetch-error');
      }
    },
    onModalShown() {
      if (this.map) {
        this.$nextTick(() => {
          this.map.invalidateSize();
        });
      }
    },
    async initMap() {
      const mapContainer = document.getElementById(`resource-add-map-container`);
      mapContainer.style.height = '250px';
      mapContainer.style.width = '100%';
      this.map = L.map(mapContainer).setView([51.75, 19.45], 14);

      L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      }).addTo(this.map);

      this.map.on('click', (e) => {
        const { lat, lng } = e.latlng;
        if (this.blueCircle) {
          this.map.removeLayer(this.blueCircle);
        }
        this.blueCircle = L.circle([lat, lng], {
          color: 'blue',
          fillColor: '#03f',
          fillOpacity: 0.5,
          radius: 75,
        }).addTo(this.map);

        this.newResource.latitude = lat;
        this.newResource.longitude = lng;
      });
    },
    async addResource() {
      const toast = useToast();

      if (this.formValid) {
        try {
          let response = null;

          if (this.isDonor) {

            if (!this.newResource.organizationId) {
              toast.error(this.$t('resources-toast-organization-required'));
              return;
            }

            const donationData = {
              name: this.newResource.name,
              description: this.newResource.description,
              quantity: this.newResource.quantity,
              unit: this.newResource.unit,
              resourceType: this.newResource.type,
              location: {
                latitude: this.newResource.latitude,
                longitude: this.newResource.longitude,
              },
              expDate: this.newResource.expDate,
              organisationId: this.newResource.organizationId,
              donorId: this.$store.state.auth.user.id,
            };

            response = await ResourceService.addDonation(donationData);
            toast.success(this.$t('resources-toast-donation-success'));
          } else {
            const res = await UserService.getOrganizationInfo();
            const resourceData = {
              name: this.newResource.name,
              description: this.newResource.description,
              quantity: this.newResource.quantity,
              unit: this.newResource.unit,
              resourceType: this.newResource.type,
              location: {
                latitude: this.newResource.latitude,
                longitude: this.newResource.longitude,
              },
              expDate: this.newResource.expDate,
              organisationId: res.data.id
            };

            response = await ResourceService.addResource(resourceData);
            toast.success(this.$t("resources-toast-add-success"));
          }

          this.showAddResourceModal = false;
          this.$emit('resource-add', response.data);
          this.resetForm();
        } catch (error) {
          console.log(error);
          toast.error(this.$t('resources-toast-add-error'));
        }
      }
    },
    resetForm() {
      this.newResource = {
        name: '',
        description: '',
        quantity: 0,
        type: 'OTHER',
        latitude: 51.75,
        longitude: 19.45,
        expDate: null,
        unit: this.$t('resources-unit-pcs'),
        organizationId: null,
      };
    },
    nextDay() {
      const today = new Date();
      today.setDate(today.getDate() + 1);
      const year = today.getFullYear();
      const month = (today.getMonth() + 1).toString().padStart(2, '0');
      const day = today.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  },
  computed: {
    formattedOrganizationOptions() {
      return this.organizationOptions.map(org => ({
        value: org.id,
        text: org.name || org.id.toString(),
      }));
    },
    isDonor() {
      return this.$store.state.auth.user.roles.includes('ROLE_DONOR');
    },
    formValid() {
      return this.isExpDateValid
          && this.isQuantityValid
          && this.isLongitudeValid
          && this.isLatitudeValid
          && this.isNameValid;
    },
    typeOptions() {
      return [
        { value: 'FOOD', text: this.$t('map-form-food') },
        { value: 'TRANSPORT', text: this.$t('map-form-transport') },
        { value: 'CLOTHING', text: this.$t('map-form-clothing') },
        { value: 'MEDICAL', text: this.$t('map-form-medical') },
        { value: 'FINANCIAL', text: this.$t('map-form-financial') },
        { value: 'EQUIPMENT', text: this.$t('map-form-eq') },
        { value: 'HOUSING', text: this.$t('map-form-housing') },
        { value: 'OTHER', text: this.$t('map-form-other') },
      ]
    },
    unitOptions() {
      return [
        { value: "kg", text: this.$t('resources-unit-kilograms')},
        { value: "g", text: this.$t('resources-unit-grams')},
        { value: "L", text: this.$t('resources-unit-liters')},
        { value: this.$t('resources-unit-pcs'), text: this.$t('resources-unit-pcs')},
        { value: "zł", text: this.$t('resources-unit-zl')},
      ]
    },
    todayPlusOneDay() {
      const today = new Date();
      today.setDate(today.getDate() + 1);  // increase day by 1
      const year = today.getFullYear();
      const month = (today.getMonth() + 1).toString().padStart(2, '0');
      const day = today.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    isNameValid() {
      return this.newResource.name !== '';
    },
    isQuantityValid() {
      return this.newResource.quantity > 0;
    },
    isLatitudeValid() {
      return this.newResource.latitude !== ''
          && this.newResource.latitude > -180 && this.newResource.latitude < 180;
    },
    isLongitudeValid() {
      return this.newResource.longitude !== ''
          && this.newResource.longitude > -90 && this.newResource.longitude < 90;
    },
    isExpDateValid() {
      if (this.newResource.type === 'FOOD' || this.newResource.type === 'MEDICAL') {
        return this.newResource.expDate !== '' && this.newResource.expDate >= this.todayPlusOneDay;
      }
      return true;
    },
    isOrganisationIdValid() {
      return this.newResource.organizationId !== undefined && this.newResource.organizationId !== null && this.newResource.organizationId !== "";
    }
  }
}
</script>

<style scoped>

</style>