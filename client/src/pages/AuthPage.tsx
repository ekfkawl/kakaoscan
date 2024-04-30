import React, { PropsWithChildren, useEffect } from 'react';
import useAuth from '../hooks/auth/useAuth';
import { Navigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { refreshToken } from '../utils/jwt/refreshToken';
import { setInitialized } from '../redux/slices/authSlice';

const AuthPage: React.FC<PropsWithChildren<{}>> = ({ children }) => {
    const dispatch = useDispatch();
    const { isAuthenticated, isInitialized } = useAuth();

    useEffect(() => {
        refreshToken().finally(() => {
            dispatch(setInitialized());
        });
    }, [dispatch]);

    if (isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    return (
        <div className="grid lg:h-screen lg:grid-cols-2 min-h-screen">
            <div className="flex items-center justify-center bg-[#1E283C] px-4 py-6 sm:px-0 lg:py-0">
                <div className="max-w-md xl:max-w-xl">
                    <div className="mb-4 flex items-center text-2xl quicksand text-[#00FFC0]">
                        <img className="mr-2 h-8 w-8" src="/favicon.png" alt="logo" />
                        kakaoscan
                    </div>
                    <h1 className="mb-4 text-3xl font-extrabold leading-none tracking-tight text-white xl:text-5xl">
                        View and download kakaotalk profiles
                    </h1>
                    <p className="mb-4 text-[#CCCCCC] lg:mb-8">
                        카카오스캔은 클라우드 인스턴스 환경에서 카카오톡 프로필을 대신 조회해주는 서비스입니다.
                    </p>
                </div>
            </div>
            <div className="flex items-center justify-center px-4 py-6 sm:px-0 lg:py-0">{children}</div>
        </div>
    );
};

export default AuthPage;
