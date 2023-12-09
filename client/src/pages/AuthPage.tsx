import React, { PropsWithChildren } from 'react';
import { Avatar } from "flowbite-react";

const AuthPage: React.FC<PropsWithChildren<{}>> = ({ children }) => {
    return (
        <section className="bg-white dark:bg-gray-900">
            <div className="grid lg:h-screen lg:grid-cols-2">
                <div className="flex items-center justify-center bg-[#24292F] px-4 py-6 sm:px-0 lg:py-0">
                    <div className="max-w-md xl:max-w-xl">
                        <a
                            href="#"
                            className="mb-4 flex items-center text-2xl quicksand text-white"
                        >
                            <img
                                className="mr-2 h-8 w-8"
                                src="/favicon.png"
                                alt="logo"
                            />
                            kakaoscan
                        </a>
                        <h1 className="mb-4 text-3xl font-extrabold leading-none tracking-tight text-white xl:text-5xl">
                            View and download kakaotalk profile
                        </h1>
                        <p className="mb-4 text-primary-200 lg:mb-8">
                            카카오스캔은 클라우드 인스턴스 환경에서 카카오톡 프로필을 대신 조회해주는 서비스입니다.
                        </p>
                    </div>
                </div>
                <div className="flex items-center justify-center px-4 py-6 sm:px-0 lg:py-0">
                    {children}
                </div>
            </div>
        </section>
    );
};

export default AuthPage;
