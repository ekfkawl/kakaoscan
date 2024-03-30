import { useCallback, useEffect, useState } from 'react';
import axiosInstance from '../utils/api/axiosInstance';

export const useFetchData = <T>(endpoint: string, initialState: T, autoFetch = true) => {
    const [data, setData] = useState<T>(initialState);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const fetchData = useCallback(
        async (queryParameters?: Record<string, string | number>) => {
            setIsLoading(true);
            try {
                const query = queryParameters ? `?${new URLSearchParams(queryParameters as any).toString()}` : '';
                const response = await axiosInstance.get(`${endpoint}${query}`);
                setData(response.data);
                setIsLoading(false);
            } catch (error) {
                setError('데이터를 불러오는 중 오류가 발생했습니다.');
                setIsLoading(false);
            }
        },
        [endpoint],
    );

    useEffect(() => {
        if (autoFetch) {
            fetchData();
        }
    }, [fetchData, autoFetch]);

    return { data, isLoading, error, fetchData };
};
