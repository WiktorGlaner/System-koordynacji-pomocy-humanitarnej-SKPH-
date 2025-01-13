import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/test/';

class UserService {
  getPublicContent() {
    return axios.get(API_URL + 'all');
  }

  getUserBoard() {
    return axios.get(API_URL + 'user', { headers: authHeader() });
  }

  getModeratorBoard() {
    return axios.get(API_URL + 'mod', { headers: authHeader() });
  }

  getAdminBoard() {
    return axios.get(API_URL + 'admin', { headers: authHeader() });
  }

  fillUserInformation(requestData) {
    return axios.post(`${API_URL}/uploadUserData`, requestData, {
      headers: authHeader()
    });
  }

  fillOrganizationInformation(requestData) {
    return axios.post(`${API_URL}/uploadOrganizationData`, requestData, {
      headers: authHeader()
    });
  }
}

export default new UserService();
