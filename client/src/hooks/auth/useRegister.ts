import { useHttp } from '../useHttp';
import axios from 'axios';
import { useEffect } from 'react';
import { ApiResponse } from '../../types/apiResponse';

interface RegisterRequest {
    email: string;
    password: string;
}

interface UseRegisterReturn {
    register: (registerData: RegisterRequest) => Promise<void>;
    isLoading: boolean;
    error: string;
    data: any;
}

const useRegister = (): UseRegisterReturn => {
    const { data, isLoading, error, sendRequest, setError } = useHttp<ApiResponse>();

    const register = async (registerData: RegisterRequest) => {
        try {
            await sendRequest({
                url: '/api/register',
                method: 'POST',
                data: registerData,
            });
        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response?.status === 429) {
                    setError('과도한 요청으로 일시적으로 가입이 제한됩니다.');
                }
            }
        }
    };

    useEffect(() => {
        if (data && data.message) {
            setError(data.message);
        }
    }, [data, setError]);

    return { register, isLoading, error, data };
};

export default useRegister;
