import React from 'react';
import { Button } from 'flowbite-react';
import { useNavigate } from 'react-router-dom';

const E404Page = () => {
    const navigate = useNavigate();

    return (
        <section className="bg-white dark:bg-gray-900">
            <div className="mx-auto max-w-screen-xl px-4 py-8 lg:px-6 lg:py-16">
                <div className="mx-auto max-w-screen-sm text-center">
                    <h1 className="mb-4 text-7xl font-extrabold tracking-tight text-primary-600 dark:text-primary-500 lg:text-9xl">
                        404
                    </h1>
                    <p className="mb-4 text-3xl font-bold tracking-tight text-gray-900 dark:text-white md:text-4xl">
                        요청하신 페이지를 찾을 수 없습니다.
                    </p>
                    <p className="mb-4 text-lg text-gray-500 dark:text-gray-400">
                        방문하시려는 페이지의 주소가 잘못 입력되었거나, 페이지의 주소가 변경 혹은 삭제되어 요청하신
                        페이지를 찾을 수 없습니다.
                    </p>
                    <Button color="info" className="my-4 inline-flex" onClick={() => navigate('/')}>
                        곱게 돌아가기
                    </Button>
                </div>
            </div>
        </section>
    );
};

export default E404Page;
