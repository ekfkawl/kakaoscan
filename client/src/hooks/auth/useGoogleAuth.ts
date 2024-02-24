import { useGoogleLogin } from '@react-oauth/google';
import axiosInstance from '../../utils/api/axiosInstance';
import { ApiResponse } from '../../types/apiResponse';
import { useState } from 'react';
import store from '../../redux/store';
import {setToken, setUser} from '../../redux/slices/authSlice';

const useGoogleAuth = () => {
    const [error, setError] = useState<string>('');

    return useGoogleLogin({
        onSuccess: async (response) => {
            try {
                const res = await axiosInstance.post<ApiResponse>('/api/login/oauth2/google', {
                    code: response.access_token,
                });
                setError(res.data.message || '');

                store.dispatch(setToken(res.data.data.accessToken));
                store.dispatch(setUser(res.data.data.userData));

                return res.data;
            } catch (error) {
                console.error('error: ', error);
            }
        },
        onError: () => {
            console.error('로그인 실패');
        },
    });
};

export default useGoogleAuth;
