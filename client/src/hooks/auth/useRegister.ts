import axios, { AxiosError, AxiosResponse } from 'axios';
import { useState } from 'react';
import { ApiResponse } from '../../types/apiResponse';
import axiosInstance from '../../utils/api/axiosInstance';

interface RegisterRequest {
    email: string;
    password: string;
}

const useRegister = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState('');

    const register = async (registerData: RegisterRequest): Promise<ApiResponse | null> => {
        setIsLoading(true);
        setError('');

        try {
            const res: AxiosResponse<ApiResponse> = await axiosInstance.post('/api/register', registerData);

            setIsLoading(false);
            setError(res.data.message || '');

            return res.data;
        } catch (error) {
            setIsLoading(false);

            if (axios.isAxiosError(error)) {
                const axiosError = error as AxiosError<ApiResponse>;
                if (axiosError.response?.status === 429) {
                    setError('과도한 요청으로 일시적으로 가입이 제한됩니다.');
                    return null;
                }
                setError(axiosError.response?.data.message || '회원가입 중 오류가 발생했습니다.');
            } else {
                setError('회원가입 중 오류가 발생했습니다.');
            }

            return null;
        }
    };

    return { register, isLoading, error };
};

export default useRegister;
