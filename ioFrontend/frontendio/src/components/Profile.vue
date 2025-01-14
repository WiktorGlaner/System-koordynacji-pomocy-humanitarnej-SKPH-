<template>
  <div class="container">
    <header class="jumbotron">
      <h3>
        <strong>{{currentUser.username}}</strong> Profile
      </h3>
    </header>

    <p v-if="currentUser.roles.includes('ROLE_VICTIM')">
      You have registered like a victim
    </p>
    <p v-if="currentUser.roles.includes('ROLE_DONOR')">
      You have registered like a donor
    </p>
    <p v-if="currentUser.roles.includes('ROLE_AUTHORITY')">
      You have registered like a authority
    </p>
    <p v-if="currentUser.roles.includes('ROLE_ORGANIZATION')">
      You have registered like a organization
    </p>
    <p v-if="currentUser.roles.includes('ROLE_VOLUNTEER')">
      You have registered like a volunteer
    </p>

    <!-- Nowy przycisk do pobierania danych -->
    <button @click="fetchUserProfile(currentUser.roles)" class="btn btn-primary">
      {{showForm ? "Hide profile data" : "Show profile data"}}
    </button>

    <!-- Wyświetlanie formularza na podstawie stanu danych -->
    <div v-if="showForm && userData" class="form-container">
      <h4>{{ isProfileComplete ? "Edytuj dane" : "Uzupełnij dane" }}</h4>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">Name:</label>
          <input v-model="userData.name" id="name" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="surname">Surname:</label>
          <input v-model="userData.surname" id="surname" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="pesel">PESEL:</label>
          <input v-model="userData.pesel" id="pesel" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input v-model="currentUser.email" id="email" type="email" class="form-control" readonly />
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-success">Save</button>
        </div>
      </form>
      <div
        v-if="message"
        class="alert"
        :class="successful ? 'alert-success' : 'alert-danger'"
      >
        {{ message }}
      </div>
    </div>

    <div v-if="showForm && currentUser.roles.includes('ROLE_ORGANIZATION') && organizationData" class="form-container">
      <h4>Add organization data</h4>
      <form @submit.prevent="submitOrganizationForm">
        <div class="form-group">
          <label for="organizationName">Organization name:</label>
          <input v-model="organizationData.name" id="organizationName" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input v-model="currentUser.email" id="email" type="email" class="form-control" readonly />
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-success">Submit</button>
        </div>
      </form>
      <div
        v-if="message"
        class="alert"
        :class="successful ? 'alert-success' : 'alert-danger'"
      >
        {{ message }}
        </div>
    </div>
    
  </div>
</template>

<script>
import UserService from '../services/user.service';

export default {
  name: 'Profile',
  data() {
    return {
      userData: null,
      organizationData: null,
      isProfileComplete: false,
      error: null,
      showForm: false,
      successful: false,
      message: ""
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
  },
  methods: {
    fetchUserProfile(user) {
      this.showForm = !this.showForm;
      if (user.includes('ROLE_ORGANIZATION')) {
        UserService.getOrganizationInfo()
        .then(response => {
          this.organizationData = response.data;
          this.isProfileComplete = !!(
            this.organizationData
          );
        })
        .catch(error => {
          this.error = error.response ? error.response.data : 'Error';
        });
      } else {
        UserService.getUserInfo()
        .then(response => {
          this.userData = response.data;
          this.isProfileComplete = !!(
            this.userData.name &&
            this.userData.surname &&
            this.userData.pesel
          );
        })
        .catch(error => {
          this.error = error.response ? error.response.data : 'Error';
        });
      }
    },
    submitForm() {
      UserService.fillUserInformation(this.userData)
    .then(response => {
      this.successful = true;
      this.message = response.data.message;
      console.log('Profile updated:', response.data);
    })
    .catch(error => {
      this.successful = false;
      if (error.response && error.response.data) {
        this.message = error.response.data.message;
        this.error = error.response.data.message;
      } else {
        this.message = 'Error';
        this.error = 'Error';
      }
    });
    },
    submitOrganizationForm() {
      UserService.fillOrganizationInformation(this.organizationData)
          .then(response => {
            console.log(response.data.message)
            this.successful = true;
            this.message = response.data.message;
          })
          .catch(error => {
          this.successful = false;
          if (error.response && error.response.data) {
            this.message = error.response.data.message;
            this.error = error.response.data.message;
          } else {
            this.message = 'Error';
            this.error = 'Error';
          }
        });
    },
  },
};
</script>

<style scoped>
.form-container {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background: #f9f9f9;
}
.form-container .form-group {
  margin-bottom: 10px;
}
</style>