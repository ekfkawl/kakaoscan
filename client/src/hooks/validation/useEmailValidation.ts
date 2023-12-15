import { useState } from 'react';
import { isEmail } from '../../utils/validation/validateAuth';

const useEmailValidation = () => {
    const [email, setEmail] = useState<string>('');
    const [error, setError] = useState<string>('');

    const validateEmail = (email: string): string => {
        if (!isEmail(email)) {
            return '올바른 이메일 형식이 아닙니다.';
        }

        const allowedDomains = ['google.com', 'naver.com'];
        const domain = email.split('@')[1];
        if (!allowedDomains.includes(domain)) {
            return '허용된 이메일 도메인이 아닙니다. (naver, daum만 가능)';
        }

        return '';
    };

    const handleBlur = (): void => {
        const validationResult = validateEmail(email);
        setError(validationResult);
    };

    return { email, setEmail, error, handleBlur };
};

export default useEmailValidation;
