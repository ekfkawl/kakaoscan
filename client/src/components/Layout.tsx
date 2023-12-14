import React from 'react';
import { Outlet } from 'react-router-dom';
import HeaderWithUser from './HeaderWithUser';
import Header from './Header';
import useAuth from '../hooks/useAuth';

const Layout = () => {
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
                    <div className="px-2 py-10 sm:py-10 md:py-10 lg:py-20 xl:py-32">
                        <Outlet />
                    </div>
                </div>
            )}

        </div>
    );
};

export default Layout;
