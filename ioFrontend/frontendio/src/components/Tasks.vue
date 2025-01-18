<template>
  <div class="container mt-5">
    <!-- Alert for unauthorized users -->
    <div v-if="!allowedRole" class="alert alert-danger text-center" style="font-size: 18px; margin-top: 20px;">
      Only users with the "ROLE_ORGANIZATION" or "ROLE_VOLUNTEER" or "ROLE_AUTHORITY" role have access to this page.
    </div>

    <!-- Tasks table and search filters, visible only if allowedRole() is true -->
    <div v-if="allowedRole">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="mb-0">Tasks</h1>
        <button v-if="hasRole('ROLE_ORGANIZATION')" class="btn btn-primary" @click="createTask()">
          <font-awesome-icon icon="plus" /> Create Task
        </button>
      </div>
      <div class="mb-3">
        <div class="row">
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
          <div class="col-md-3">
            <label for="statusFilter" class="form-label">Status</label>
            <select id="statusFilter" class="form-select" v-model="filters.status">
              <option value="">All</option>
              <option 
                v-for="option in filterOptions.status" 
                :key="option" 
                :value="option"
              >
                {{ option }}
              </option>
            </select>
          </div>
          <div class="col-md-3">
            <label for="filterBy" class="form-label">Filter By</label>
            <select id="filterBy" class="form-select" v-model="filters.by">
              <option value="">None</option>
              <option value="priority">Priority</option>
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
        </div>
      </div>

      <!-- Table displaying tasks -->
      <table class="table table-striped table-bordered">
        <thead class="thead-dark">
          <tr>
            <th v-for="(header, index) in tableHeaders" :key="index">{{ header }}</th>
            <th class="text-center" style="width: 1%;">Info</th>
            <th class="text-center" style="width: 1%;" v-if="hasRole('ROLE_ORGANIZATION')">Edit</th>
            <th class="text-center" style="width: 1%;" v-if="hasRole('ROLE_ORGANIZATION')">End</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="task in filteredTasks" :key="task.task.id">
            <td>{{ task.task.id }}</td>
            <td>{{ task.task.title }}</td>
            <td>{{ task.task.organization.name }}</td>
            <td>{{ task.task.location }}</td>
            <td>{{ task.task.priority }}</td>
            <td>{{ task.task.status }}</td>
            <td>{{ task.task.grade || "Not graded" }}</td>
            <td>
              <button 
                class="btn btn-primary btn-sm text-center w-100" 
                @click="goToTaskDetails(task.task.id)"
              >
                <font-awesome-icon icon="info-circle" /> Info
              </button>
            </td>

            <td v-if="hasRole('ROLE_ORGANIZATION')">
              <button 
                class="btn btn-success btn-sm text-center w-100" 
                @click="editTask(task.task.id)"
                v-if="task.task.status === 'IN_PROGRESS'"
                :disabled="task.task.organization.id !== organizationInfo.id"
                :class="{ 'disabled-gray': task.task.organization.id !== organizationInfo.id }"
              >
                <font-awesome-icon icon="edit" /> Edit
              </button>
              <button 
                class="btn btn-warning btn-sm text-center w-100" 
                @click="rateTask(task.task.id)"
                v-else-if="task.task.status === 'COMPLETED' || task.task.status === 'GRADED'"
                :disabled="task.task.status === 'GRADED' || task.task.organization.id !== organizationInfo.id"
                :class="{ 'disabled-gray': task.task.organization.id !== organizationInfo.id }"
              >
                <font-awesome-icon icon="star" /> Grade
              </button>
            </td>

            <td v-if="hasRole('ROLE_ORGANIZATION')">
              <button 
                class="btn btn-danger btn-sm text-center w-100" 
                @click="endTask(task.task.id)"
                :disabled="task.task.status === 'COMPLETED' || task.task.status === 'GRADED' || task.task.organization.id !== organizationInfo.id"
                :class="{ 'disabled-gray': task.task.organization.id !== organizationInfo.id }"
              >
                <font-awesome-icon icon="trash-alt" /> End
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import TaskService from '../services/task.service';
import UserService from '@/services/user.service';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faInfoCircle, faEdit, faTrashAlt, faStar, faPlus} from '@fortawesome/free-solid-svg-icons';

