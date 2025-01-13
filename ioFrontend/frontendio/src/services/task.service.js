import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/test/';

class TaskService {
    getAllTasks() {
      return axios.get(API_URL + 'getAllTasks', { 
        headers: authHeader() });
    }
    getTask(id) {
      return axios.get(API_URL + 'getTask', { 
        params: { id: id },
        headers: authHeader() });
    }
  }
  
  export default new TaskService();
  