<template>
  <div class="container mt-5">
    <!-- Alert for unauthorized users -->
    <div v-if="!allowedRole" class="alert text-center" style="font-size: 18px; margin-top: 20px;">
      {{ $t('task-denied') }}
    </div>

    <!-- Tasks table and search filters, visible only if allowedRole() is true -->
    <div v-if="allowedRole">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="mb-0">{{$t('tasks-tasks')}}</h1>
        <button v-if="hasRole('ROLE_ORGANIZATION')" class="btn btn-primary" @click="createTask()">
          <font-awesome-icon icon="plus" /> {{$t('tasks-createTask')}}
        </button>
      </div>
      <div class="mb-3">
        <div class="row">
          <div class="col-md-3">
            <label for="searchTitle" class="form-label">{{ $t('tasks-searchByTitle') }}</label>
            <input 
              id="searchTitle" 
              type="text" 
              class="form-control" 
              v-model="filters.titleKeyword" 
              :placeholder="$t('tasks-enterKeyWorld')"
            />
          </div>
          <div class="col-md-3">
            <label for="statusFilter" class="form-label">{{ $t('tasks-status') }}</label>
            <select id="statusFilter" class="form-select" v-model="filters.status">
              <option value="">{{ $t('tasks-all') }}</option>
              <option 
                v-for="option in filterOptions.status" 
                :key="option.value" 
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
          </div>
          <div class="col-md-3">
            <label for="filterBy" class="form-label">{{ $t('tasks-filterBy') }}</label>
            <select id="filterBy" class="form-select" v-model="filters.by">
              <option value="">{{ $t('tasks-none') }}</option>
              <option value="priority">{{ $t('tasks-priority') }}</option>
              <option value="organization">{{ $t('tasks-organization') }}</option>
              <option value="location">{{ $t('tasks-location') }}</option>
            </select>
          </div>
          <div class="col-md-3">
            <label for="filterValue" class="form-label">{{ $t('tasks-filterValue') }}</label>
            <select 
              id="filterValue" 
              class="form-select" 
              v-model="filters.value" 
              :disabled="!filters.by || filters.by === 'noRating'"
            >
              <option value="">{{ $t('tasks-all') }}</option>
              <option 
                v-for="option in filterOptions[filters.by]" 
                :key="option.value" 
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
          </div>
        </div>
      </div>

      <!-- Table displaying tasks -->
      <table class="table table-striped table-bordered">
        <thead class="thead-dark">
          <tr>
            <th v-for="(header, index) in translatedTableHeaders" :key="index">{{ header }}</th>
            <th class="text-center" style="width: 1%;">{{ $t('tasks-info') }}</th>
            <th class="text-center" style="width: 1%;" v-if="hasRole('ROLE_ORGANIZATION')">{{ $t('tasks-edit') }}</th>
            <th class="text-center" style="width: 1%;" v-if="hasRole('ROLE_ORGANIZATION') || hasRole('ROLE_VOLUNTEER')">{{ $t('tasks-end') }}</th>
            <th class="text-center" style="width: 1%;" v-if="hasRole('ROLE_ORGANIZATION') || hasRole('ROLE_VOLUNTEER')">{{$t('tasks-route')}}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="task in paginatedTasks" :key="task.task.id">
            <td>{{ task.task.id }}</td>
            <td>{{ task.task.title }}</td>
            <td>{{ task.task.organization.name }}</td>
            <td>{{ task.task.location }}</td>
            <td>{{ translatedPriority[task.task.priority] || task.task.priority }}</td>
            <td>{{ translatedStatus[task.task.status] || task.task.status }}</td>
            <td>
              <span v-if="task.task.grade">{{ task.task.grade }}</span>
              <span v-else>{{ $t('tasks-notGraded') }}</span>
            </td>
            <td>
              <button 
                class="btn btn-primary btn-sm text-center w-100" 
                @click="goToTaskDetails(task.task.id)"
              >
                <font-awesome-icon icon="info-circle" /> {{ $t('tasks-info') }}
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
                <font-awesome-icon icon="edit" /> {{ $t('tasks-edit') }}
              </button>
              <button 
                class="btn btn-warning btn-sm text-center w-100" 
                @click="rateTask(task.task.id)"
                v-else-if="task.task.status === 'COMPLETED' || task.task.status === 'GRADED'"
                :disabled="task.task.status === 'GRADED' || task.task.organization.id !== organizationInfo.id"
                :class="{ 'disabled-gray': task.task.organization.id !== organizationInfo.id }"
              >
                <font-awesome-icon icon="star" /> {{ $t('tasks-grade') }}
              </button>
            </td>

            <td v-if="hasRole('ROLE_ORGANIZATION')">
              <button 
                class="btn btn-danger btn-sm text-center w-100" 
                @click="endTask(task.task.id)"
                :disabled="task.task.status === 'COMPLETED' || task.task.status === 'GRADED' || task.task.organization.id !== organizationInfo.id"
                :class="{ 'disabled-gray': task.task.organization.id !== organizationInfo.id }"
              >
                <font-awesome-icon icon="trash-alt" /> {{ $t('tasks-end') }}
              </button>
            </td>
            <td v-if="hasRole('ROLE_VOLUNTEER')">
              <button 
                class="btn btn-danger btn-sm text-center w-100" 
                @click="endTask(task.task.id)"
                :disabled="task.task.status === 'COMPLETED' || task.task.status === 'GRADED'"
              >
                <font-awesome-icon icon="trash-alt" /> {{ $t('tasks-end') }}
              </button>
            </td>
            <td v-if="hasRole('ROLE_ORGANIZATION')">
              <button 
                class="btn btn-info btn-sm text-center w-100"
                @click="designateRoute(this.tasks.find(t => t.task.id === task.task.id)?.resources[0]?.location, { latitude: task.task.request.latitude, longitude: task.task.request.longitude })"
                :disabled="task.task.status === 'COMPLETED' || task.task.status === 'GRADED' || task.task.organization.id !== organizationInfo.id"
                :class="{ 'disabled-gray': task.task.organization.id !== organizationInfo.id }"
              >
              <font-awesome-icon :icon="['fas', 'route']" /> {{ $t('tasks-route') }}
              </button>
            </td>
            <td v-if="hasRole('ROLE_VOLUNTEER')">
              <button 
                class="btn btn-info btn-sm text-center w-100"
                @click="designateRoute(this.tasks.find(t => t.task.id === task.task.id)?.resources[0]?.location, { latitude: task.task.request.latitude, longitude: task.task.request.longitude })"
                :disabled="task.task.status === 'COMPLETED' || task.task.status === 'GRADED'"
              >
              <font-awesome-icon :icon="['fas', 'route']" /> {{ $t('tasks-route') }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="currentPage--" :disabled="currentPage === 1">{{$t('tasks-prev')}}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="currentPage++" :disabled="currentPage === totalPages">{{$t('tasks-next')}}</button>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script>
import TaskService from '../services/task.service';
import UserService from '@/services/user.service';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faInfoCircle, faEdit, faTrashAlt, faStar, faPlus, faRoute} from '@fortawesome/free-solid-svg-icons';

