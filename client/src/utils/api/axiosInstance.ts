import axios from 'axios';
import store from '../../redux/store';
import { getAPIBaseURL } from '../web/url';

const axiosInstance = axios.create({
    baseURL: getAPIBaseURL(),
});

axiosInstance.interceptors.request.use((config) => {
    const token = store.getState().auth.token;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    config.withCredentials = true;

    return config;
});

export default axiosInstance;
