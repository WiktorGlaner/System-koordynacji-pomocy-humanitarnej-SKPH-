<template>
    <div class="container-fluid d-flex justify-content-center align-items-center mt-4">
      <div class="card h-100" style="width: 100%; padding: 20px;">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center">
            <h1 class="card-title mx-auto">{{ task.task.title || 'Title' }}</h1>
            <button class="btn btn-secondary" @click="goBack" aria-label="Back">Powrót</button>
          </div>
  
          <form @submit.prevent="submitForm">
            <div class="container mt-5">
                <div class="mb-3">
                    <label class="form-label">Title</label>
                    <input 
                        type="text" 
                        class="form-control" 
                        v-model="task.task.title" 
                        placeholder="Enter the title of the task" 
                    />
                    <div v-if="errors.title" class="text-danger small mt-2">{{ errors.title }}</div>
                </div>

                <div class="mb-3">
                    <label class="form-label">Description</label>
                    <input 
                        type="text" 
                        class="form-control" 
                        v-model="task.task.description" 
                        placeholder="Enter a description of the task" 
                    />
                    <div v-if="errors.description" class="text-danger small mt-2">{{ errors.description }}</div>
                </div>

                <div class="mb-3">
                    <label class="form-label">Location</label>
                    <input 
                        type="text" 
                        class="form-control" 
                        v-model="task.task.location" 
                        placeholder="Enter the location of the task" 
                    />
                    <div v-if="errors.location" class="text-danger small mt-2">{{ errors.location }}</div>
                </div>

                <div class="mb-3">
                    <label class="form-label">Priority</label>
                    <select class="form-select" v-model="task.task.priority">
                        <option value="" disabled selected>Select priority</option>
                        <option value="CRITICAL">CRITICAL</option>
                        <option value="HIGH">HIGH</option>
                        <option value="MEDIUM">MEDIUM</option>
                        <option value="LOW">LOW</option>
                    </select>
                    <div v-if="errors.priority" class="text-danger small mt-2">{{ errors.priority }}</div>
                </div>
  
              <div class="mb-3">
                <label class="form-label">Status</label>
                <select class="form-select" readonly disabled>
                  <option value="IN_PROGRESS">In Progress</option>
                </select>
              </div>

              <div class="mb-3 mt-10">
                <h1 class="card-title mx-auto text-center">Choose request</h1>

                <!-- Dodane pola do wyszukiwania -->
                <div class="row mb-3">
                    <div class="col-md-4">
                        <input
                            type="text"
                            v-model="searchRequestQuery"
                            class="form-control"
                            placeholder="Search by resource name"
                        />
                    </div>
                    <div class="col-md-4">
                        <select v-model="selectedRequestType" class="form-control">
                            <option value="">Filter by type</option>
                            <option v-for="type in uniqueRequestTypes" :key="type" :value="type">{{ type }}</option>
                        </select>
                    </div>
                </div>

                <div v-if="allRequests.length > 0">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>id</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Type</th>
                                <th>Amount</th>
                                <th>Location</th>
                                <th>Reporter</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="request in filteredRequests" :key="request.requestId">
                                <td>{{ request.requestId }}</td>
                                <td>{{ request.resourceName }}</td>
                                <td>{{ request.description }}</td>
                                <td>{{ request.resourceType }}</td>
                                <td>{{ request.amount }}</td>  
                                <td>{{ request.latitude + ', ' + request.longitude }}</td>
                                <td>
                                    {{ request.reporter.name && request.reporter.surname 
                                        ? 'Name: ' + request.reporter.name + ' ' + request.reporter.surname 
                                        : 'Name: Not provided' }}
                                </td>
                                <td>
                                    <button
                                        v-if="task.requestID !== request.requestId"
                                        class="btn btn-primary btn-sm"
                                        @click.prevent="selectRequest(request)"
                                    >
                                        Select
                                    </button>
                                    <button
                                        v-else
                                        class="btn btn-danger btn-sm"
                                        @click.prevent="removeRequest"
                                    >
                                        Remove
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <!-- Przyciski do nawigacji między stronami -->
                    <div class="d-flex justify-content-between mt-3">
                        <button
                            class="btn btn-secondary"
                            @click="prevPageRequest"
                            :disabled="currentPageRequest === 0"
                        >
                            Previous
                        </button>
                        <button
                            class="btn btn-secondary"
                            @click="nextPageRequest"
                            :disabled="currentPageRequest === totalPagesRequest - 1"
                        >
                            Next
                        </button>
                    </div>
                </div>
                <div v-if="errors.request" class="text-danger small mt-2">{{ errors.request }}</div>
            </div>



              <div class="mb-3 mt-10">
                <h1 class="card-title mx-auto text-center">Choose volunteers</h1>
                <div v-if="allVolunteers.length > 0">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Login</th>
                                <th>Email</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            
                            <tr v-for="volunteer in paginatedVolunteers" :key="volunteer.username">
                                <td>{{ volunteer.name && volunteer.surname ? volunteer.name + ' ' + volunteer.surname : 'No data given' }}</td>
                                <td>{{ volunteer.username }}</td>
                                <td>{{ volunteer.email }}</td>
                                <td>
                                    <button
                                        v-if="!isVolunteerSelected(volunteer.username)"
                                        class="btn btn-primary btn-sm"
                                        @click.prevent="selectVolunteer(volunteer)"
                                    >
                                        Select
                                    </button>
                                    <button
                                        v-else
                                        class="btn btn-danger btn-sm"
                                        @click.prevent="removeVolunteer(volunteer)"
                                    >
                                        Remove
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <!-- Przyciski do nawigacji między stronami -->
                    <div class="d-flex justify-content-between mt-3">
                        <button 
                            class="btn btn-secondary" 
                            @click.prevent="prevPageVolunteer"
                            :disabled="currentPageVolunteer === 0"
                        >
                            Previous
                        </button>
                        <button 
                            class="btn btn-secondary" 
                            @click.prevent="nextPageVolunteer"
                            :disabled="currentPageVolunteer === totalPagesVolunteer - 1"
                        >
                            Next
                        </button>
                    </div>
                </div>
                <div v-if="errors.volunteers" class="text-danger small mt-2">{{ errors.volunteers }}</div>
            </div>

            <div class="mb-3 mt-10">
                <h1 class="card-title mx-auto text-center">Choose resources</h1>

                <!-- Dodane pola do wyszukiwania -->
                <div class="row mb-3">
                    <div class="col-md-4">
                        <input
                            type="text"
                            v-model="searchResourceQuery"
                            class="form-control"
                            placeholder="Search by resource name"
                        />
                    </div>
                    <div class="col-md-4">
                        <select v-model="selectedResourceType" class="form-control">
                            <option value="">Filter by type</option>
                            <option v-for="type in uniqueResourceTypes" :key="type" :value="type">{{ type }}</option>
                        </select>
                    </div>
                </div>

                <div v-if="allResources.length > 0">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Resource Name</th>
                                <th>Description</th>
                                <th>Type</th>
                                <th>Location</th>
                                <th>Available quantity</th>
                                <th>Quantity to Assign</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="resource in filteredResources" :key="resource.id">
                                <td>{{ resource.name || 'No name provided' }}</td>
                                <td>{{ resource.description }}</td>
                                <td>{{ resource.resourceType }}</td>
                                <td>{{ resource.latitude + ' ' + resource.longitude }}</td>
                                <td>{{ resource.quantity + ' ' + resource.unit }}</td>
                                <td>
                                    <input 
                                        v-if="!isResourceSelected(resource.id)" 
                                        type="number" 
                                        v-model.number="resource.selectedQuantity" 
                                        min="1" 
                                        :max="resource.quantity" 
                                        class="form-control form-control-sm" 
                                        placeholder="Enter quantity"/>
                                    <span v-else>{{ getResourceQuantity(resource.id) }}</span>
                                </td>
                                <td>
                                    <button
                                        v-if="!isResourceSelected(resource.id)"
                                        class="btn btn-primary btn-sm"
                                        @click.prevent="selectResource(resource)"
                                        :disabled="!resource.selectedQuantity || resource.selectedQuantity <= 0"
                                    >
                                        Select
                                    </button>
                                    <button
                                        v-else
                                        class="btn btn-danger btn-sm"
                                        @click.prevent="removeResource(resource)"
                                    >
                                        Remove
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div class="d-flex justify-content-between mt-3">
                        <button 
                            class="btn btn-secondary" 
                            @click.prevent="prevPageResource"
                            :disabled="currentPageResource === 0"
                        >
                            Previous
                        </button>
                        <button 
                            class="btn btn-secondary" 
                            @click.prevent="nextPageResource"
                            :disabled="currentPageResource === totalPagesResource - 1"
                        >
                            Next
                        </button>
                    </div>
                </div>
                <div v-if="errors.resources" class="text-danger small mt-2">{{ errors.resources }}</div>
            </div>




  
              <div class="d-flex justify-content-center mt-4">
                <button type="submit" class="btn btn-primary w-100">{{$t('task-submit')}}</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import TaskService from '../services/task.service';
  import UserService from '../services/user.service';
  import RequestService from '../services/request.service';
  import ResourceService from '../services/resource.service';