library.add(faInfoCircle, faEdit, faTrashAlt, faStar, faPlus, faRoute);

export default {
  name: 'Task',
  data() {
    return {
      allowedRoles: ["ROLE_ORGANIZATION", "ROLE_VOLUNTEER", "ROLE_AUTHORITY"],
      tableHeaders: ["#", "task-title", "task-organization", "task-location", "task-priority", "task-status", "task-grade"],
      tasks: [],
      organizationInfo: {},
      filters: {
        by: "",
        value: "",
        status: "",
        titleKeyword: "",
      },
      currentPage: 1,
      pageSize: 7,
    };
  },
  created() {
    if(this.currentUser){
      if (this.hasRole('ROLE_ORGANIZATION')) {
        this.fetchOrganizationInfo();
      }
      if (this.hasRole('ROLE_ORGANIZATION') || this.hasRole('ROLE_VOLUNTEER')) {
        this.fetchTasks();
      }
    }else{
      this.$router.push("/login");
    }
  },
  computed: {
    paginatedTasks() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredTasks.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.filteredTasks.length / this.pageSize);
    },
    filterOptions() {
    return {
      status: [
        { value: 'IN_PROGRESS', label: this.$t('tasks-inProgress') },
        { value: 'COMPLETED', label: this.$t('tasks-completed') },
        { value: 'GRADED', label: this.$t('tasks-graded') }
      ],
      priority: [
       { value: 'CRITICAL', label: this.$t('tasks-critical') },
        { value: 'HIGH', label: this.$t('tasks-high') },
        { value: 'MEDIUM', label: this.$t('tasks-medium') },
        { value: 'LOW', label: this.$t('tasks-low') }
      ],
      organization: [],
      location: []
    };
  },
  translatedStatus() {
    return {
      IN_PROGRESS: this.$t('tasks-inProgress'),
      COMPLETED: this.$t('tasks-completed'),
      GRADED: this.$t('tasks-graded')
    };
  },
  translatedPriority() {
    return {
      CRITICAL: this.$t('tasks-critical'),
      HIGH: this.$t('tasks-high'),
      MEDIUM: this.$t('tasks-medium'),
      LOW: this.$t('tasks-low')
    };
  },
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
      } else if (this.filters.by === "location") {
        filtered = filtered.filter((task) =>
          !this.filters.value || task.task[this.filters.by] === this.filters.value
        );
      } else if (this.filters.by === "organization") {
        filtered = filtered.filter((task) =>
          !this.filters.value || task.task[this.filters.by].name === this.filters.value
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
    translatedTableHeaders() {
    return [
      "#",
      this.$t('tasks-title'),
      this.$t('tasks-organization'),
      this.$t('tasks-location'),
      this.$t('tasks-priority'),
      this.$t('tasks-status'),
      this.$t('tasks-grade'),
    ];
  },
  },
  methods: {
    async fetchOrganizationInfo() {
      try {
        const response = await UserService.getOrganizationInfo();
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
    updateFilterOptions() {
    this.filterOptions.organization = [
      ...new Map(this.tasks.map((t) => t.task.organization.name)
        .map((org) => [org, org])
      ).values()
    ].map((org) => ({ value: org, label: org }));

    this.filterOptions.location = [...new Set(this.tasks.map((t) => t.task.location))]
      .map((loc) => ({ value: loc, label: loc }));
  },
  getAllTasks() {
    TaskService.getAllTasks().then(
      (response) => {
        this.tasks = response.data;
        this.updateFilterOptions();
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
          this.updateFilterOptions();
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
    designateRoute(resourceLocation, requestLocation) {
      console.log(requestLocation.latitude, requestLocation.longitude);
      // Przekazujemy zarówno latitude, jak i longitude
      this.$router.push(`/map2/${resourceLocation.latitude}/${resourceLocation.longitude}/${requestLocation.latitude}/${requestLocation.longitude}`);
    },
    hasRole(role) {
      return this.currentUser.roles.includes(role);
    }
  },
  watch: {
  '$i18n.locale': function () {
    this.updateFilterOptions();
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
