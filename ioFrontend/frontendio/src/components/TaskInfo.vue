<template>
  <div class="container-fluid d-flex justify-content-center align-items-center  mt-4">
    <div class="card h-100" style="width: 100%; padding: 20px;">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
          <h1 v-if="task.task" class="card-title mx-auto">{{ task.task.title }}</h1>
          <button 
            class="btn btn-secondary" 
            @click="goBack" 
            aria-label="Back"
          >
            Powrót
          </button>
        </div>
        <div class="container mt-5">
          <div v-if="task.task" class="mb-3">
            <label class="form-label">Description</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="task.task.description" 
              readonly
            >
          </div>
          <div v-if="task.task" class="mb-3">
            <label class="form-label">Grade</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="task.task.grade" 
              readonly
            >
          </div>
          <div v-if="task.task" class="mb-3">
            <label class="form-label">Location</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="task.task.location" 
              readonly
            >
          </div>
          <div v-if="task.task" class="mb-3">
            <label class="form-label">Priority</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="task.task.priority" 
              readonly
            >
          </div>
          <div v-if="task.task" class="mb-3">
            <label class="form-label">Status</label>
            <input 
              type="text" 
              class="form-control" 
              v-model="task.task.status" 
              readonly
            >
          </div>

          <h2 class="text-center mt-4">Organization</h2>
            <ul class="list-group mt-3">
              <li 
                class="list-group-item"
                v-if="task.task && task.task.organization"
              >
                <p><strong>Organization name:</strong> {{ task.task.organization.name }}</p>
                <p><strong>Head of organization:</strong> {{ task.task.organization.user.username }}</p>

              </li>
            </ul>

          <h2 class="text-center mt-4">Request</h2>
          <ul class="list-group mt-3">
            <li 
                class="list-group-item"
                v-if="task.task && task.task.request"
              >
              <p><strong>Description:</strong> {{ task.task.request.description || 'Not provided' }}</p>
              <p><strong>Type:</strong> {{ task.task.request.resource_type || 'Not provided' }}</p>
              <p><strong>Amount:</strong> {{ task.task.request.amount || 'Not provided' }}</p>
              <p><strong>Location:</strong> 
                {{ task.task.request.latitude && task.task.request.longitude ? task.task.request.latitude + ', ' + task.task.request.longitude : 'Location not provided' }}
              </p>
              <p><strong>Status:</strong> {{ task.task.request.status || 'Not provided' }}</p>
              <p><strong>Reporter:</strong> 
                {{ task.task.request.reporter && task.task.request.reporter.name && task.task.request.reporter.surname ? task.task.request.reporter.name + ' ' + task.task.request.reporter.surname : 'No provided' }}
              </p>
              </li>
          </ul>
          <h2 class="text-center mt-4">Resources</h2>
          <ul class="list-group mt-3" v-if="task.resources">
            <li 
              class="list-group-item" 
              v-for="resource in task.resources" 
              :key="resource.id"
            >
              <p><strong>Name:</strong> {{ resource.name }}</p>
              <p><strong>Description:</strong> {{ resource.description || 'No description' }}</p>
              <p><strong>Location:</strong> {{ resource.location.latitude }}, {{ resource.location.longitude }}</p>
              <p><strong>Expiration Date:</strong> {{ resource.expDate }}</p>
            </li>
          </ul>
          <h2 class="text-center mt-4">Volunteers</h2>
          <ul class="list-group mt-3" v-if="task.resources">
            <li 
              class="list-group-item" 
              v-for="volunteer in task.task.volunteers" 
              :key="volunteer.id"
            >
              <p><strong>Name and surname:</strong> {{ volunteer.name }} {{ volunteer.surname }}</p>
              <p><strong>Username:</strong> {{ volunteer.user.username }}</p>
              <p><strong>PESEL:</strong> {{ volunteer.pesel }}</p>
              <p><strong>Email:</strong> {{ volunteer.user.email}}</p>
              <p><strong>Active:</strong> {{ volunteer.activity ? 'Yes' : 'No' }}</p>
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
  data: function() {
    return {
      task: {},
    };
  },
  created() {
    this.fetchTaskDetails();
  },
  methods: {
    async fetchTaskDetails() {
      try {
        const response = await TaskService.getTask(this.id);
        console.log(response.data);
        this.task = response.data;
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
  },
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
