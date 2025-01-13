<template>
    <div>
        {{ task.task.id }}
    </div>
</template>

<script>
import TaskService from '../services/task.service';

export default {
    props: ['id'],
    name: 'Task',
    data: function() {
        return {
            task: [],
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
  },
}
</script>

<style scoped>
</style>