import { useState } from 'react';
import { isPassword } from '../../utils/validation/validateAuth';

const usePasswordValidation = () => {
    const [password, setPassword] = useState<string>('');
    const [error, setError] = useState<string>('');

    const validatePassword = (password: string): string => {
        if (password.includes(' ')) {
            return '공백은 포함될 수 없습니다.';
        }

        if (!isPassword(password)) {
            return '8~16자의 영문 대/소문자, 숫자, 특수문자만 사용해 주세요.';
        }

        return '';
    };

    const handleBlur = (): void => {
        const validationResult = validatePassword(password);
        setError(validationResult);
    };

    return { password, setPassword, error, handleBlur };
};

export default usePasswordValidation;
