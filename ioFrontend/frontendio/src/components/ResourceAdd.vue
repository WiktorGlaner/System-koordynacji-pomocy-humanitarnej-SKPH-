<template>
  <div class="d-flex justify-content-between align-items-center mt-3">
    <h4>{{$t('resources')}}</h4>
    <button class="btn btn-primary m-10" @click="showAddResourceModal = true">
      {{$t('resources-add-resource')}}
    </button>

    <BModal v-model="showAddResourceModal" :title="$t('resources-add-resource')" @ok="addResource" :ok-disabled="!formValid">
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


        <BFormGroup :label="$t('resources-table-type')" label-for="resource-type">
          <BFormSelect
              id="resource-type"
              v-model="newResource.type"
              :options="typeOptions"
          />
        </BFormGroup>

        <BRow>
          <BCol md="6">
            <BFormGroup :label="$t('resources-table-latitude')" label-for="resource-latitude">
              <BFormInput
                  id="resource-latitude"
                  v-model="newResource.latitude"
                  type="number"
                  step="any"
                  min="-180"
                  max="180"
                  required
                  :placeholder="$t('resources-table-latitude-placeholder')"
                  :state="isLatitudeValid"
              />
            </BFormGroup>
          </BCol>

          <BCol md="6">
            <BFormGroup :label="$t('resources-table-longitude')" label-for="resource-longitude">
              <BFormInput
                  id="resource-longitude"
                  v-model="newResource.longitude"
                  type="number"
                  step="any"
                  min="-90"
                  max="90"
                  required
                  :placeholder="$t('resources-table-longitude-placeholder')"
                  :state="isLongitudeValid"
              />
            </BFormGroup>
          </BCol>
        </BRow>

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
  data() {
    return {
      showAddResourceModal: false,
      newResource: {
        name: '',
        description: '',
        quantity: 0,
        type: 'OTHER',
        latitude: 0,
        longitude: 0,
        expDate: null,
        unit: this.$t('resources-unit-pcs')
      },
    }
  },
  watch: {
    '$i18n.locale': function(newLang) {
      this.newResource.unit = this.$t('resources-unit-pcs');
    }
  },
  methods: {
    async addResource() {
      // const toast = useToast();
      const toast = useToast();

      if (this.formValid) {
        try {
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
            organisationId: 5 // @todo: change later to take organisationId from USER
          };

          const response = await ResourceService.addResource(resourceData);
          this.showAddResourceModal = false;

          this.$emit('resource-add', response.data);

          toast.success(this.$t('resources-toast-add-success'));

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
        latitude: 0,
        longitude: 0,
        expDate: null,
        unit: this.$t('resources-unit-pcs')
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
  }
}
</script>

<style scoped>

</style>