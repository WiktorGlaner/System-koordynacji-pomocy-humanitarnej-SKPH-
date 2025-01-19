import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/';

class ResourceService {
    getAllResources() {
        return axios.get(API_URL + 'resource', { headers: authHeader() });
    }

    getOrganisationResources(organisationId) {
        return axios.get(API_URL + `resource?organisationId=${organisationId}`, { headers: authHeader() });
    }

    getOrganisationAvailableResources(organisationId) {
        return axios.get(API_URL + `resource?organisationId=${organisationId}&status=AVAILABLE`, { headers: authHeader() });
    }
    
    getDonorResources(donorId) {
        return axios.get(API_URL + `donation/${donorId}`, { headers: authHeader() });
    }

    addResource(resource) {
        return axios.post(API_URL + 'resource', resource, { headers: authHeader() });
    }

    async addDonation(donation) {
        return axios.post(API_URL + 'donation', donation, { headers: authHeader() });
    }

    editResource(resourceParams, resourceId) {
        return axios.patch(API_URL + `resource/${resourceId}`, resourceParams, { headers: authHeader() });
    }

    getResource(resourceId) {
        return axios.get(API_URL + `resource/${resourceId}`, { headers: authHeader() });
    }

    async getTotalAssignedQuantity() {
        const response = await axios.get(API_URL + 'assignments', {headers: authHeader()});
        const assignments = response.data;

        return assignments.reduce((acc, assignment) => {
            const resourceId = assignment.resource.id;
            if (!acc[resourceId]) {
                acc[resourceId] = 0;
            }
            acc[resourceId] += assignment.assignedQuantity;
            return acc;
        }, {});
    }

    deleteResource(id) {
        return axios.delete(API_URL + `resource/${id}`, { headers: authHeader() });
    }

    getResourceUsage(id) {
        return axios.get(API_URL + `assignments/${id}`, { headers: authHeader() });
    }
}

export default new ResourceService();