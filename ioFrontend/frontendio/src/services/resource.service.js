import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/';

class ResourceService {
    getAllResources() {
        return axios.get(API_URL + 'resource', { headers: authHeader() });
    }

    addResource(resource) {
        return axios.post(API_URL + 'resource', resource, { headers: authHeader() });
    }
}

export default new ResourceService();