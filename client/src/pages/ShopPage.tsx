import { Tabs } from 'flowbite-react';
import React, { useState } from 'react';
import LearnMore from '../components/LearnMore';

const ShopPage = () => {
    const [activeTab, setActiveTab] = useState(0);

    return (
        <div className="mx-auto max-w-screen-lg">
            <div className="bg-white rounded-lg divide-y divide-gray-200 shadow dark:divide-gray-700 lg:divide-y-0 lg:divide-x lg:grid lg:grid-cols-3 dark:bg-gray-800">
                <div className="col-span-2 p-6 lg:p-8">
                    <Tabs style="fullWidth" onActiveTabChange={(tab) => setActiveTab(tab)}>
                        <Tabs.Item active title="500 P">
                            <AboutPoint />
                        </Tabs.Item>
                        <Tabs.Item title="1,000 P">
                            <AboutPoint />
                        </Tabs.Item>
                        <Tabs.Item title="5,000 P">
                            <AboutPoint />
                        </Tabs.Item>
                    </Tabs>
                </div>
                <div className="flex p-6 lg:p-8">
                    <div className={`self-center w-full ${activeTab === 0 ? '' : 'hidden'}`}>
                        <div className="text-gray-500 dark:text-gray-400">가격</div>
                        <div className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white">500원</div>
                        <a
                            href="#"
                            className="flex justify-center text-white bg-primary-600 hover:bg-primary-700 focus:ring-4 focus:ring-bue-200 dark:focus:ring-primary-900 font-medium rounded-lg text-sm px-5 py-2.5 text-center mb-4"
                        >
                            지금 구매
                        </a>
                        <p className="text-sm text-gray-500 dark:text-gray-400">구매는 계좌이체로만 가능합니다.</p>
                        <p className="text-sm text-gray-500 dark:text-gray-400">
                            수작업으로 처리되므로 시간이 다소 걸릴 수도 있는 점 양해부탁드립니다. 늦어도 24시간 이내
                            처리됩니다.
                        </p>
                    </div>
                    <div className={`self-center w-full ${activeTab === 1 ? '' : 'hidden'}`}>
                        <div className="text-gray-500 dark:text-gray-400">가격</div>
                        <div className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white">1,000원</div>
                        <a
                            href="#"
                            className="flex justify-center text-white bg-primary-600 hover:bg-primary-700 focus:ring-4 focus:ring-bue-200 dark:focus:ring-primary-900 font-medium rounded-lg text-sm px-5 py-2.5 text-center mb-4"
                        >
                            지금 구매
                        </a>
                        <p className="text-sm text-gray-500 dark:text-gray-400">구매는 계좌이체로만 가능합니다.</p>
                        <p className="text-sm text-gray-500 dark:text-gray-400">
                            수작업으로 처리되므로 시간이 다소 걸릴 수도 있는 점 양해부탁드립니다. 늦어도 24시간 이내
                            처리됩니다.
                        </p>
                    </div>
                    <div className={`self-center w-full ${activeTab === 2 ? '' : 'hidden'}`}>
                        <div className="text-gray-500 dark:text-gray-400">가격</div>
                        <div className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white">5,000원</div>
                        <a
                            href="#"
                            className="flex justify-center text-white bg-primary-600 hover:bg-primary-700 focus:ring-4 focus:ring-bue-200 dark:focus:ring-primary-900 font-medium rounded-lg text-sm px-5 py-2.5 text-center mb-4"
                        >
                            지금 구매
                        </a>
                        <p className="text-sm text-gray-500 dark:text-gray-400 break-all">구매는 계좌이체로만 가능합니다.</p>
                        <p className="text-sm text-gray-500 dark:text-gray-400 break-all">
                            관리자의 수작업으로 처리되므로 시간이 다소 걸릴 수도 있는 점 양해부탁드립니다. 늦어도 24시간 이내
                            처리됩니다.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
};

const AboutPoint: React.FC = () => (
    <div>
        <div className="mt-6 mb-2 font-medium text-gray-900 dark:text-white">포인트는 무엇인가요?</div>
        <p className="text-gray-500 dark:text-gray-400 mb-2 break-all">
            포인트는 카카오톡 프로필을 조회하는데 사용되는 화폐와 같습니다. 프로필을 조회할 때마다 포인트가 차감되므로,
            포인트를 충전하여 원하는 대상의 프로필을 탐색하실 수 있습니다.
        </p>
        <LearnMore to="/policy" text="자세히 알아보기" />
    </div>
);

export default ShopPage;
