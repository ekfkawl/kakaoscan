import { Button, Tabs } from 'flowbite-react';
import React, { useEffect, useState } from 'react';
import LearnMore from '../components/LearnMore';
import { useHttp } from '../hooks/useHttp';
import MessagePopup from '../components/Popup/MessagePopup';
import { ApiResponse } from '../types/apiResponse';
import { useNavigate } from 'react-router-dom';

const ShopPage = () => {
    const navigate = useNavigate();

    const [activeTab, setActiveTab] = useState(0);
    const { sendRequest, data, isLoading, error } = useHttp<ApiResponse>();
    const [showPaymentMessage, setShowPaymentMessage] = useState(false);

    const pointPayment = async () => {
        if (isLoading) {
            return;
        }

        const amounts = [1500, 3000, 5000];
        const amount = amounts[activeTab];

        await sendRequest({
            url: '/api/payment',
            method: 'POST',
            data: JSON.stringify({
                type: 'pointPayment',
                amount,
            }),
        });
    };

    const snapshotPreservation = async () => {
        if (isLoading) {
            return;
        }

        const amount = 10000;

        await sendRequest({
            url: '/api/payment',
            method: 'POST',
            data: JSON.stringify({
                type: 'snapshotPreservationPayment',
                amount,
            }),
        });
    };

    useEffect(() => {
        if (data && data.success) {
            navigate('/payment-history');
        }
        if (error) {
            setShowPaymentMessage(true);
        }
    }, [data, error]);

    return (
        <div className="mx-auto max-w-screen-lg">
            <div className="bg-white rounded-lg divide-y divide-gray-200 shadow dark:divide-gray-700 lg:divide-y-0 lg:divide-x lg:grid lg:grid-cols-3 dark:bg-gray-800">
                <div className="col-span-2 p-6 lg:p-8">
                    <Tabs style="default" onActiveTabChange={(tab) => setActiveTab(tab)}>
                        <Tabs.Item active title="1,500 P">
                            <AboutPoint />
                        </Tabs.Item>
                        <Tabs.Item title="3,000 P">
                            <AboutPoint />
                        </Tabs.Item>
                        <Tabs.Item title="5,000 P">
                            <AboutPoint />
                        </Tabs.Item>
                        <Tabs.Item title="스냅샷 보존권 (30일)">
                            <AboutSnapshotPreservation />
                        </Tabs.Item>
                    </Tabs>
                </div>
                <div className="flex p-6 lg:p-8">
                    <div className={`self-center w-full ${activeTab === 0 ? '' : 'hidden'}`}>
                        <div className="text-gray-500 dark:text-gray-400">가격</div>
                        <div className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white">1,500원</div>
                        <BuyProduct productPayment={pointPayment} />
                    </div>
                    <div className={`self-center w-full ${activeTab === 1 ? '' : 'hidden'}`}>
                        <div className="text-gray-500 dark:text-gray-400">가격</div>
                        <div className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white">3,000원</div>
                        <BuyProduct productPayment={pointPayment} />
                    </div>
                    <div className={`self-center w-full ${activeTab === 2 ? '' : 'hidden'}`}>
                        <div className="text-gray-500 dark:text-gray-400">가격</div>
                        <div className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white">5,000원</div>
                        <BuyProduct productPayment={pointPayment} />
                    </div>
                    <div className={`self-center w-full ${activeTab === 3 ? '' : 'hidden'}`}>
                        <div className="text-gray-500 dark:text-gray-400">가격</div>
                        <div className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white">10,000원</div>
                        <BuyProduct productPayment={snapshotPreservation} />
                    </div>
                </div>
            </div>
            {showPaymentMessage && (
                <MessagePopup
                    show={showPaymentMessage}
                    onClose={() => setShowPaymentMessage(false)}
                    title="알림"
                    description={error}
                    onConfirm={() => navigate('/payment-history')}
                />
            )}
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

const AboutSnapshotPreservation: React.FC = () => (
    <div>
        <div className="mt-6 mb-2 font-medium text-gray-900 dark:text-white">스냅샷 보존권은 무엇인가요?</div>
        <p className="text-gray-500 dark:text-gray-400 mb-2 break-all">
            스냅샷 보존권 보유 기간 동안 모든 프로필 조회 내역이 만료되지 않습니다.
            일반적으로 프로필 조회 결과는 조회 시점으로부터 48시간 동안만 조회 내역에 저장됩니다.
        </p>
        <LearnMore to="/policy" text="자세히 알아보기" />
    </div>
);

const BuyProduct: React.FC<{ productPayment: () => Promise<void> }> = ({ productPayment }) => (
    <div>
        <Button fullSized color="blue" className="my-4 inline-flex" onClick={() => productPayment()}>
            지금 구매
        </Button>
        <p className="text-sm text-gray-500 dark:text-gray-400 break-all">
            계좌이체를 통해서만 구매가 가능합니다. 입금 완료 후 1분 이내에 주문이 자동으로 처리됩니다.
        </p>
    </div>
);

export default ShopPage;
