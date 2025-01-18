<template>
  <template>
    <BModal :visible="isVisible" :title="$t('resource-edit-resource')" @shown="onModalShown" :ok-disabled="!formValid" @hide="closeModal" @ok="saveChanges">
      <BForm @submit.prevent="saveChanges">
        <BFormGroup :label="$t('resource-location')" label-for="resource-location">
          <div :id="`resource-edit-map-container`"></div>
        </BFormGroup>

        <BFormGroup :label="$t('resources-table-description')">
          <BFormTextarea
              v-model="localResource.description"
              :placeholder="$t('resources-table-description-placeholder')"
          />
        </BFormGroup>
        <BFormGroup :label="$t('resources-table-status')">
          <BFormCheckbox
              v-model="isDamagedChecked"
              :true-value="true"
              :false-value="false"
          >
            {{$t('resources-damaged')}}
          </BFormCheckbox>
        </BFormGroup>
        <BFormGroup :label="$t('resources-table-quantity')">
          <BFormInput
              type="number"
              v-model="localResource.quantity"
              min="0"
              :state="isQuantityValid"
          />
        </BFormGroup>
      </BForm>
    </BModal>
  </template>
</template>

<script>
import {
  BButton,
  BForm,
  BFormGroup,
  BFormInput,
  BFormSelect,
  BFormSelectOption,
  BFormTextarea,
  BModal,
  BFormCheckbox,
} from 'bootstrap-vue-next';
import ResourceService from "@/services/resource.service.js";
import {useToast} from "vue-toastification";

export default {
  components: {
    BFormTextarea,
    BButton,
    BFormInput,
    BFormSelect,
    BFormSelectOption,
    BFormGroup,
    BForm,
    BModal,
    BFormCheckbox,
  },
  props: {
    isVisible: { type: Boolean, default: false },
    resourceData: { type: Object, required: true },
  },
  data() {
    return {
      localResource: { ...this.resourceData },
      isDamagedChecked: false,
    };
  },
  watch: {
    resourceData: {
      immediate: true,
      handler(newData) {
        this.localResource = { ...newData };
      },
    },
  },
  computed: {
    formValid() {
      return this.isQuantityValid;
    },
    isQuantityValid() {
      return this.localResource.quantity > 0;
    },
  },
  mounted() {
    this.initMap();
  },
  methods: {
    onModalShown() {
      if (this.map) {
        this.$nextTick(() => {
          this.updateMapPosition()
          this.map.invalidateSize();
        });
      }
    },
    closeModal() {
      this.$emit("close");
    },
    async saveChanges() {
      const toast = useToast();

      const resourceId = this.localResource.id;

      const res = await ResourceService.getResource(resourceId);

      const editResourceParams = {};

      if (res.data.description !== this.localResource.description) {
        editResourceParams.description = this.localResource.description;
      }

      if (res.data.location.latitude !== this.localResource.location.latitude) {
        editResourceParams.latitude = this.localResource.location.latitude;
      }

      if (res.data.location.longitude !== this.localResource.location.longitude) {
        editResourceParams.longitude = this.localResource.location.longitude;
      }

      if (res.data.quantity !== this.localResource.quantity) {
        editResourceParams.quantity = this.localResource.quantity;
      }

      if (this.isDamagedChecked && res.data.status !== "DAMAGED") {
        editResourceParams.status = "DAMAGED";
        this.localResource.status = "DAMAGED";
      }

      if (Object.keys(editResourceParams).length > 0) {
        try {
          await ResourceService.editResource(editResourceParams, resourceId);
          toast.success(this.$t("resources-toast-edit-success"));
          this.$emit("save", this.localResource);
        } catch (error) {
          console.log(error);
          toast.error(this.$t("resources-toast-edit-error"));
        }
      } else {
        toast.info(this.$t("resources-toast-edit-nochange"))
      }

      this.closeModal();
    },
    async initMap() {
      const mapContainer = document.getElementById(`resource-edit-map-container`);
      mapContainer.style.height = '250px';
      mapContainer.style.width = '100%';
      const latitude = this.localResource.location.latitude || 51.75;
      const longitude = this.localResource.location.longitude || 19.45;
      this.map = L.map(mapContainer).setView([latitude, longitude], 14);

      this.blueCircle = L.circle([latitude, longitude], {
        color: 'blue',
        fillColor: '#03f',
        fillOpacity: 0.5,
        radius: 75,
      }).addTo(this.map);

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

        this.localResource.location.latitude = lat;
        this.localResource.location.longitude = lng;
      });
    },
    updateMapPosition() {
      if (this.map) {
        const latitude = this.localResource.location.latitude || 51.75;
        const longitude = this.localResource.location.longitude || 19.45;
        this.map.setView([latitude, longitude], 14);

        if (this.blueCircle) {
          this.map.removeLayer(this.blueCircle);
        }
        this.blueCircle = L.circle([latitude, longitude], {
          color: 'blue',
          fillColor: '#03f',
          fillOpacity: 0.5,
          radius: 75,
        }).addTo(this.map);
      }
    },
  },
};
</script>