<template>
  <div class="container-fluid d-flex justify-content-center align-items-center mt-4">
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

        <form>
          <div class="container mt-5">
            <div class="mb-3" v-if="task.task">
              <label class="form-label">Title</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="task.task.title" 
                :readonly="task.task.status === 'COMPLETED'"
                :disabled="task.task.status === 'COMPLETED'"
                :required="task.task.status === 'IN_PROGRESS'"
              >
              <div v-if="errors.title" class="text-danger small mt-2">
                {{ errors.title }}
              </div>
            </div>

            <div class="mb-3" v-if="task.task">
              <label class="form-label">Description</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="task.task.description" 
                :readonly="task.task.status === 'COMPLETED'"
                :disabled="task.task.status === 'COMPLETED'"
                :required="task.task.status === 'IN_PROGRESS'"
              >
              <div v-if="errors.description" class="text-danger small mt-2">
                {{ errors.description }}
              </div>
            </div>


            <div class="mb-3" v-if="task.task">
              <label class="form-label">Grade</label>
              <input 
                type="number" 
                class="form-control" 
                v-model="task.task.grade" 
                :readonly="task.task.status === 'IN_PROGRESS'"
                :disabled="task.task.status === 'IN_PROGRESS'"
                :required="task.task.status === 'COMPLETED'"
              >
              <div v-if="errors.grade" class="text-danger small mt-2">
                {{ errors.grade }}
              </div>
            </div>

            <div class="mb-3" v-if="task.task">
              <label class="form-label">Location</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="task.task.location" 
                :readonly="task.task.status === 'COMPLETED'"
                :disabled="task.task.status === 'COMPLETED'"
                :required="task.task.status === 'IN_PROGRESS'"
              >
              <div v-if="errors.location" class="text-danger small mt-2">
                {{ errors.location }}
              </div>
            </div>

            <div class="mb-3" v-if="task.task">
              <label class="form-label">Priority</label>
              <select 
                class="form-select" 
                v-model="task.task.priority"
                :readonly="task.task.status === 'COMPLETED'"
                :disabled="task.task.status === 'COMPLETED'"
                :required="task.task.status === 'IN_PROGRESS'"
              >
                <option value="CRITICAL">CRITICAL</option>
                <option value="HIGH">HIGH</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="LOW">LOW</option>
              </select>
            </div>

            <div class="mb-3" v-if="task.task">
              <label class="form-label">Status</label>
              <select 
                class="form-select" 
                v-model="task.task.status"
                readonly
                disabled
              >
                <option value="IN_PROGRESS">In Progress</option>
                <option value="COMPLETED">Completed</option>
              </select>
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
                <p><strong>Type:</strong> {{ resource.resourceType }}</p>
                <p><strong>Description:</strong> {{ resource.description || 'No description' }}</p>
                <p><strong>Amount:</strong> {{ resource.quantity + ' ' + resource.unit }}</p>
                <p><strong>Expiration Date:</strong> {{ resource.expDate }}</p>
              </li>
            </ul>

            <h2 class="text-center mt-4">Volunteers</h2>
            <ul class="list-group mt-3" v-if="task.task && task.task.volunteers">
              <li 
                class="list-group-item" 
                v-for="volunteer in task.task.volunteers" 
                :key="volunteer.id"
              >
                <p><strong>Name and surname:</strong> {{ volunteer.name }} {{ volunteer.surname }}</p>
                <p><strong>Username:</strong> {{ volunteer.user.username }}</p>
                <p><strong>Email:</strong> {{ volunteer.user.email}}</p>
                <p><strong>Active:</strong> {{ volunteer.activity ? 'Yes' : 'No' }}</p>
              </li>
            </ul>
          </div>

          <div class="d-flex justify-content-center mt-4">
            <button 
              type="submit" 
              class="btn btn-primary w-100"
              @click.prevent="handleSubmit"
            >
              Zatwierdź
            </button>
          </div>
        </form>
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
        errors: {},
      };
    },
    created() {
      this.fetchTaskDetails();
    },
    methods: {
      async fetchTaskDetails() {
        try {
          const response = await TaskService.getTask(this.id);
          this.task = response.data;
          console.log(this.task);
        } catch (error) {
          console.error("Error fetching task details:", error);
        }
      },
      async handleSubmit() {
        if (this.task.task.status === 'COMPLETED') {
          await this.gradeTask();
        } else {
          await this.submitForm();
        }
      },
      async gradeTask() {
        this.errors = {};
        const grade = Number(this.task.task.grade);
        if (isNaN(grade) || grade < 1 || grade > 5) {
          this.errors.grade = 'Grade must be a number between 1 and 5.';
        }
        if (Object.keys(this.errors).length > 0) {
          console.log(this.errors);
          return;
        }
        
        try {
          const response = await TaskService.gradeTask(this.id, this.task.task.grade);
          console.log(response.data);
          this.$router.push(`/tasks`);
        } catch (error) {
          console.error("Error rating task:", error);
        }
      },
      async submitForm() {
        this.errors = {};

        if (!this.task.task.title || this.task.task.title.trim() === '') {
            this.errors.title = 'Title cannot be empty.'; 
        }

        if (!this.task.task.description || this.task.task.description.trim() === '') {
            this.errors.description = 'Description cannot be empty.'; 
        }

        if (!this.task.task.location || this.task.task.location.trim() === '') {
            this.errors.location = 'Location cannot be empty.';
        }

        if (Object.keys(this.errors).length > 0) {
            console.log(this.errors);
            return;
        }

        console.log('Form submitted successfully!');

    
        const volunteersArray = JSON.parse(JSON.stringify(this.task.task.volunteers));
        const request = JSON.parse(JSON.stringify(this.task.task.request));
        const updatedTask = {
          title: this.task.task.title,
          volunteers: volunteersArray,
          request: request,
          organization: this.task.task.organization,
          description: this.task.task.description,
          grade: this.task.task.grade,
          location: this.task.task.location,
          priority: this.task.task.priority,
        };
        console.log(updatedTask);

      try {
        const response = await TaskService.editTask(this.id, updatedTask);
        this.task = response.data;
        console.log('Task updated successfully!');
        this.$router.push(`/tasks`);
      } catch (error) {
        console.error('Error updating task:', error);
        this.errors.submit = 'Failed to update task.';
      }
       


      },
      goBack() {
        this.$router.go(-1);
      }
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
  