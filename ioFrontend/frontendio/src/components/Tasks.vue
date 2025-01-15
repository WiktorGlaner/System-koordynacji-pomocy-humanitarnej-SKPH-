<template>
  <div class="container mt-5">
    <h1 class="mb-4">Tasks</h1>
    <!-- Sekcja filtrów -->
    <div class="mb-3">
      <div class="row">
        <div class="col-md-3">
          <label for="filterBy" class="form-label">Filter By</label>
          <select id="filterBy" class="form-select" v-model="filters.by">
            <option value="">None</option>
            <option value="status">Status</option>
            <option value="priority">Priority</option>
            <option value="noRating">No Rating & Completed</option>
            <option value="organization">Organization</option>
            <option value="location">Location</option>
          </select>
        </div>
        <div class="col-md-3">
          <label for="filterValue" class="form-label">Filter Value</label>
          <select 
            id="filterValue" 
            class="form-select" 
            v-model="filters.value" 
            :disabled="!filters.by || filters.by === 'noRating'"
          >
            <option value="">All</option>
            <option 
              v-for="option in filterOptions[filters.by]" 
              :key="option" 
              :value="option"
            >
              {{ option }}
            </option>
          </select>
        </div>
        <div class="col-md-3">
          <label for="searchTitle" class="form-label">Search by Title</label>
          <input 
            id="searchTitle" 
            type="text" 
            class="form-control" 
            v-model="filters.titleKeyword" 
            placeholder="Enter keyword..."
          />
        </div>
      </div>
    </div>

    <table class="table table-striped table-bordered">
      <thead class="thead-dark">
        <tr>
          <th v-for="(header, index) in tableHeaders" :key="index">{{ header }}</th>
          <th class="text-center" style="width: 1%;">Info</th>
          <th class="text-center" style="width: 1%;">Edit</th>
          <th class="text-center" style="width: 1%;">End</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="task in filteredTasks" :key="task.task.id">
          <td>{{ task.task.id }}</td>
          <td>{{ task.task.title }}</td>
          <td>{{ task.task.organization }}</td>
          <td>{{ task.task.location }}</td>
          <td>{{ task.task.priority }}</td>
          <td>{{ task.task.status }}</td>
          <td>
            <button 
              class="btn btn-primary btn-sm text-center w-100" 
              @click="goToTaskDetails(task.task.id)"
            >
              <font-awesome-icon icon="info-circle" /> Info
            </button>
          </td>
          <td>
            <button 
              class="btn btn-success btn-sm text-center w-100" 
              @click="editTask(task.task.id)"
              v-if="task.task.status === 'IN_PROGRESS'"
            >
              <font-awesome-icon icon="edit" /> Edit
            </button>
            <button 
              class="btn btn-warning btn-sm text-center w-100" 
              @click="rateTask(task.task.id)"
              v-else-if="task.task.status === 'COMPLETED'"
              :disabled="task.task.grade"
            >
              <font-awesome-icon icon="star" /> Grade
            </button>
          </td>
          <td>
            <button 
              class="btn btn-danger btn-sm text-center w-100" 
              @click="endTask(task.task.id)"
              :disabled="task.task.status === 'COMPLETED'"
            >
              <font-awesome-icon icon="trash-alt" /> End
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import TaskService from '../services/task.service';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faInfoCircle, faEdit, faTrashAlt, faStar } from '@fortawesome/free-solid-svg-icons';

library.add(faInfoCircle, faEdit, faTrashAlt, faStar);

export default {
  name: 'Task',
  data() {
    return {
      tableHeaders: ["#", "Title", "Organization", "Location", "Priority", "Status"],
      tasks: [],
      filters: {
        by: "",
        value: "",
        titleKeyword: "",
      },
      filterOptions: {
        status: ["IN_PROGRESS", "COMPLETED"],
        priority: ["HIGH", "MEDIUM", "LOW"],
        noRating: [],
        organization: [], // Automatyczne opcje
        location: [], // Automatyczne opcje
      },
    };
  },
  created() {
    this.fetchTasks();
  },
  computed: {
    filteredTasks() {
      let filtered = this.tasks;

      // Filtruj po wybranym filtrze
      if (this.filters.by === "status") {
        filtered = filtered.filter((task) =>
          !this.filters.value || task.task.status === this.filters.value
        );
      } else if (this.filters.by === "priority") {
        filtered = filtered.filter((task) =>
          !this.filters.value || task.task.priority === this.filters.value
        );
      } else if (this.filters.by === "noRating") {
        filtered = filtered.filter(
          (task) => task.task.status === "COMPLETED" && task.task.grade === 0
        );
      } else if (this.filters.by === "organization" || this.filters.by === "location") {
        filtered = filtered.filter((task) =>
          !this.filters.value || task.task[this.filters.by] === this.filters.value
        );
      }

      // Wyszukiwanie po title
      if (this.filters.titleKeyword) {
        filtered = filtered.filter((task) =>
          task.task.title.toLowerCase().includes(this.filters.titleKeyword.toLowerCase())
        );
      }

      return filtered;
    },
  },
  methods: {
    fetchTasks() {
      TaskService.getAllTasks().then(
        (response) => {
          this.tasks = response.data;

          // Automatyczne ładowanie unikalnych wartości dla organization i location
          this.filterOptions.organization = [...new Set(this.tasks.map((t) => t.task.organization))];
          this.filterOptions.location = [...new Set(this.tasks.map((t) => t.task.location))];
        },
        (error) => {
          console.log(error.response?.data?.message || error.message || error.toString());
        }
      );
    },
    goToTaskDetails(taskId) {
      this.$router.push(`/tasks/info/${taskId}`);
    },
    editTask(taskId) {
      this.$router.push(`/tasks/edit/${taskId}`);
    },
    rateTask(taskId) {
      this.$router.push(`/tasks/rate/${taskId}`);
    },
    endTask(taskId) {
      TaskService.endTask(taskId).then(
        () => this.fetchTasks(),
        (error) => {
          console.log(error.response?.data?.message || error.message || error.toString());
        }
      );
    },
  },
};
</script>
