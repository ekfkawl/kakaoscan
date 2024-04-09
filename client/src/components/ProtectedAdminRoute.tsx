import React from 'react';
import { Navigate } from 'react-router-dom';
import useUser from '../hooks/auth/useUser';

interface ProtectedAdminRouteProps {
    children: React.ReactNode;
}

const ProtectedAdminRoute: React.FC<ProtectedAdminRouteProps> = ({ children }) => {
    const user = useUser();

    if (!user || user.role !== 'ADMIN') {
        return <Navigate to="/login" replace />;
    }

    return <>{children}</>;
};

export default ProtectedAdminRoute;
