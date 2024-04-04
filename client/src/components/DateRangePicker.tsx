import React from 'react';
import { Datepicker } from 'flowbite-react';
import { simpleFormatDate } from '../utils/format/format';

interface DateRangePickerProps {
    start: string;
    end: string;
    setStart: (date: string) => void;
    setEnd: (date: string) => void;
}

const DateRangePicker: React.FC<DateRangePickerProps> = ({ start, end, setStart, setEnd }) => {
    const handleDateStartChange = (date: Date) => {
        const formattedDate = simpleFormatDate(date);
        setStart(formattedDate);
    };

    const handleDateEndChange = (date: Date) => {
        const formattedDate = simpleFormatDate(date);
        setEnd(formattedDate);
    };

    return (
        <div className="flex justify-end items-center space-x-4 mb-4">
            <div className="relative w-fit">
                <Datepicker
                    id="date_start"
                    onSelectedDateChanged={handleDateStartChange}
                    value={start}
                    maxDate={new Date()}
                />
            </div>
            <span className="dark:text-white">~</span>
            <div className="relative w-fit">
                <Datepicker
                    id="date_end"
                    onSelectedDateChanged={handleDateEndChange}
                    value={end}
                    maxDate={new Date()}
                />
            </div>
        </div>
    );
};

export default DateRangePicker;
