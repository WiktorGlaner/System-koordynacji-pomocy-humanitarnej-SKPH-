<template>
  <div class="container my-4">
    <div v-if="roles.includes('ROLE_AUTHORITY')">
      <RequestInfoAuth/>
    </div>
    <div v-else-if="roles.includes('ROLE_VICTIM')">
      <div class="activity">
        <button type="submit" class="btn btn-primary" @click="setMode(1)">{{ $t('add-request') }}</button>
        <button type="submit" class="btn btn-primary" @click="setMode(2)">{{$t('show-request-list')}}</button>
      </div>
      <div class="mode1" v-if="mode === 1">
        <RequestAdd/>
      </div>
      <div class="mode2" v-else-if="mode === 2">
        <RequestInfoVictim/>
      </div>
    </div>
    <div v-else>
      <h1 style="display: flex; justify-content: center; margin-top: 100px;">You are not allowed to use requests</h1>
    </div>
  </div>
</template>

<script>
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import RequestAdd from "@/components/RequestAdd.vue";
import RequestInfoVictim from "@/components/RequestInfoVictim.vue";
import RequestInfoAuth from "@/components/RequestInfoAuth.vue";

export default {
  components: {RequestInfoAuth, RequestInfoVictim, RequestAdd, FontAwesomeIcon},
  data() {
    return {
      roles: [],
      username: null,
      mode: null,
    };
  },
  created() {
    this.checkRole();
    this.getUser();
  },
  methods: {
    checkRole() {
      const userData = localStorage.getItem('user');
      const user = JSON.parse(userData);
      this.roles = user.roles;
    },
    getUser() {
      const userData = localStorage.getItem('user');
      const user= JSON.parse(userData);
      this.username = user.username;
    },
    setMode(mode) {
      this.mode = mode;
    },
  }
};
</script>

<style>

  .edit {
    display: flex;
    text-align: center;
    align-items: center;
    justify-content: right;
    flex-direction: row-reverse;
    gap: 10px;
  }
  .edit > input {
  }

  .carddiv {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    width: 100%;
  }

  .activity {
    padding: 30px;
    display: flex;
    gap: 20px;
  }
</style>
