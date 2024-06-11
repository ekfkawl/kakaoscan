import React, { useEffect, useState } from 'react';
import { Button, Checkbox, Label, TextInput } from 'flowbite-react';
import usePasswordValidation from '../hooks/validation/usePasswordValidation';
import { useHttp } from '../hooks/useHttp';
import { ApiResponse } from '../types/apiResponse';
import { useNavigate } from 'react-router-dom';
import useUser from '../hooks/auth/useUser';
import MessagePopup from '../components/Popup/MessagePopup';
import useLogout from "../hooks/auth/useLogout";

const MyPage = () => {
    const navigate = useNavigate();
    const { password, setPassword, error: passwordError, handleBlur: handlePasswordBlur } = usePasswordValidation();
    const [password2, setPassword2] = useState('');
    const { sendRequest, data, isLoading, error } = useHttp<ApiResponse>();
    const [showMessage, setShowMessage] = useState(false);
    const user = useUser();
    const { logout } = useLogout();
    const [isAgreed, setIsAgreed] = useState(false);
    const [requestType, setRequestType] = useState('');

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setRequestType('passwordChange');
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

    const handleDeleteAccount = async () => {
        setRequestType('deleteAccount');
        await sendRequest({
            url: '/api/user',
            method: 'DELETE',
        });
    };

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    useEffect(() => {
        if (error || data?.success) {
            setShowMessage(true);
        }
    }, [data, error]);

    return (
        <div className="flex justify-center mx-auto max-w-screen-lg">
            <div className="pl-2 pr-2 w-full sm:w-1/2">
                {user?.authenticationType === 'LOCAL' && (
                    <>
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
                            {passwordError && (
                                <p className="mt-2 text-sm text-red-600 dark:text-red-500">{passwordError}</p>
                            )}
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
                                <Button
                                    type="submit"
                                    disabled={password2.length === 0 || password !== password2}
                                    className="mb-4"
                                >
                                    수정완료
                                </Button>
                            </div>
                        </form>
                    </>
                )}
                <h2 className="mb-1 text-xl font-bold leading-tight tracking-tight text-gray-900 dark:text-white md:text-2xl">
                    회원탈퇴
                </h2>
                <div className="mt-4 flex items-center">
                    <Checkbox id="accept" checked={isAgreed} onChange={(e) => setIsAgreed(e.target.checked)} />
                    <Label htmlFor="accept" className="ml-2 flex">
                        회원탈퇴에 동의합니다.
                    </Label>
                </div>
                <div className="flex justify-end">
                    <Button type="button" className="mb-4" disabled={!isAgreed} onClick={handleDeleteAccount}>
                        회원탈퇴
                    </Button>
                </div>
            </div>
            {showMessage && (
                <MessagePopup
                    show={showMessage}
                    onClose={() => setShowMessage(false)}
                    title="알림"
                    description={
                        requestType === 'passwordChange'
                            ? error || '비밀번호가 변경되었습니다.'
                            : '회원탈퇴가 완료되었습니다.'
                    }
                    onConfirm={() => {
                        if (data?.success) {
                            handleLogout();
                        }
                    }}
                />
            )}
        </div>
    );
};

export default MyPage;
