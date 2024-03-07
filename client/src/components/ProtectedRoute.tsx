import React, { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import useAuth from '../hooks/auth/useAuth';
import { Spinner } from 'flowbite-react';
import { useDispatch } from 'react-redux';
import { refreshToken } from '../utils/jwt/refreshToken';
import { setInitialized } from '../redux/slices/authSlice';

interface ProtectedRouteProps {
    children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
    const dispatch = useDispatch();
    const { isAuthenticated, isInitialized } = useAuth();

    useEffect(() => {
        refreshToken().finally(() => {
            dispatch(setInitialized());
        });
    }, [dispatch]);

    if (!isInitialized) {
        return (
            <div className="flex justify-center items-center h-screen dark:bg-gray-900">
                <Spinner color="success" />
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" />;
    }

    return <>{children}</>;
};

export default ProtectedRoute;
