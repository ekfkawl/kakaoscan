import React, { useEffect, useState } from 'react';
import { Button, Label, TextInput } from 'flowbite-react';
import usePasswordValidation from '../hooks/validation/usePasswordValidation';
import { useHttp } from '../hooks/useHttp';
import { ApiResponse } from '../types/apiResponse';
import { useNavigate } from 'react-router-dom';
import MessagePopup from '../components/Popup/MessagePopup';

const MyPage = () => {
    const navigate = useNavigate();
    const { password, setPassword, error: passwordError, handleBlur: handlePasswordBlur } = usePasswordValidation();
    const [password2, setPassword2] = useState('');
    const { sendRequest, data, isLoading, error } = useHttp<ApiResponse>();
    const [showMessage, setShowMessage] = useState(false);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (password === password2) {
            await sendRequest({
                url: '/api/user/password',
                method: 'PUT',
                data: {
                    password: password,
                },
            });
        }
    };

    useEffect(() => {
        if (error || data?.success) {
            setShowMessage(true);
        }
    }, [data, error]);

    return (
        <div className="flex justify-center mx-auto max-w-screen-lg">
            <div className="pl-2 pr-2 w-full sm:w-1/2">
                <h2 className="mb-1 text-xl font-bold leading-tight tracking-tight text-gray-900 dark:text-white md:text-2xl">
                    비밀번호 변경
                </h2>
                <form className="mt-4 space-y-4 md:space-y-5 lg:mt-5" onSubmit={handleSubmit}>
                    <div className="grid grid-cols-1 gap-2">
                        <Label htmlFor="password" className="dark:text-white">
                            새로운 비밀번호
                        </Label>
                        <TextInput
                            name="password"
                            id="password"
                            placeholder="••••••••"
                            required
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            onBlur={handlePasswordBlur}
                        />
                    </div>
                    {passwordError && <p className="mt-2 text-sm text-red-600 dark:text-red-500">{passwordError}</p>}
                    <div className="grid grid-cols-1 gap-2">
                        <Label htmlFor="confirm-password" className="dark:text-white">
                            새로운 비밀번호 확인
                        </Label>
                        <TextInput
                            name="confirm-password"
                            id="confirm-password"
                            placeholder="••••••••"
                            required
                            type="password"
                            value={password2}
                            onChange={(e) => setPassword2(e.target.value)}
                        />
                    </div>
                    <div className="flex justify-end">
                        <Button type="submit" disabled={password2.length === 0 || password !== password2}>
                            수정완료
                        </Button>
                    </div>
                </form>
            </div>
            {showMessage && (
                <MessagePopup
                    show={showMessage}
                    onClose={() => setShowMessage(false)}
                    title="알림"
                    description={error || '비밀번호가 변경되었습니다.'}
                    onConfirm={() => {
                        if (data?.success) {
                            navigate('/');
                        }
                    }}
                />
            )}
        </div>
    );
};

export default MyPage;
