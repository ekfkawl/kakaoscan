import { useEffect, useState } from 'react';

const useRememberUserId = () => {
    const [userId, setUserId] = useState('');
    const [remember, setRemember] = useState(false);

    useEffect(() => {
        const savedUserId = localStorage.getItem('rememberUserId');
        if (savedUserId) {
            setUserId(savedUserId);
            setRemember(true);
        }
    }, []);

    useEffect(() => {
        if (remember) {
            localStorage.setItem('rememberUserId', userId);
        } else {
            localStorage.removeItem('rememberUserId');
        }
    }, [userId, remember]);

    return {
        userId,
        setUserId,
        remember,
        setRemember,
    };
};

export default useRememberUserId;
