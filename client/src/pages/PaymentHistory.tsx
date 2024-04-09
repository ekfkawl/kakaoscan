import React, { useEffect, useState } from 'react';
import { Badge, Button } from 'flowbite-react';
import DateRangePicker from '../components/DateRangePicker';
import { useFetchData } from '../hooks/useFetchData';
import useDateRangePicker from '../hooks/ui/useDateRangePicker';
import { formatDate } from '../utils/format/format';
import { useHttp } from '../hooks/useHttp';
import { ApiResponse } from '../types/apiResponse';
import MessagePopup from '../components/Popup/MessagePopup';
import ConfirmPopup from '../components/Popup/ConfirmPopup';

const PaymentHistory = () => {
    const { start, end, setStart, setEnd } = useDateRangePicker();
    const {
        data: transactionsData,
        isLoading: fetchIsLoading,
        fetchData,
    } = useFetchData<any>('/api/product/transactions', {}, false);

    const { sendRequest, data, isLoading, error } = useHttp<ApiResponse>();
    const cancelTransaction = async (transactionId: number): Promise<void> => {
        try {
            await sendRequest({
                url: '/api/payment',
                method: 'PUT',
                data: {
                    transactionId: transactionId,
                },
            });
            setRefreshFlag(!refreshFlag);
        } catch (error) {
            console.error(error);
        }
    };
    const [refreshFlag, setRefreshFlag] = useState(false);

    const [showMessage, setShowMessage] = useState(false);
    const [showCancelTransactionConfirmPopup, setShowCancelTransactionConfirmPopup] = useState(false);
    const [cancelTransactionId, setCancelTransactionId] = useState(0);

    useEffect(() => {
        fetchData({ startDate: start, endDate: end });
    }, [start, end, fetchData, refreshFlag]);

    useEffect(() => {
        if (error) {
            setShowMessage(true);
        }
    }, [data, error]);

    return (
        <div className="mx-auto max-w-screen-lg">
            <div className="flex justify-end items-center space-x-2 mb-4">
                <DateRangePicker start={start} setStart={setStart} end={end} setEnd={setEnd} />
            </div>

            {fetchIsLoading ? (
                <div></div>
            ) : transactionsData?.data?.productTransactionList?.length > 0 ? (
                <>
                    {transactionsData.data.productTransactionList.map((product: any) => (
                        <div
                            key={product.id}
                            className="bg-white dark:bg-gray-800 p-5 rounded-lg shadow mb-5 divide-y divide-gray-200 dark:divide-gray-700"
                        >
                            <div className="pb-5 flex flex-col text-left">
                                <div className="flex items-center">
                                    <Badge
                                        color={
                                            product.productTransactionStatus === '대기'
                                                ? 'red'
                                                : product.productTransactionStatus === '취소'
                                                  ? 'gray'
                                                  : 'success'
                                        }
                                        className="w-fit px-3"
                                    >
                                        {product.productTransactionStatus}
                                    </Badge>
                                    <div className="text-lg font-semibold text-gray-900 dark:text-white ml-3">
                                        {product.productName}
                                    </div>
                                </div>
                                <div className="text-sm text-gray-500 dark:text-gray-400">
                                    {formatDate(new Date(product.createdAt))}
                                </div>
                            </div>
                            <div className="pt-5 md:flex md:items-center md:justify-between">
                                {product.productTransactionStatus === '대기' && (
                                    <>
                                        <div>
                                            <h4 className="leading-tight tracking-tight text-gray-900 dark:text-white">
                                                입금해야 할 금액:{' '}
                                                <span className="text-indigo-600 dark:text-indigo-400">
                                                    {new Intl.NumberFormat('ko-KR').format(product.amount)}원
                                                </span>
                                            </h4>
                                            <DepositorInfo depositor={product.depositor} />
                                            <p className="mt-3 text-base font-normal text-gray-800 dark:text-white">
                                                {transactionsData.data.account}
                                            </p>
                                            <p className="mt-3 text-sm font-normal text-gray-500 dark:text-gray-400">
                                                <strong>* 중요:</strong> 입금 시, 입금자명을{' '}
                                                <span className="font-semibold">{product.depositor}</span> (으)로 정확히
                                                기재해 주세요.
                                            </p>
                                        </div>
                                        <div className="flex justify-end items-center">
                                            <Button
                                                color="gray"
                                                onClick={() => {
                                                    setCancelTransactionId(product.id);
                                                    setShowCancelTransactionConfirmPopup(true);
                                                }}
                                            >
                                                <svg
                                                    fill="currentColor"
                                                    viewBox="0 0 20 20"
                                                    xmlns="http://www.w3.org/2000/svg"
                                                    className="w-5 h-5 mr-1 -ml-1"
                                                >
                                                    <path
                                                        fillRule="evenodd"
                                                        d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                                                        clipRule="evenodd"
                                                    />
                                                </svg>
                                                취소
                                            </Button>
                                        </div>
                                    </>
                                )}
                                {product.productTransactionStatus !== '대기' && (
                                    <DepositorInfo depositor={product.depositor} />
                                )}
                            </div>
                        </div>
                    ))}
                    {showMessage && (
                        <MessagePopup
                            show={showMessage}
                            onClose={() => setShowMessage(false)}
                            title="알림"
                            description={error}
                            onConfirm={() => setShowMessage(false)}
                        />
                    )}
                    {showCancelTransactionConfirmPopup && (
                        <ConfirmPopup
                            show={showCancelTransactionConfirmPopup}
                            onClose={() => setShowCancelTransactionConfirmPopup(false)}
                            title="알림"
                            description={<p>포인트 충전 신청을 취소하시겠어요?</p>}
                            onConfirm={() => cancelTransaction(cancelTransactionId)}
                        />
                    )}
                </>
            ) : (
                <div className="text-center">
                    <div className="border-t border-gray-200 dark:border-gray-700">
                        <div className="mt-16">
                            <p className="text-gray-500 dark:text-gray-400 mb-2 text-4xl">🤔</p>
                            <p className="text-gray-500 dark:text-gray-400 mb-2">구매 내역이 비어있습니다.</p>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

const DepositorInfo: React.FC<{ depositor: string }> = ({ depositor }) => (
    <h4 className="mt-1 leading-tight tracking-tight text-gray-900 dark:text-white">
        입금자명: <span className="text-indigo-600 dark:text-indigo-400">{depositor}</span>
    </h4>
);

export default PaymentHistory;
