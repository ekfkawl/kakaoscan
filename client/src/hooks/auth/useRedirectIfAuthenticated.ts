import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useAuth from './useAuth';

const useRedirectIfAuthenticated = (redirectPath: string) => {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();

    useEffect(() => {
        if (isAuthenticated) {
            navigate(redirectPath);
        }
    }, [isAuthenticated, navigate, redirectPath]);
};

export default useRedirectIfAuthenticated;