library.add(faInfoCircle, faEdit, faTrashAlt, faStar, faPlus);

export default {
  name: 'Task',
  data() {
    return {
      allowedRoles: ["ROLE_ORGANIZATION", "ROLE_VOLUNTEER", "ROLE_AUTHORITY"],
      tableHeaders: ["#", "Title", "Organization", "Location", "Priority", "Status", "Grade"],
      tasks: [],
      organizationInfo: {},
      filters: {
        by: "",
        value: "",
        status: "",
        titleKeyword: "",
      },
      filterOptions: {
        status: ["IN_PROGRESS", "COMPLETED", "GRADED"],
        priority: ["HIGH", "MEDIUM", "LOW"],
        organization: [], // Automatyczne opcje
        location: [], // Automatyczne opcje
      },
    };
  },
  created() {
    if (this.hasRole('ROLE_ORGANIZATION')) {
      this.fetchOrganizationInfo();
    }
    this.fetchTasks();
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push("/login");
    }
  },
  computed: {
    filteredTasks() {
      let filtered = this.tasks;

      // Filtracja po statusie
      if (this.filters.status) {
        filtered = filtered.filter((task) => task.task.status === this.filters.status);
      }

      // Filtracja po pozostałych filtrach
      if (this.filters.by === "priority") {
        filtered = filtered.filter((task) =>
          !this.filters.value || task.task.priority === this.filters.value
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
    currentUser() {
      return this.$store.state.auth.user;
    },
    allowedRole() {
      return this.currentUser && this.allowedRoles.some(role => this.currentUser.roles.includes(role));
    },
  },
  methods: {
    async fetchOrganizationInfo() {
      try {
        const response = await UserService.getOrganizationInfo();
        console.log('Organization info:', response.data);
        this.organizationInfo = response.data;
      } catch (error) {
        console.error('Error fetching organization info:', error);
      }
    },
    fetchTasks() {
      if (this.hasRole('ROLE_ORGANIZATION') || this.hasRole('ROLE_AUTHORITY')) {
        this.getAllTasks();
      } else if (this.hasRole('ROLE_VOLUNTEER')) {
        this.getVolunteersTasks(this.currentUser.username);
      }
    },
    getAllTasks() {
      TaskService.getAllTasks().then(
        (response) => {
          this.tasks = response.data;
          this.filterOptions.organization = [...new Set(this.tasks.map((t) => t.task.organization))];
          this.filterOptions.location = [...new Set(this.tasks.map((t) => t.task.location))];
        },
        (error) => {
          console.log(error.response?.data?.message || error.message || error.toString());
        }
      );
    },
    getVolunteersTasks(username) {
      TaskService.getVolunteersTasks(this.currentUser.username).then(
        (response) => {
          this.tasks = response.data;
          this.filterOptions.organization = [...new Set(this.tasks.map((t) => t.task.organization))];
          this.filterOptions.location = [...new Set(this.tasks.map((t) => t.task.location))];
        },
        (error) => {
          console.log(error.response?.data?.message || error.message || error.toString());
        }
      );
    },
    createTask() {
      this.$router.push('/tasks/create');
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
    hasRole(role) {
      return this.currentUser.roles.includes(role);
    }
  },
};
</script>

<style scoped>
.disabled-gray {
  background-color: #d3d3d3 !important;  /* Szary kolor tła */
  color: #6c757d !important;  /* Szary kolor tekstu */
  border-color: #d3d3d3 !important;  /* Dopasowanie koloru obramowania */
  cursor: not-allowed;
}
</style>
