<template>
  <div class="container-fluid d-flex justify-content-center align-items-center mt-4">
    <div class="card h-100" style="width: 100%; padding: 20px;">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
          <h1 v-if="task.task" class="card-title mx-auto">{{ task.task.title }}</h1>
          <button class="btn btn-secondary" @click="goBack" aria-label="Back">
            {{ $t('taskInfo-goBack') }}
          </button>
        </div>
        <div class="container mt-5">
          <div v-if="task.task" class="mb-3">
            <label class="form-label">{{ $t('taskInfo-description') }}</label>
            <input type="text" class="form-control" v-model="task.task.description" readonly>
          </div>
          <div v-if="task.task" class="mb-3">
            <label class="form-label">{{ $t('taskInfo-grade') }}</label>
            <input type="text" class="form-control" v-model="task.task.grade" readonly>
          </div>
          <div v-if="task.task" class="mb-3">
            <label class="form-label">{{ $t('taskInfo-location') }}</label>
            <input type="text" class="form-control" v-model="task.task.location" readonly>
          </div>
          <div v-if="task.task" class="mb-3">
            <label class="form-label">{{ $t('taskInfo-priority') }}</label>
            <input type="text" class="form-control" :value="translatedPriority[task.task.priority] || task.task.priority" readonly>
          </div>
          <div v-if="task.task" class="mb-3">
            <label class="form-label">{{ $t('taskInfo-status') }}</label>
            <input type="text" class="form-control" :value="translatedStatus[task.task.status] || task.task.status" readonly>
          </div>

          <h2 class="text-center mt-4">{{ $t('taskInfo-organization') }}</h2>
          <ul class="list-group mt-3">
            <li class="list-group-item" v-if="task.task && task.task.organization">
              <p><strong>{{ $t('taskInfo-organizationName') }}:</strong> {{ task.task.organization.name }}</p>
              <p><strong>{{ $t('taskInfo-headOfOrganization') }}:</strong> {{ task.task.organization.user.username }}</p>
            </li>
          </ul>

          <h2 class="text-center mt-4">{{ $t('taskInfo-request') }}</h2>
          <ul class="list-group mt-3">
            <li class="list-group-item" v-if="task.task && task.task.request">
              <p><strong>{{ $t('taskInfo-description') }}:</strong> {{ task.task.request.description || $t('taskInfo-notProvided') }}</p>
              <p><strong>{{ $t('taskInfo-type') }}:</strong> {{ task.task.request.resource_type || $t('taskInfo-notProvided') }}</p>
              <p><strong>{{ $t('taskInfo-amount') }}:</strong> {{ task.task.request.amount || $t('taskInfo-notProvided') }}</p>
              <p><strong>{{ $t('taskInfo-location') }}: </strong>
                {{ task.task.request.latitude && task.task.request.longitude ? task.task.request.latitude + ', ' + task.task.request.longitude : $t('taskInfo-notProvided') }}
              </p>
              <p><strong>{{ $t('taskInfo-status') }}: </strong>
                {{ translatedStatus[task.task.request.status] || task.task.request.status || $t('taskInfo-notProvided')}}
              </p>
              <p><strong>{{ $t('taskInfo-reporter') }}:</strong>
                {{ task.task.request.reporter && task.task.request.reporter.name && task.task.request.reporter.surname ? task.task.request.name + ' ' + task.task.request.surname : $t('taskInfo-notProvided') }}
              </p>
            </li>
          </ul>

          <h2 class="text-center mt-4">{{ $t('taskInfo-resources') }}</h2>
          <ul class="list-group mt-3" v-if="task.resources">
            <li class="list-group-item" v-for="resource in task.resources" :key="resource.id">
              <p><strong>{{ $t('taskInfo-name') }}:</strong> {{ resource.name }}</p>
              <p><strong>{{ $t('taskInfo-description') }}:</strong> {{ resource.description || $t('taskInfo-notProvided') }}</p>
              <p><strong>{{ $t('taskInfo-location') }}:</strong> {{ resource.location.latitude }}, {{ resource.location.longitude }}</p>
              <p><strong>{{ $t('taskInfo-expirationDate') }}:</strong> {{ resource.expDate }}</p>
            </li>
          </ul>

          <h2 class="text-center mt-4">{{ $t('taskInfo-volunteers') }}</h2>
          <ul class="list-group mt-3" v-if="task && task.task && task.task.volunteers && task.task.volunteers.length">

            <li class="list-group-item" v-for="volunteer in task.task.volunteers" :key="volunteer.id">
              <p><strong>{{ $t('taskInfo-nameAndSurname') }}:</strong> {{ volunteer.name }} {{ volunteer.surname }}</p>
              <p><strong>{{ $t('taskInfo-username') }}:</strong> {{ volunteer.user.username }}</p>
              <p><strong>{{ $t('taskInfo-pesel') }}:</strong> {{ volunteer.pesel }}</p>
              <p><strong>{{ $t('taskInfo-email') }}:</strong> {{ volunteer.user.email }}</p>
              <p><strong>{{ $t('taskInfo-active') }}:</strong> {{ volunteer.activity ? $t('taskInfo-activeYes') : $t('taskInfo-activeNo') }}</p>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import TaskService from '../services/task.service';

export default {
  props: ['id'],
  name: 'Task',
  data() {
    return {
      task: {},
    };
  },
  computed: {
    translatedPriority() {
      return {
        CRITICAL: this.$t('taskInfo-critical'),
        HIGH: this.$t('taskInfo-high'),
        MEDIUM: this.$t('taskInfo-medium'),
        LOW: this.$t('taskInfo-low')
      };
    },
    translatedStatus() {
      return {
        IN_PROGRESS: this.$t('taskInfo-inProgress'),
        COMPLETED: this.$t('taskInfo-completed'),
        GRADED: this.$t('taskInfo-graded')
      };
    }
  },
  created() {
    this.fetchTaskDetails();
  },
  methods: {
    async fetchTaskDetails() {
      try {
        const response = await TaskService.getTask(this.id);
        this.task = response.data;
        console.log("Task details:", this.task);
      } catch (error) {
        console.error("Error fetching task details:", error);
      }
    },
    goBack() {
      this.$router.go(-1);
    }
  },
  watch: {
    '$route.params.id': 'fetchTaskDetails',
  }
};
</script>

<style scoped>
.card {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}
.card-title {
  font-size: 24px;
  margin-bottom: 20px;
}
.list-group-item {
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 15px;
}
</style>
