import React, { useState } from 'react';
import { Accordion, Button } from 'flowbite-react';
import LearnMore from '../components/LearnMore';
import { addDays, formatDate, formatPhoneNumber } from '../utils/format/format';
import { MdCheck } from 'react-icons/md';
import TimeSince from '../components/TimeSince';
import { useNavigate } from 'react-router-dom';
import { useFetchData } from '../hooks/useFetchData';
import { FaCoins } from 'react-icons/fa';

const SearchHistoryPage = () => {
    const navigate = useNavigate();
    const { data: searchHistories, isLoading } = useFetchData<any>('/api/search-histories', {});
    const [isCopied, setIsCopied] = useState(false);

    return (
        <div className="mx-auto max-w-screen-lg">
            {isLoading ? (
                <div></div>
            ) : searchHistories?.data?.histories?.length > 0 ? (
                <>
                    <Accordion flush collapseAll={true}>
                        {searchHistories.data.histories.map((history: any, index: number) => (
                            <Accordion.Panel key={index}>
                                <Accordion.Title className="bg-transparent dark:bg-transparent">
                                    <div className="flex items-center sm:flex-row">
                                        <img
                                            className="mx-auto w-12 h-12 rounded-full ml-0 mr-3"
                                            src={history.profile.profileImageUrl || '/default-profile.jpg'}
                                            alt="avatar"
                                        />
                                        <div className="text-left break-all">
                                            <p className="text-md text-gray-900 dark:text-white">
                                                {history.profile.nickName || '이름 없음'}
                                            </p>
                                            <p className="text-gray-500 dark:text-gray-400">
                                                {history.profile.statusMessage || '상태메세지 없음'}
                                            </p>
                                        </div>
                                    </div>
                                </Accordion.Title>
                                <Accordion.Content>
                                    <div className="flex justify-start items-center mb-2">
                                        <div className="mr-2 flex h-7 w-7 items-center justify-center rounded-lg bg-gray-100 dark:bg-gray-800 lg:h-8 lg:w-8">
                                            <svg
                                                className="h-4 w-4 text-gray-600 dark:text-gray-500 lg:h-5 lg:w-5"
                                                fill="currentColor"
                                                viewBox="0 0 20 20"
                                                xmlns="http://www.w3.org/2000/svg"
                                            >
                                                <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z" />
                                            </svg>
                                        </div>
                                        <p className="text-gray-900 dark:text-white">
                                            {history.targetPhoneNumber.startsWith('@')
                                                ? history.targetPhoneNumber
                                                : formatPhoneNumber(history.targetPhoneNumber)}
                                        </p>
                                    </div>
                                    <div className="flex justify-start items-center mb-2">
                                        <div className="mr-2 flex h-7 w-7 items-center justify-center rounded-lg bg-gray-100 dark:bg-gray-800 lg:h-8 lg:w-8">
                                            <svg
                                                className="h-4 w-4 text-gray-600 dark:text-gray-500 lg:h-5 lg:w-5"
                                                fill="currentColor"
                                                viewBox="0 0 20 20"
                                                xmlns="http://www.w3.org/2000/svg"
                                            >
                                                <path
                                                    fillRule="evenodd"
                                                    d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z"
                                                    clipRule="evenodd"
                                                ></path>
                                            </svg>
                                        </div>
                                        <TimeSince createdAt={history.createdAt} />
                                    </div>
                                    <div className="flex justify-start items-center mb-2">
                                        <div className="mr-2 flex h-7 w-7 items-center justify-center rounded-lg bg-gray-100 dark:bg-gray-800 lg:h-8 lg:w-8">
                                            <FaCoins className="h-3 w-3 text-gray-600 dark:text-gray-500 lg:h-4 lg:w-4" />
                                        </div>
                                        <p className="text-gray-900 dark:text-white">
                                            -{new Intl.NumberFormat('ko-KR').format(history.cost)} P
                                        </p>
                                    </div>
                                    <div className="flex justify-start items-center mb-2">
                                        <div className="mr-2 flex h-7 w-7 items-center justify-center rounded-lg bg-gray-100 dark:bg-gray-800 lg:h-8 lg:w-8">
                                            <svg
                                                className="h-4 w-4 text-gray-600 dark:text-gray-500 lg:h-5 lg:w-5"
                                                fill="currentColor"
                                                viewBox="0 0 20 20"
                                                xmlns="http://www.w3.org/2000/svg"
                                            >
                                                <path
                                                    fillRule="evenodd"
                                                    d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                                                    clipRule="evenodd"
                                                ></path>
                                            </svg>
                                        </div>
                                        <p className="text-gray-900 dark:text-white">
                                            만료일: {formatDate(addDays(history.createdAt, 2))}
                                        </p>
                                    </div>
                                    <div className="flex justify-end space-x-2">
                                        <Button
                                            color="blue"
                                            outline
                                            onClick={() => {
                                                navigate('/search-history/detail', {
                                                    state: { targetHistory: history },
                                                });
                                            }}
                                        >
                                            스냅샷 확인
                                        </Button>
                                        {navigator.clipboard && (
                                            <Button
                                                color="blue"
                                                outline
                                                onClick={() =>
                                                    navigator.clipboard
                                                        .writeText(history.targetPhoneNumber)
                                                        .then(() => {
                                                            setIsCopied(true);
                                                            setTimeout(() => setIsCopied(false), 500);
                                                        })
                                                }
                                            >
                                                {isCopied ? <MdCheck /> : '번호/아이디 복사'}
                                            </Button>
                                        )}
                                    </div>
                                </Accordion.Content>
                            </Accordion.Panel>
                        ))}
                    </Accordion>
                </>
            ) : (
                <div className="text-center">
                    <p className="text-gray-500 dark:text-gray-400 mb-2 text-4xl">🤔</p>
                    <p className="text-gray-500 dark:text-gray-400 mb-2">조회 내역이 비어있습니다.</p>
                    <LearnMore to="/policy" text="자세히 알아보기" />
                </div>
            )}
        </div>
    );
};

export default SearchHistoryPage;
