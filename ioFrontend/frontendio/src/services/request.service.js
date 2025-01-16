import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/request/';


class RequestService {
    types = [
        { FOOD: 'Jedzenie'},
        { TRANSPORT: 'Transport' },
        { CLOTHING: 'Ubrania' },
        { MEDICAL: 'Pomoc medyczna' },
        { FINANCIAL: 'Pomoc finansowa' },
        { EQUIPMENT: 'Wyposażenie' },
        { HOUSING: 'Dom' },
        { OTHER: 'Inne' }
    ]

    statuses = [
        { REGISTERED: 'Zarejestrowane' },
        { IN_PROGRESS: 'W toku' },
        { COMPLETED: 'Ukończone' }
    ]

    getAllRequests() {
        return axios.get(API_URL + 'requests', { headers: authHeader() });
    }

    deleteRequest(username, id) {
        return axios.delete(API_URL + username + '/requests/deleterequest/' + id );
    }

    getRequestTypes() {
        return axios.get("http://localhost:8080/resourceTypes");
    }

    addRequest(username, newRequestData) {
        return axios.post(API_URL + username + "/requests/addrequest", newRequestData, { headers: authHeader() });
    }

    getRequestsByUser(username) {
        return axios.get(API_URL + username + "/requests");
    }

    changeRequest(username, newRequestData, id) {
        return axios.put(API_URL + username + '/requests/changerequest/' + id, newRequestData), { headers: authHeader() };
    }
}

export default new RequestService();