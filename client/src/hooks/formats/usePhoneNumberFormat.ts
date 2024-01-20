import { useState } from 'react';

const usePhoneNumberFormat = (): [string, (value: string) => void, (event: React.ChangeEvent<HTMLInputElement>) => void] => {
    const [phoneNumber, setPhoneNumber] = useState<string>('');
    const setFormattedPhoneNumber = (value: string) => {
        const formatted = formatPhoneNumber(value);
        setPhoneNumber(formatted);
    };

    const formatPhoneNumber = (inputValue: string): string => {
        if (!inputValue) return inputValue;

        const numbersAndHyphens = inputValue.replace(/[^\d-]/g, '');
        const numbers = numbersAndHyphens.replace(/-/g, '');

        if (numbers.length < 4) return numbers;
        if (numbers.length < 7) return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;

        return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
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
