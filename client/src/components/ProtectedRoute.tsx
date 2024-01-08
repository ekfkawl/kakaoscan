import React from 'react';
import { Navigate } from 'react-router-dom';
import useAuth from '../hooks/auth/useAuth';
import { Spinner } from 'flowbite-react';

interface ProtectedRouteProps {
    children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
    const { isAuthenticated, isInitialized } = useAuth();

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
