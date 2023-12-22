import React from 'react';
import { Outlet } from 'react-router-dom';
import HeaderWithUser from '../Header/HeaderWithUser';
import Header from '../Header/Header';
import useAuth from '../../hooks/auth/useAuth';

const AppLayout = () => {
    const { isAuthenticated } = useAuth();

    return (
        <div className="relative bg-white dark:bg-gray-900">
            {!isAuthenticated && (
                <div>
                    <Header />
                    <Outlet />
                </div>
            )}
            {isAuthenticated && (
                <div className="mx-auto max-w-8xl w-full px-3 py-3">
                    <HeaderWithUser />
                    <div className="px-2 py-14 sm:py-14 md:py-14 lg:py-24 xl:py-40">
                        <Outlet />
                    </div>
                </div>
            )}

        </div>
    );
};

export default AppLayout;
