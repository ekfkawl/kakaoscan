import { AxiosResponse } from 'axios';
import { useState } from 'react';
import axiosInstance from '../../utils/api/axiosInstance';
import store from '../../redux/store';
import { setToken } from '../../redux/slices/authSlice';
import { ApiResponse } from '../../types/apiResponse';

interface LoginRequest {
    email: string;
    password: string;
}

const useLogin = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');

    const login = async (loginData: LoginRequest): Promise<ApiResponse | null> => {
        setIsLoading(true);
        setError('');

        try {
            const res: AxiosResponse<ApiResponse> = await axiosInstance.post(
                'api/login',
                loginData,
            );

            setIsLoading(false);
            setError(res.data.message || '');

            store.dispatch(setToken(res.data.data?.accessToken));

            return res.data;
        } catch (error) {
            setIsLoading(false);
            setError('로그인 중 오류가 발생했습니다.');

            return null;
        }
    };

    return { login, isLoading, error };
};

export default useLogin;
