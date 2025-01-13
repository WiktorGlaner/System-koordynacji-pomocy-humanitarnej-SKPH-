<template>
  <div id="app">
    <nav class="navbar navbar-expand navbar-dark bg-dark w-100">
      <a href="/" class="navbar-brand">SKPH</a>
      <div class="navbar-nav mr-auto">
        <li class="nav-item">
          <router-link to="/home" class="nav-link">
            <font-awesome-icon icon="home" /> Home
          </router-link>
        </li>
        <li v-if="showAdminBoard" class="nav-item">
          <router-link to="/admin" class="nav-link">Admin Board</router-link>
        </li>
        <li v-if="showModeratorBoard" class="nav-item">
          <router-link to="/mod" class="nav-link">Moderator Board</router-link>
        </li>
        <li class="nav-item">
          <router-link v-if="currentUser" to="/user" class="nav-link">User</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/communication" class="nav-link">Chat</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/map" class="nav-link">Map</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/resource" class="nav-link">Resource</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/organization" class="nav-link">Organization</router-link>
        </li>
      </div>

      <div v-if="!currentUser" class="navbar-nav ml-auto">
        <li class="nav-item">
          <router-link to="/register" class="nav-link">
            <font-awesome-icon icon="user-plus" /> Sign Up
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/login" class="nav-link">
            <font-awesome-icon icon="sign-in-alt" /> Login
          </router-link>
        </li>
      </div>

      <div v-if="currentUser" class="navbar-nav ml-auto">
        <li class="nav-item">
          <router-link to="/profile" class="nav-link">
            <font-awesome-icon icon="user" />
            {{ currentUser.username }}
          </router-link>
        </li>
        <li class="nav-item">
          <a class="nav-link" @click.prevent="logOut">
            <font-awesome-icon icon="sign-out-alt" /> LogOut
          </a>
        </li>
      </div>
      <div class="language-switche">
        <button type="button" class="btn btn btn-link" @click="changeLanguage('en')">
          <img src="./assets/en_flag.svg" alt="English" class="flag-icon" />
        </button>
        <button type="button" class="btn btn btn-link" @click="changeLanguage('pl')">
          <img src="./assets/pl_flag.svg" alt="Polish" class="flag-icon" />
        </button>
      </div>
      
    </nav>

    <div class="container">
      <router-view />
    </div>
  </div>
</template>

<script>
import axios from 'axios';
export default {
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    showAdminBoard() {
      if (this.currentUser && this.currentUser['roles']) {
        return this.currentUser['roles'].includes('ROLE_ADMIN');
      }

      return false;
    },
    showModeratorBoard() {
      if (this.currentUser && this.currentUser['roles']) {
        return this.currentUser['roles'].includes('ROLE_MODERATOR');
      }

      return false;
    }
  },
  methods: {
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    },
    async changeLanguage(lang) {
      this.$i18n.locale = lang;
      try {
        if (!this.currentUser || !this.currentUser.id) {
          console.error('User is not logged in or ID is missing.');
          return;
        }
        const response = await axios.get(`http://localhost:8080/lang/${this.currentUser.id}/${lang}`)
        console.log('Language changed successfully:', response.data);
      } catch (err){
        console.error('Error while changing language:', err.response?.data || err.message);
      }
    }
  }
};
</script>