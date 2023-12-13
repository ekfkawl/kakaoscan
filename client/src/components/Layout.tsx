import React from 'react';
import { Outlet } from 'react-router-dom';
import HeaderWithUser from './HeaderWithUser';
import Header from './Header';
import useAuth from '../hooks/useAuth';

const Layout = () => {
    const { isAuthenticated } = useAuth();

    return (
        <div className="relative">
            {!isAuthenticated && <Header />}
            {isAuthenticated && (
                <div className="absolute inset-0 z-50 mx-auto max-w-8xl">
                    <HeaderWithUser />
                </div>
            )}
            <Outlet />
        </div>
    );
};

export default Layout;
