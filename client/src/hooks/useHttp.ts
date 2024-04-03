import { useCallback, useEffect, useState } from 'react';
import axios, { AxiosRequestConfig } from 'axios';
import axiosInstance from '../utils/api/axiosInstance';
import { ApiResponse } from '../types/apiResponse';

type RequestConfig = Omit<AxiosRequestConfig, 'url'> & {
    url: string;
};

type AutoFetchOptions = {
    url: string;
    params?: Record<string, any>;
};

type UseHttpReturn<T> = {
    data: T | null;
    isLoading: boolean;
    error: string;
    sendRequest: (config: RequestConfig) => Promise<void>;
    setAutoFetchOptions: (options: AutoFetchOptions) => void;
    setError: (message: string) => void;
};

export const useHttp = <T>(): UseHttpReturn<T> => {
    const [data, setData] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [autoFetchOptions, setAutoFetchOptions] = useState<AutoFetchOptions | null>(null);

    const sendRequest = useCallback(async (config: RequestConfig): Promise<void> => {
        setIsLoading(true);
        setError('');

        try {
            const response = await axiosInstance({
                ...config,
                url: config.url,
                headers: {
                    'Content-Type': 'application/json',
                    ...config.headers,
                },
            });
            setData(response.data);
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                const res = error.response.data as ApiResponse;
                setError(res.message || '데이터를 처리하는 중 오류가 발생했습니다.');
            } else {
                setError('데이터를 처리하는 중 오류가 발생했습니다.');
            }
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        if (autoFetchOptions) {
            sendRequest({
                ...autoFetchOptions,
                method: 'GET',
            });
        }
    }, [sendRequest, autoFetchOptions]);

    return { data, isLoading, error, sendRequest, setAutoFetchOptions, setError };
};
