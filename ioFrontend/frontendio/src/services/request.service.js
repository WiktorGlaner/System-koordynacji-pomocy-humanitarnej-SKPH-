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
}

export default new RequestService();