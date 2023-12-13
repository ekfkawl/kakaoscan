import { AxiosResponse } from 'axios';
import axiosInstance from '../utils/axiosInstance';
import store from '../redux/store';
import { clearToken } from '../redux/slices/authSlice';
import { ApiResponse } from '../types/apiResponse';

const useLogout = () => {
    const logout = async (): Promise<ApiResponse | null> => {
        try {
            const res: AxiosResponse<ApiResponse> = await axiosInstance.post('api/logout');

            store.dispatch(clearToken());

            return res.data;
        } catch (error) {
            return null;
        }
    };

    return { logout };
};

export default useLogout;
