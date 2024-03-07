import { useState } from 'react';
import { formatPhoneNumber } from '../../utils/format/format';

const usePhoneNumberFormat = (): [
    string,
    (value: string) => void,
    (event: React.ChangeEvent<HTMLInputElement>) => void,
] => {
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const setFormattedPhoneNumber = (value: string) => {
        const formatted = formatPhoneNumber(value);
        setPhoneNumber(formatted);
    };

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
        let formattedPhoneNumber = formatPhoneNumber(event.target.value);

        if (formattedPhoneNumber.endsWith('-')) {
            formattedPhoneNumber = formattedPhoneNumber.slice(0, -1);
        }

        setPhoneNumber(formattedPhoneNumber);
    };

    return [phoneNumber, setFormattedPhoneNumber, handleChange];
};

export default usePhoneNumberFormat;
