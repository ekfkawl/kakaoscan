import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import store from '../../redux/store';
import { setToken, setUser } from '../../redux/slices/authSlice';
import { ApiResponse } from '../../types/apiResponse';
import { useHttp } from '../useHttp';

interface LoginRequest {
    email: string;
    password: string;
}

interface UseLoginReturn {
    login: (loginData: LoginRequest) => Promise<void>;
    isLoading: boolean;
    error: string;
}

const useLogin = (): UseLoginReturn => {
    const { sendRequest, data, isLoading, error } = useHttp<ApiResponse>();
    const navigate = useNavigate();

    const login = async (loginData: LoginRequest): Promise<void> => {
        await sendRequest({
            url: '/api/login',
            method: 'POST',
            data: loginData,
        });
    };

    useEffect(() => {
        if (data && data.success) {
            store.dispatch(setToken(data.data.accessToken));
            store.dispatch(setUser(data.data.userData));
            navigate('/');
        }
    }, [data, navigate]);

    return { login, isLoading, error };
};

export default useLogin;
