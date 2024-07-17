import { useState } from 'react';
import { formatPhoneNumber } from '../../utils/format/format';

const usePhoneNumberFormat = (): [
    string,
    (value: string) => void,
    (value: string) => void,
    (event: React.ChangeEvent<HTMLInputElement>) => void,
    (event: React.ChangeEvent<HTMLInputElement>) => void,
] => {
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const setFormattedPhoneNumber = (value: string) => {
        const formatted = formatPhoneNumber(value);
        setPhoneNumber(formatted);
    };

    const handleChangeForNumber = (event: React.ChangeEvent<HTMLInputElement>): void => {
        let formattedPhoneNumber = formatPhoneNumber(event.target.value);

        if (formattedPhoneNumber.endsWith('-')) {
            formattedPhoneNumber = formattedPhoneNumber.slice(0, -1);
        }

        setPhoneNumber(formattedPhoneNumber);
    };

    const handleChangeForId = (event: React.ChangeEvent<HTMLInputElement>): void => {
        const validInput = event.target.value.replace(/[^a-zA-Z0-9._-]/g, '');
        setPhoneNumber(validInput);
    };

    return [phoneNumber, setPhoneNumber, setFormattedPhoneNumber, handleChangeForNumber, handleChangeForId];
};

export default usePhoneNumberFormat;
