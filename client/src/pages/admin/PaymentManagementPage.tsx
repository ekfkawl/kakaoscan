import React, { useEffect, useState } from 'react';
import DateRangePicker from '../../components/DateRangePicker';
import useDateRangePicker from '../../hooks/ui/useDateRangePicker';
import { Pagination, Select, Table, TextInput } from 'flowbite-react';
import { useFetchData } from '../../hooks/useFetchData';
import { formatDate } from '../../utils/format/format';
import { useDebounce } from '../../hooks/ui/useDebounce';
import ConfirmPopup from '../../components/Popup/ConfirmPopup';
import { useHttp } from '../../hooks/useHttp';
import { ApiResponse } from '../../types/apiResponse';

const PaymentManagementPage = () => {
    const { start, end, setStart, setEnd } = useDateRangePicker();
    const [status, setStatus] = useState<string>('');
    const [keyword, setKeyword] = useState<string>('');
    const debouncedKeyword = useDebounce(keyword, 500);

    const {
        data: transactionsData,
        isLoading: fetchIsLoading,
        fetchData,
    } = useFetchData<any>('/api/admin/product/transactions', {}, false);
    const [refreshFlag, setRefreshFlag] = useState(false);

    const [currentPage, setCurrentPage] = useState(1);
    const onPageChange = (page: number) => setCurrentPage(page);

    const handleStatusChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setStatus(event.target.value);
    };

    const handleKeywordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setKeyword(event.target.value);
    };

    const [showEditConfirmPopup, setEditConfirmPopup] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState<any>(null);

    const { sendRequest, data, isLoading: sendIsLoading, error } = useHttp<ApiResponse>();
    const approvalTransaction = async (transactionId: number): Promise<void> => {
        try {
            await sendRequest({
                url: '/api/admin/product/approval',
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

    useEffect(() => {
        fetchData({
            startDate: start,
            endDate: end,
            status: status,
            keyword: debouncedKeyword,
            page: currentPage,
            pageSize: 10,
        });
    }, [start, end, status, debouncedKeyword, fetchData, refreshFlag, currentPage]);

    return (
        <div className="mx-auto max-w-screen-lg">
            <div className="flex flex-col md:flex-row justify-end items-center space-y-4 md:space-y-0 md:space-x-4 mb-4">
                <div className="flex w-full space-x-2">
                    <TextInput
                        placeholder="이메일 또는 입금자명"
                        className="flex-grow"
                        onChange={handleKeywordChange}
                        value={keyword}
                    />
                    <div></div>
                    <Select onChange={handleStatusChange} value={status}>
                        <option value="">전체</option>
                        <option value="PENDING">대기</option>
                        <option value="EARNED">완료</option>
                        <option value="CANCELLED">취소</option>
                    </Select>
                </div>

                <div className="flex justify-end items-center space-x-2">
                    <DateRangePicker start={start} setStart={setStart} end={end} setEnd={setEnd} />
                </div>
            </div>

            <div className="overflow-x-auto">
                <Table hoverable>
                    <Table.Head>
                        <Table.HeadCell>상태</Table.HeadCell>
                        <Table.HeadCell>상품</Table.HeadCell>
                        <Table.HeadCell>입금자</Table.HeadCell>
                        <Table.HeadCell>신청일</Table.HeadCell>
                        <Table.HeadCell>
                            <span className="sr-only">Edit</span>
                        </Table.HeadCell>
                    </Table.Head>

                    <Table.Body className="divide-y">
                        {!fetchIsLoading && transactionsData?.data?.productTransactionList?.length > 0 && (
                            <>
                                {transactionsData.data.productTransactionList.map((product: any) => (
                                    <Table.Row
                                        key={product.id}
                                        className="whitespace-nowrap bg-white dark:border-gray-700 dark:bg-gray-800"
                                    >
                                        <Table.Cell
                                            className={`${
                                                product.productTransactionStatus === '대기'
                                                    ? 'text-red-500'
                                                    : product.productTransactionStatus === '취소'
                                                      ? 'text-gray-500'
                                                      : 'text-green-500'
                                            }`}
                                        >
                                            {product.productTransactionStatus}
                                        </Table.Cell>
                                        <Table.Cell>{product.productName}</Table.Cell>
                                        <Table.Cell>{product.depositor}</Table.Cell>
                                        <Table.Cell>{formatDate(new Date(product.createdAt)).slice(2)}</Table.Cell>
                                        <Table.Cell>
                                            <p
                                                className={`cursor-pointer font-medium hover:underline ${
                                                    product.productTransactionStatus !== '대기'
                                                        ? 'line-through'
                                                        : 'text-cyan-600 dark:text-cyan-500'
                                                }`}
                                                onClick={() => {
                                                    setSelectedProduct(product);
                                                    setEditConfirmPopup(true);
                                                }}
                                            >
                                                승인
                                            </p>
                                        </Table.Cell>
                                    </Table.Row>
                                ))}
                            </>
                        )}
                    </Table.Body>
                </Table>
            </div>

            {!fetchIsLoading && transactionsData?.data?.totalCount > 0 && (
                <div className="flex overflow-x-auto justify-center mt-2">
                    <Pagination
                        currentPage={currentPage}
                        totalPages={Math.ceil(transactionsData?.data?.totalCount / 10)}
                        onPageChange={onPageChange}
                        showIcons
                        previousLabel=""
                        nextLabel=""
                    />
                </div>
            )}

            {showEditConfirmPopup && selectedProduct && (
                <ConfirmPopup
                    show={showEditConfirmPopup}
                    onClose={() => {
                        setEditConfirmPopup(false);
                        setSelectedProduct(null);
                    }}
                    title="거래 승인"
                    description={
                        <>
                            <p>트랜잭션 아이디: {selectedProduct.id}</p>
                            <p>상태: {selectedProduct.productTransactionStatus}</p>
                            <p>신청일: {formatDate(new Date(selectedProduct.createdAt))}</p>
                            <p>수정일: {formatDate(new Date(selectedProduct.updatedAt))}</p>
                            <br />
                            <p>이메일: {selectedProduct.email}</p>
                            <p>상품: {selectedProduct.productName}</p>
                            <p>입금액: {selectedProduct.amount}원</p>
                            <p>입금자: {selectedProduct.depositor}</p>
                        </>
                    }
                    onConfirm={() => {
                        if (selectedProduct.productTransactionStatus === '대기') {
                            approvalTransaction(selectedProduct.id);
                        }
                    }}
                />
            )}
        </div>
    );
};

export default PaymentManagementPage;
