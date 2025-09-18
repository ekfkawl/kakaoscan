import React, { useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import HeaderWithUser from './Header/HeaderWithUser';
import Header from './Header/Header';
import useAuth from '../hooks/auth/useAuth';
import { WebSocketProvider } from '../providers/StompProvider';
import ChannelService from '../ChannelService';
import useUser from '../hooks/auth/useUser';
import { enc, HmacSHA256 } from 'crypto-js';

const AppLayout = () => {
    const { isAuthenticated } = useAuth();
    const user = useUser();

    useEffect(() => {
        if (isAuthenticated) {
            ChannelService.loadScript();
            ChannelService.boot({
                pluginKey: process.env.REACT_APP_CHANNEL_PLUGIN_KEY || '',
                memberId: user?.email,
                memberHash: HmacSHA256(
                    user?.email || '',
                    enc.Hex.parse(process.env.REACT_APP_CHANNEL_USER_SECRET_KEY || ''),
                ).toString(),
                profile: {
                    name: user?.email,
                    email: user?.email,
                },
                language: 'ko',
                zIndex: 50,
            });
        }else {
            ChannelService.shutdown();
        }
    }, [isAuthenticated, user?.email]);

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
                    <WebSocketProvider>
                        <HeaderWithUser />
                        <div className="px-2 py-14 sm:py-14 md:py-14 lg:py-24 xl:py-40">
                            <Outlet />
                        </div>
                    </WebSocketProvider>
                </div>
            )}
        </div>
    );
};

export default AppLayout;
