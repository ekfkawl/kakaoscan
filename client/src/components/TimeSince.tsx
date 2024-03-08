import React, { useEffect, useState } from 'react';
import { timeSince } from '../utils/format/format';

const TimeSince = ({ createdAt }: { createdAt: string }) => {
    const [timeSinceText, setTimeSinceText] = useState('');

    useEffect(() => {
        const update = () => {
            setTimeSinceText(timeSince(createdAt));
        };

        update();

        const intervalId = setInterval(update, 5000);

        return () => clearInterval(intervalId);
    }, [createdAt]);

    return <p className="text-gray-900 dark:text-white">{timeSinceText}</p>;
};

export default TimeSince;
