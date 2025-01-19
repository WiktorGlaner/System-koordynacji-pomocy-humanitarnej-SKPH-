<template>
  <div v-if="allowedRole">
    <div v-if="hasRole('ROLE_ORGANIZATION') || hasRole('ROLE_DONOR')">
      <ResourceAdd @resource-add="updateResources"/>
    </div>
    <div v-if="hasRole('ROLE_ORGANIZATION')">
      <!-- @TODO add resource modify option -->
    </div>
    <div v-if="hasRole('ROLE_ORGANIZATION') || hasRole('ROLE_AUTHORITY') || hasRole('ROLE_DONOR')">
      <ResourcesList ref="resourcesList"/>
    </div>
  </div>
  <div v-else class="alert text-center" >
    {{$t('resources-not-allowed')}}
  </div>
</template>

<script>
import ResourcesList from "@/components/ResourcesList.vue";
import ResourceAdd from "@/components/ResourceAdd.vue";

export default {
  components: {
    ResourcesList: ResourcesList,
    ResourceAdd: ResourceAdd
  },
  methods: {
    updateResources(newResource) {
      this.$refs.resourcesList.updateResources(newResource);
    },
    hasRole(role) {
      return this.currentUser.roles.includes(role);
    }
  },
  data() {
    return {
      allowedRoles: ["ROLE_ORGANIZATION", "ROLE_DONOR", "ROLE_AUTHORITY"],
    };
  },
  mounted() {
    if (!this.currentUser) {
      console.log(this.currentUser);
      this.$router.push("/login");
    }
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    allowedRole() {
      return this.currentUser && this.allowedRoles.some(role => this.currentUser.roles.includes(role));
    },
  }
}
</script>

<style scoped>

</style>