import axiosInstance from './axiosInstance';
import store from '../redux/store';
import { clearToken, setToken } from '../redux/slices/authSlice';
import { ApiResponse } from '../types/apiResponse';

export const refreshToken = async (): Promise<void> => {
    try {
        const response = await axiosInstance.post<ApiResponse>('/api/refresh-token');
        const newAccessToken = response.data.data.accessToken;

        store.dispatch(setToken(newAccessToken));
    } catch (error) {
        console.error('error refreshing token: ', error);
        store.dispatch(clearToken());
    }
};
