import { useEffect, useState } from 'react';
import axiosInstance from '../../utils/api/axiosInstance';

const useSearchHistories = () => {
    const [searchHistories, setSearchHistories] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchSearchHistories = async () => {
            setIsLoading(true);
            try {
                const response = await axiosInstance.get('/api/search-histories');
                setSearchHistories(response.data.data.histories);
                setIsLoading(false);
            } catch (error) {
                setError('데이터를 불러오는 중 오류가 발생했습니다.');
                setIsLoading(false);
            }
        };

        fetchSearchHistories();
    }, []);

    return { searchHistories, isLoading, error };
};

export default useSearchHistories;
