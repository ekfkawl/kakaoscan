import axiosInstance from '../api/axiosInstance';
import store from '../../redux/store';
import { clearToken, setToken, setUser } from '../../redux/slices/authSlice';
import { ApiResponse } from '../../types/apiResponse';

export const refreshToken = async (): Promise<void> => {
    try {
        const res = await axiosInstance.post<ApiResponse>('/api/refresh-token');

        store.dispatch(setToken(res.data.data.accessToken));
        store.dispatch(setUser(res.data.data.userData));
    } catch (error) {
        console.error('error refreshing token: ', error);
        store.dispatch(clearToken());
    }
};
