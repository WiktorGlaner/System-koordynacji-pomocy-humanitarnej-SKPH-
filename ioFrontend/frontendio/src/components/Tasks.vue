<template>
  <div class="container mt-5">
    <h1 class="mb-4">Tasks</h1>
    <table class="table table-striped table-bordered">
      <thead class="thead-dark">
        <tr>
          <th>#</th>
          <th v-for="(header, index) in tableHeaders" :key="index">{{ header }}</th>
        </tr>
      </thead>
      <tbody>
        <tr 
          v-for="task in tasks" 
          :key="task.task.id" 
          @click="goToTaskDetails(task.task.id)" 
          class="clickable-row"
        >
          <td>{{ task.task.id }}</td>
          <td>{{ task.task.title }}</td>
          <td>{{ task.task.organization }}</td>
          <td>{{ task.task.location }}</td>
          <td>{{ task.task.priority }}</td>
          <td>{{ task.task.status }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import TaskService from '../services/task.service';

export default {
    name: 'Task',
    data() {
        return {
            tableHeaders: ["Title:", "Organization: ", "Location: ", "Priority: ","Status: "],
            tasks: [],
        };
    },
    created() {
        TaskService.getAllTasks().then(
            (response) => {
                this.tasks = response.data;
                console.log(response.data);        
            },
            (error) => {
                console.log(
                    (error.response &&
                        error.response.data &&
                        error.response.data.message) ||
                    error.message ||
                    error.toString()
                );
            }
        );
    },
    methods: {
    goToTaskDetails(taskId) {
      this.$router.push(`/task/${taskId}`);
    },
  },
}
</script>

<style scoped>
h1 {
    color: #42b983;
}

.clickable-row {
  cursor: pointer;
  transition: background-color 0.2s;
}

.clickable-row:hover {
  background-color: #f1f1f1;
}
</style>