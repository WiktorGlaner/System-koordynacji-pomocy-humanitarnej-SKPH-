import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/test/';

class TaskService {
    getAllTasks() {
      return axios.get(API_URL + 'getAllTasks', { 
        headers: authHeader() });
    }
    getVolunteersTasks(username) {
      return axios.get(API_URL + 'tasks/volunteer', { 
        params: { username: username },
        headers: authHeader() });
    }
    getTask(id) {
      return axios.get(API_URL + 'getTask', { 
        params: { id: id },
        headers: authHeader() });
    }
    createTask(createTaskDTO) {
      return axios.post(API_URL + 'createTask', createTaskDTO, { 
        headers: authHeader() });
    }
    endTask(id) {
      return axios.put(API_URL + 'endTask', null, { 
        params: { id: id },
        headers: authHeader() });
    }
    editTask(id, updatedTask) {
      return axios.put(API_URL + 'editTask', updatedTask, { 
        params: { id: id },
        headers: authHeader() });
    }
    gradeTask(id, grade) {
      return axios.put(API_URL + 'gradeTask', null, { 
        params: { id: id, grade: grade },
        headers: authHeader() });
    }
  }
  
  export default new TaskService();
  