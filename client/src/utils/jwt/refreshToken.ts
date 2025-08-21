import axiosInstance from '../api/axiosInstance';
import store from '../../redux/store';
import { clearToken, setToken, setUser } from '../../redux/slices/authSlice';
import { ApiResponse } from '../../types/apiResponse';
import {hasRefreshHint} from "../web/cookie";

export const refreshToken = async (): Promise<void> => {
    if (!hasRefreshHint()) {
        store.dispatch(clearToken());
        return;
    }

    try {
        const res = await axiosInstance.post<ApiResponse>(
            '/api/refresh-token',
            {},
            { validateStatus: s => s === 200 || s === 401 }
        );

        if (res.status === 200) {
            store.dispatch(setToken(res.data.data.accessToken));
            store.dispatch(setUser(res.data.data.userData));
        } else {
            store.dispatch(clearToken());
        }
    } catch (error) {
        console.debug('silent refresh failed (network): ', error);
        store.dispatch(clearToken());
    }
};