import { all } from 'axios';
  
  export default {
    name: 'TaskCreate',
    data: function () {
      return {
        task: {
          task: {
            title: '',
            description: '',
            location: '',
            priority: '',
            organization: {},
          },
          requestID: '',
          resources: [],
          volunteers: [],
        },
        errors: {},
        allVolunteers: [],
        allRequests: [],
        allResources: [],
        searchResourceQuery: '',
        selectedResourceType: '',
        searchRequestQuery: '',
        selectedRequestType: '', 
        currentPageVolunteer: 0,
        pageSizeVolunteer: 10,

        currentPageRequest: 0,
        pageSizeRequest: 10,

        currentPageResource: 0,
        pageSizeResource: 10,
      };
    },
    async created() {
      await this.fetchOrganizationInfo();
      this.fetchRequests();
      this.fetchVolunteers();
      this.fetchResources();
    },
    computed: {
        paginatedVolunteers() {
            const start = this.currentPageVolunteer * this.pageSizeVolunteer;
            const end = start + this.pageSizeVolunteer;
            return this.allVolunteers.slice(start, end);
        },
        
        totalPagesVolunteer() {
            return Math.ceil(this.allVolunteers.length / this.pageSizeVolunteer);
        },

        paginatedRequests() {
            const start = this.currentPageRequest * this.pageSizeRequest;
            const end = start + this.pageSizeRequest;
            return this.allRequests.slice(start, end);
            },
            
        totalPagesRequset() {
        return Math.ceil(this.allRequests.length / this.pageSizeRequest);
        },

        paginatedResources() {
            const start = this.currentPageResource * this.pageSizeResource;
            const end = start + this.pageSizeResource;
            return this.allResources.slice(start, end);
        },
        totalPagesResource() {
            return Math.ceil(this.allResources.length / this.pageSizeResource);
        },
        uniqueResourceTypes() {
        return [...new Set(this.allResources.map(resource => resource.resourceType))];
    },
    
    filteredResources() {
        let filtered = this.allResources;

        // Filtrujemy po nazwie
        if (this.searchResourceQuery) {
            filtered = filtered.filter(resource =>
                resource.name.toLowerCase().includes(this.searchResourceQuery.toLowerCase())
            );
        }

        // Filtrujemy po typie
        if (this.selectedResourceType) {
            filtered = filtered.filter(resource =>
                resource.resourceType === this.selectedResourceType
            );
        }

        return filtered;
    },
    uniqueRequestTypes() {
        return [...new Set(this.allRequests.map(request => request.resourceType))];
    },
    // Filtrowanie requestów na podstawie nazwy i typu
    filteredRequests() {
        let filtered = this.allRequests;

        // Filtrujemy po nazwie zasobu
        if (this.searchRequestQuery) {
            filtered = filtered.filter(request =>
                request.resourceName.toLowerCase().includes(this.searchRequestQuery.toLowerCase())
            );
        }

        // Filtrujemy po typie zasobu
        if (this.selectedRequestType) {
            filtered = filtered.filter(request =>
                request.resourceType === this.selectedRequestType
            );
        }

        return filtered;
    },
    },
    methods: {
      async fetchOrganizationInfo() {
        try {
          const response = await UserService.getOrganizationInfo();
          this.task.task.organization = response.data;
        } catch (error) {
          console.error('Error fetching organization info:', error);
        }
      },
      async fetchVolunteers() {
        try {
            const response = await UserService.getVolunteersInOrganization();
            this.allVolunteers = response.data.filter(volunteer => volunteer.activity === false);

        } catch (error) {
            this.error = 'Failed to load volunteers';
            console.error('Error fetching volunteers:', error);
        }
      },
      async fetchRequests() {
        try {
            const response = await RequestService.getAllRequests();
            this.allRequests = response.data.filter(request => request.status === 'REGISTERED');
        } catch (error) {
            console.error('Error fetching requests:', error);
        }
    },
    async fetchResources() {
        try {
        const organisationId = this.task.task.organization.id;  // Przyjmując, że organizacja ma ID
        const response = await ResourceService.getOrganisationAvailableResources(this.task.task.organization.id);
        this.allResources = response.data;  // Przypisanie danych do allResources
        console.log(this.allResources);
        } catch (error) {
        console.error('Error fetching resources:', error);
        }
    },

      prevPageVolunteer() {
            if (this.currentPageVolunteer > 0) {
                this.currentPageVolunteer--;
            }
        },
        nextPageVolunteer() {
            if (this.currentPageVolunteer < this.totalPagesVolunteer - 1) {
                this.currentPageVolunteer++;
            }
        },


        prevPageRequest() {
            if (this.currentPageRequest > 0) {
                this.currentPageRequest--;
            }
        },
        nextPageRequest() {
            if (this.currentPageRequest < this.totalPagesRequset - 1) {
                this.currentPageRequest++;
            }
        },

        prevPageResource() {
            if (this.currentPageResource > 0) {
            this.currentPageResource--;
            }
        },
        nextPageResource() {
            if (this.currentPageResource < this.totalPagesResource - 1) {
            this.currentPageResource++;
            }
        },

        isResourceSelected(resourceId) {
            return this.task.resources.some(resource => resource.id === resourceId);
        },
        getResourceQuantity(resourceId) {
            const selectedResource = this.task.resources.find(resource => resource.id === resourceId);
            return selectedResource ? selectedResource.quantity : '';
        },
        selectResource(resource) {
            this.errors.resources = '';
            if (resource.selectedQuantity && resource.selectedQuantity > 0 && resource.selectedQuantity <= resource.quantity) {
                this.task.resources.push({ id: resource.id, quantity: resource.selectedQuantity });
                resource.selectedQuantity = null; 
            }else{
                this.errors.resources = 'Wybrana ilość jest nieprawidłowa.';
            }
            
            console.log('Selected resources:', this.task.resources);
        },
        removeResource(resource) {
            this.task.resources = this.task.resources.filter(r => r.id !== resource.id);
            console.log('Selected resources:', this.task.resources);
        },



        isRequestSelected(request) {
            return this.task.requestID === request.requestId;
        },

        selectRequest(request) {
            this.task.requestID = request.requestId;
        },

        removeRequest() {
            this.task.requestID = '';
        },


      isVolunteerSelected(username) {
            return this.task.volunteers.includes(username);
        },

        // Funkcja do wyboru wolontariusza
        selectVolunteer(volunteer) {
            if (!this.isVolunteerSelected(volunteer.username)) {
            this.task.volunteers.push(volunteer.username);
            }
        },

        // Funkcja do usuwania wolontariusza
        removeVolunteer(volunteer) {
            this.task.volunteers = this.task.volunteers.filter(
            (username) => username !== volunteer.username
            );
        },
      goBack() {
        this.$router.go(-1);
      },
      async submitForm() {
        this.errors = {};
  
        // Sprawdzanie pola tytuł
        if (!this.task.task.title || this.task.task.title.trim() === '') {
          this.errors.title = 'Title cannot be empty.';
        }
  
        // Sprawdzanie opisu
        if (!this.task.task.description || this.task.task.description.trim() === '') {
          this.errors.description = 'Description cannot be empty.';
        }
  
        // Sprawdzanie lokalizacji
        if (!this.task.task.location || this.task.task.location.trim() === '') {
          this.errors.location = 'Location cannot be empty.';
        }
  
        // Sprawdzanie priorytetu
        if (!this.task.task.priority || this.task.task.priority.trim() === '') {
          this.errors.priority = 'Priority has to be chosen.';
        }

         // Sprawdzanie wolontariuszy
        if (this.task.volunteers.length === 0) {
        this.errors.volunteers = 'At least one volunteer must be selected.';
        }

        // Sprawdzanie requestów
        if (!this.task.requestID) {
        this.errors.request = 'At least one request must be selected.';
        }


        console.log('Creating task:', this.task.resources.length);
        // Sprawdzanie zasobów
        if (!this.task.resources || this.task.resources.length === 0) {
        this.errors.resources = 'At least one resource must be provided.';
        }
        
        console.log('Creating task:', this.task);

        // Jeśli są błędy, nie kontynuuj
        if (Object.keys(this.errors).length > 0) {
          console.log(this.errors);
          return;
        }
  
        
        // const createTaskDTO = {
        // title: this.task.task.title,
        // description: this.task.task.description,
        // location: this.task.task.location,
        // priority: this.task.task.priority,
        // organization: this.task.task.organization,
        // requestID: this.task.task.requestID,
        // resources: this.task.task.resources,
        // volunteers: this.task.volunteers,
        // };

        try {
        const response = await TaskService.createTask(this.task);
        console.log('Task created successfully:', response.data);
        this.$router.push(`/tasks`);
        // Możesz dodać przekierowanie lub inne akcje po utworzeniu zadania
        } catch (error) {
        console.error('Error creating task:', error);
        this.errors.general = 'Failed to create task. Please try again later.';
        } 
      },

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
  