import { useEffect, useState } from 'react';

const useRememberUserId = () => {
    const [userId, setUserId] = useState(() => {
        const savedRemember = localStorage.getItem('remember');
        return savedRemember === 'true' ? localStorage.getItem('rememberUserId') || '' : '';
    });
    const [remember, setRemember] = useState(() => {
        const savedRemember = localStorage.getItem('remember');
        return savedRemember === 'true';
    });

    useEffect(() => {
        const savedUserId = localStorage.getItem('rememberUserId');
        if (savedUserId) {
            setUserId(savedUserId);
        }
    }, []);

    useEffect(() => {
        if (remember) {
            localStorage.setItem('rememberUserId', userId);
            localStorage.setItem('remember', 'true');
        } else {
            localStorage.removeItem('rememberUserId');
            localStorage.setItem('remember', 'false');
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
