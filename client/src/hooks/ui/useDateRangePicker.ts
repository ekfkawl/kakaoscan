import { useEffect, useState } from 'react';
import { simpleFormatDate } from '../../utils/format/format';

function useDateRangePicker() {
    const today = new Date();
    const sevenDaysAgo = new Date(today.getFullYear(), today.getMonth(), today.getDate() - 7);

    const [start, setStart] = useState<string>(simpleFormatDate(sevenDaysAgo));
    const [end, setEnd] = useState<string>(simpleFormatDate(today));

    useEffect(() => {
        if (new Date(start) > new Date(end)) {
            setStart(end);
        }
    }, [start, end]);

    return {
        start,
        end,
        setStart,
        setEnd,
    };
}

export default useDateRangePicker;
