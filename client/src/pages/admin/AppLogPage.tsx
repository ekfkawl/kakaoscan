import React, { useEffect, useState } from 'react';
import DateRangePicker from '../../components/DateRangePicker';
import useDateRangePicker from '../../hooks/ui/useDateRangePicker';
import { Pagination, Select, Table, TextInput } from 'flowbite-react';
import { useFetchData } from '../../hooks/useFetchData';
import { formatDate } from '../../utils/format/format';
import { useDebounce } from '../../hooks/ui/useDebounce';

const AppLogPage = () => {
    const { start, end, setStart, setEnd } = useDateRangePicker();
    const [level, setlevel] = useState<string>('');
    const [keyword, setKeyword] = useState<string>('');
    const debouncedKeyword = useDebounce(keyword, 500);

    const { data: logsData, isLoading: fetchIsLoading, fetchData } = useFetchData<any>('/api/admin/log', {}, false);

    const [currentPage, setCurrentPage] = useState(1);
    const onPageChange = (page: number) => setCurrentPage(page);

    const handleStatusChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setlevel(event.target.value);
    };

    const handleKeywordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setKeyword(event.target.value);
    };

    useEffect(() => {
        fetchData({
            startDate: start,
            endDate: end,
            level: level,
            keyword: debouncedKeyword,
            page: currentPage,
            pageSize: 20,
        });
    }, [start, end, level, debouncedKeyword, fetchData, currentPage]);

    return (
        <div className="mx-auto max-w-screen-lg">
            <div className="flex flex-col md:flex-row justify-end items-center space-y-4 md:space-y-0 md:space-x-4 mb-4">
                <div className="flex w-full space-x-2">
                    <TextInput
                        placeholder="메세지 또는 아이디"
                        className="flex-grow"
                        onChange={handleKeywordChange}
                        value={keyword}
                    />
                    <div></div>
                    <Select onChange={handleStatusChange} value={level}>
                        <option value="">전체</option>
                        <option value="PENDING">INFO</option>
                        <option value="EARNED">WARN</option>
                        <option value="CANCELLED">ERROR</option>
                    </Select>
                </div>

                <div className="flex justify-end items-center space-x-2">
                    <DateRangePicker start={start} setStart={setStart} end={end} setEnd={setEnd} />
                </div>
            </div>

            <div className="overflow-x-auto">
                <Table hoverable>
                    <Table.Head>
                        <Table.HeadCell>날짜</Table.HeadCell>
                        <Table.HeadCell>레벨</Table.HeadCell>
                        <Table.HeadCell>쓰레드</Table.HeadCell>
                        <Table.HeadCell>메세지</Table.HeadCell>
                        <Table.HeadCell>아이디</Table.HeadCell>
                    </Table.Head>

                    <Table.Body className="divide-y">
                        {!fetchIsLoading && logsData?.data?.appLogList?.length > 0 && (
                            <>
                                {logsData.data.appLogList.map((log: any) => (
                                    <Table.Row key={log.id} className="bg-white dark:border-gray-700 dark:bg-gray-800">
                                        <Table.Cell className="whitespace-nowrap">
                                            {formatDate(new Date(log.date)).slice(5)}
                                        </Table.Cell>
                                        <Table.Cell className="whitespace-nowrap">{log.level}</Table.Cell>
                                        <Table.Cell className="whitespace-nowrap">{log.threadName}</Table.Cell>
                                        <Table.Cell>{highlightKeyword(log.message, keyword)}</Table.Cell>
                                        <Table.Cell>{highlightKeyword(log.requestId, keyword)}</Table.Cell>
                                    </Table.Row>
                                ))}
                            </>
                        )}
                    </Table.Body>
                </Table>
            </div>

            {!fetchIsLoading && logsData?.data?.totalCount > 0 && (
                <div className="flex overflow-x-auto justify-center mt-2">
                    <Pagination
                        currentPage={currentPage}
                        totalPages={Math.ceil(logsData?.data?.totalCount / 20)}
                        onPageChange={onPageChange}
                        showIcons
                        previousLabel=""
                        nextLabel=""
                    />
                </div>
            )}
        </div>
    );
};

const highlightKeyword = (text: string, keyword: string) => {
    if (!keyword.trim()) {
        return text;
    }

    const parts = text.split(new RegExp(`(${keyword})`, 'gi'));
    return (
        <>
            {parts.map((part, index) =>
                part.toLowerCase() === keyword.toLowerCase() ? (
                    <span key={index} className="bg-yellow-200">
                        {part}
                    </span>
                ) : (
                    part
                ),
            )}
        </>
    );
};

export default AppLogPage;
