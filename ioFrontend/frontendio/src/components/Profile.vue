<template>
  <div class="container">
    <div class="jumbotron">
      <h1>{{ $t('profile-title1') }}</h1>
    </div>
    <div class="profile-row">
      <div class="profile-item">
        <p class="label">{{ $t('profile-username') }}</p>
        <p class="value">{{ currentUser.username }}</p>
      </div>
      <div class="profile-item">
        <p class="label">Email</p>
        <p class="value">{{ currentUser.email }}</p>
      </div>
      <div class="profile-item">
        <p class="label">{{ $t('profile-role') }}</p>
        <p class="value">
          <span v-if="currentUser.roles.includes('ROLE_VICTIM')">{{ $t('profile-victim') }}</span>
          <span v-else-if="currentUser.roles.includes('ROLE_DONOR')">{{ $t('profile-donor') }}</span>
          <span v-else-if="currentUser.roles.includes('ROLE_AUTHORITY')">{{ $t('profile-authority') }}</span>
          <span v-else-if="currentUser.roles.includes('ROLE_ORGANIZATION')">{{ $t('profile-organization') }}</span>
          <span v-else-if="currentUser.roles.includes('ROLE_VOLUNTEER')">{{ $t('profile-volunteer') }}</span>
        </p>
      </div>
    </div>
  </div>

  <div v-if="userData" class="container">
    <div class="jumbotron">
      <h1>{{ $t('profile-title2') }}</h1>
    </div>
    <div class="profile-row">
      <div class="profile-item">
        <p class="label">{{ $t('profile-name') }}</p>
        <p class="value">{{ userData && userData.name ? userData.name : $t('profile-null') }}</p>
      </div>
      <div class="profile-item">
        <p class="label">{{ $t('profile-surname') }}</p>
        <p class="value">{{ userData && userData.surname ? userData.surname : $t('profile-null') }}</p>
      </div>
      <div class="profile-item">
        <p class="label">PESEL</p>
        <p class="value">{{ userData && userData.pesel ? userData.pesel : $t('profile-null') }}</p>
      </div>
      <div class="profile-item"> 
        <button @click="toogleForm()" class="btn btn-primary">
          {{showForm ?  $t('profile-edit2') : $t('profile-edit1')}}
        </button>
      </div>
    </div>    

    <!-- Wyświetlanie formularza na podstawie stanu danych -->
    <div v-if="showForm" class="form-container">
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">{{ $t('profile-name') }}</label>
          <input v-model="changeData.name" id="name" type="text" class="form-control" />
          <span v-if="errors.name" class="text-danger">{{ errors.name }}</span>
        </div>
        <div class="form-group">
          <label for="surname">{{ $t('profile-surname') }}</label>
          <input v-model="changeData.surname" id="surname" type="text" class="form-control" />
          <span v-if="errors.surname" class="text-danger">{{ errors.surname }}</span>
        </div>
        <div class="form-group">
          <label for="pesel">PESEL:</label>
          <input v-model="changeData.pesel" id="pesel" type="text" class="form-control" />
          <span v-if="errors.pesel" class="text-danger">{{ errors.pesel }}</span>
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-success">{{ $t('profile-save') }}</button>
        </div>
      </form>
      <div v-if="message" class="alert" :class="successful ? 'alert-success' : 'alert-danger'">
      {{ message }}
    </div>
    </div>
  </div>

  <div v-if="currentUser.roles.includes('ROLE_ORGANIZATION')" class="container">
    <div class="jumbotron">
      <h1>{{ $t('profile-title3') }}</h1>
    </div>
    <div class="profile-row">
      <div class="profile-item-organization">
        <p class="label">{{ $t('profile-organization-name') }}</p>
        <p class="value">{{ organizationData && organizationData.name ? organizationData.name : $t('profile-null') }}</p>
      </div>
      <div class="profile-item"> 
        <button @click="toogleForm()" class="btn btn-primary">
          {{showForm ? $t('profile-edit2') : $t('profile-edit1')}}
        </button>
      </div>
    </div>   
    <div v-if="showForm" class="form-container">
      <form @submit.prevent="submitOrganizationForm">
        <div class="form-group">
          <label for="organizationName">{{ $t('profile-organization-name') }}</label>
          <input v-model="changeDataOrg.name" id="organizationName" type="text" class="form-control" />
          <span v-if="errors.name" class="text-danger">{{ errors.name }}</span>
        </div>
        <div class="form-group">
          <button type="submit" class="btn btn-success">{{ $t('profile-save') }}</button>
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
      changeData: {
        name: "",
        surname: "",
        pesel: ""
      },
      changeDataOrg: {
        name: ""
      },
      userData: null,
      organizationData: null,
      isProfileComplete: false,
      error: null,
      showForm: false,
      successful: false,
      message: "",
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    errors() {
      const errors = {
        name: null,
        surname: null,
        pesel: null
      };
      
      if (this.currentUser.roles.includes('ROLE_ORGANIZATION')) {
         if (!this.changeDataOrg.name) {
          errors.name = this.$t('profile-organization-required');
      } 
    } else {
        if (!this.changeData.name ) {
        errors.name = this.$t('profile-name-required');
      }

      if (!this.changeData.surname) {
        errors.surname = this.$t('profile-surname-required');
      }

      if (!this.changeData.pesel) {
        errors.pesel = this.$t('profile-pesel-required');
      } else if (!/^\d{11}$/.test(this.changeData.pesel)) {
        errors.pesel = this.$t('profile-pesel-invalid');
      }
      
    }
    return errors;
  },
},
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    } else {
      this.fetchUserProfile(this.currentUser.roles);
      console.log(this.userData);
    }
  },
  methods: {
    fetchUserProfile(roles) {
      if (roles.includes('ROLE_ORGANIZATION')) {
        UserService.getOrganizationInfo()
        .then(response => {
          this.organizationData = response.data;
          this.changeDataOrg = { ...this.organizationData };
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
          this.changeData = { ...this.userData };
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
    validateForm() {
      return !this.errors.name && !this.errors.surname && !this.errors.pesel;
    },
    validateOrganizationForm() {
      return !this.errors.name;
    },
    submitForm() {
      if (this.validateForm()) {
        UserService.fillUserInformation(this.changeData)
      .then(response => {
        this.successful = true;
        this.userData = { ...this.changeData };
        this.message = this.$t('profile-success');

        setTimeout(() => {
              this.message = "";
            }, 5000);
      })
      .catch(error => {
        this.successful = false;
        if (error.response && error.response.data) {
          this.message = this.$t('profile-error1');
          this.error = error.response.data
        } else {
          this.message = this.$t('profile-error2');
          this.error = this.$t('profile-error2');
        }
      });
    }
    },
    submitOrganizationForm() {
      if (this.validateOrganizationForm()) {
      UserService.fillOrganizationInformation(this.changeDataOrg)
          .then(response => {
            this.organizationData = { ...this.changeDataOrg };
            this.successful = true;
            this.message = this.$t('profile-success');

            setTimeout(() => {
              this.message = "";
            }, 5000);
          })
          .catch(error => {
          this.successful = false;
          if (error.response && error.response.data) {
            this.message = this.$t('profile-error1');
            this.error = error.response.data
          } else {
            this.message = this.$t('profile-error2');
            this.error = this.$t('profile-error2');
          }
        });
      }
    },
    toogleForm() {
      this.message = "";
      this.showForm = !this.showForm;
    }
  },
};
</script>

<style scoped>
.container {
  width: 80%;
  margin: 0 auto;
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.jumbotron {
  text-align: center;
  margin-bottom: 20px;
}

.profile-row {
  display: flex;
  justify-content: space-between;
  gap: 20px; /* Odstęp między elementami */
}

.profile-item {
  flex: 1;
  text-align: center;
}

.profile-item-organization {
  flex: 1;
  text-align: left;
}

.label {
  font-weight: bold;
  font-size: 1rem;
  margin-bottom: 5px;
  color: #555;
}

.value {
  font-size: 1.2rem;
  color: #333;
}
</style>