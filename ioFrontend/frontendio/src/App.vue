<template>
  <div id="app">
    <nav class="navbar navbar-expand navbar-dark bg-dark w-100">
      <a href="/" class="navbar-brand">SKPH</a>
      <div class="navbar-nav mr-auto">
        <li class="nav-item">
          <router-link to="/home" class="nav-link">
            <font-awesome-icon icon="home" /> {{ t('nav-home') }}
          </router-link>
        </li>
        <li v-if="showAdminBoard" class="nav-item">
          <router-link to="/admin" class="nav-link">{{ t('nav-admin-board') }}</router-link>
        </li>
        <li v-if="showModeratorBoard" class="nav-item">
          <router-link to="/mod" class="nav-link">{{ t('nav-moderator-board') }}</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/communication" class="nav-link">{{ t('nav-chat') }}</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/map" class="nav-link">{{ t('nav-map') }}</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/tasks" class="nav-link">{{ t('nav-tasks') }}</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/resource" class="nav-link">{{ t('nav-resource') }}</router-link>
        </li>
        <li class="nav-item" v-if="currentUser && (currentUser.roles.includes('ROLE_AUTHORITY') || currentUser.roles.includes('ROLE_VOLUNTEER'))">
          <router-link to="/reports" class="nav-link">{{ t('nav-reports') }}</router-link>
        </li>
      </div>
      <div class="navbar-nav ml-auto">
        <li class="nav-item" v-if="!currentUser">
          <router-link to="/login" class="nav-link">{{ t('nav-login') }}</router-link>
        </li>
        <li class="nav-item" v-if="!currentUser">
          <router-link to="/register" class="nav-link">{{ t('nav-sign-up') }}</router-link>
        </li>
        <li class="nav-item" v-if="currentUser">
          <router-link to="/profile" class="nav-link">
            <font-awesome-icon icon="user" /> {{ currentUser.username }}
          </router-link>
        </li>
        <li class="nav-item" v-if="currentUser">
          <a href="#" class="nav-link" @click="logout">
            <font-awesome-icon icon="sign-out-alt" /> {{ t('nav-logout') }}
          </a>
        </li>
        <li class="nav-item">
          <button type="button" class="btn btn-link" @click="changeLanguage('en')">
            <img src="./assets/en_flag.svg" alt="English" class="flag-icon" />
          </button>
        </li>
        <li class="nav-item">
          <button type="button" class="btn btn-link" @click="changeLanguage('pl')">
            <img src="./assets/pl_flag.svg" alt="Polish" class="flag-icon" />
          </button>
        </li>
      </div>
    </nav>

    <div class="container mt-3">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { ref, computed, onMounted, watch } from 'vue';
import { useStore } from 'vuex';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

const store = useStore();
const { t, locale } = useI18n();
const router = useRouter();
const currentUser = computed(() => store.state.auth.user);

const changeLanguage = async (lang) => {
  try {
    if (currentUser.value && currentUser.value.id) {
      const response = await axios.get(`http://localhost:8080/lang/${currentUser.value.id}/${lang}`);
      console.log('Language changed successfully:', response.data);
    }
    locale.value = lang;
    localStorage.setItem('language', lang); // Save language to localStorage
  } catch (err) {
    console.error('Error while changing language:', err.response?.data || err.message);
  }
};

const setLangAfterLogin = async () => {
  try {
    if (!currentUser.value || !currentUser.value.id) {
      console.error('User is not logged in or ID is missing.');
      return;
    }
    const response = await axios.get(`http://localhost:8080/lang/getlang/${currentUser.value.id}`);
    locale.value = response.data;
    console.log('Language set successfully:', response.data);
  } catch (err) {
    console.error('Error while setting language:', err.response?.data || err.message);
  }
};

onMounted(async () => {
  const savedLanguage = localStorage.getItem('language');
  if (savedLanguage) {
    locale.value = savedLanguage;
  } else {
    await setLangAfterLogin();
  }
});

watch(locale, () => {
  // This will trigger when the locale changes
  console.log('Locale changed to:', locale.value);
});

const showAdminBoard = computed(() => currentUser.value && currentUser.value.roles.includes('ROLE_ADMIN'));
const showModeratorBoard = computed(() => currentUser.value && currentUser.value.roles.includes('ROLE_MODERATOR'));

const logout = () => {
  store.dispatch('auth/logout');
  router.push('/login');
};
</script>
<style scoped>

.navbar-nav.ml-auto {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.nav-item {
  margin-left: 10px;
}
</style>