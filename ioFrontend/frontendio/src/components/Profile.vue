<template>
  <div class="container">
    <header class="jumbotron">
      <h3>
        <strong>{{currentUser.username}}</strong> Profile
      </h3>
    </header>
    <p>
      <strong>Token:</strong>
      {{currentUser.accessToken.substring(0, 20)}} ... {{currentUser.accessToken.substr(currentUser.accessToken.length - 20)}}
    </p>
    <p>
      <strong>Id:</strong>
      {{currentUser.id}}
    </p>
    <p>
      <strong>Email:</strong>
      {{currentUser.email}}
    </p>
    <strong>Authorities:</strong>
    <ul>
      <li v-for="role in currentUser.roles" :key="role">{{role}}</li>
    </ul>

    <button @click="toggleForm" class="btn btn-primary">
      {{ showForm ? "Ukryj" : "Wypełnij dane o profilu" }}
    </button>

    <!-- Formularz wyświetlany warunkowo -->
    <div v-if="showForm && !currentUser.roles.includes('ROLE_ORGANIZATION')" class="form-container">
      <h4>Podaj swoje dane osobowe</h4>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">Name:</label>
          <input v-model="name" id="name" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="surname">Surname:</label>
          <input v-model="surname" id="surname" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <label for="pesel">PESEL:</label>
          <input v-model="pesel" id="pesel" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-success">Submit</button>
        </div>
      </form>
    </div>

    <!-- Formularz dla użytkowników z rolą "organization" -->
    <div v-if="showForm && currentUser.roles.includes('ROLE_ORGANIZATION')" class="form-container">
      <h4>Podaj dane organizacji</h4>
      <form @submit.prevent="submitOrganizationForm">
        <div class="form-group">
          <label for="organizationName">Nazwa organizacji:</label>
          <input v-model="name" id="name" type="text" class="form-control" />
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-success">Submit</button>
        </div>
      </form>
    </div>

  </div>
</template>

<script>
import UserService from '../services/user.service';

export default {
  name: 'Profile',
  data() {
    return {
      username: '',
      name: '',
      surname: '',
      pesel: '',
      updatedUser: null,
      error: null,
      showForm: false, // Dodane pole do przechowywania widoczności formularza
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
    toggleForm() {
      this.showForm = !this.showForm; // Przełącza widoczność formularza
    },
    submitForm() {
      const requestData = {
        name: this.name,
        surname: this.surname,
        pesel: this.pesel,
      };
      console.log('Current User:', this.currentUser.username);
      UserService.fillUserInformation(requestData, this.currentUser.accessToken)
          .then(response => {
            console.log(response.data)
            // this.updatedUser = updatedUser;
            // this.error = null;
          })
          .catch(error => {
            this.error = error.response ? error.response.data : 'Nieznany błąd';
            this.updatedUser = null;
          });
    },
    submitOrganizationForm() {
      const requestData = {
        name: this.name,
      };
      console.log('Current User:', this.currentUser.username);
      UserService.fillOrganizationInformation(requestData, this.currentUser.accessToken)
          .then(response => {
            console.log(response.data)
            // this.updatedUser = updatedUser;
            // this.error = null;
          })
          .catch(error => {
            this.error = error.response ? error.response.data : 'Nieznany błąd';
            this.updatedUser = null;
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