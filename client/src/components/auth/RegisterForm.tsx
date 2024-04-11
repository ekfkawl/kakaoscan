import React, { useEffect } from 'react';
import { Alert, Button, Label, Modal, TextInput } from 'flowbite-react';
import { Link, useNavigate } from 'react-router-dom';
import useEmailValidation from '../../hooks/validation/useEmailValidation';
import usePasswordValidation from '../../hooks/validation/usePasswordValidation';
import useModalState from '../../hooks/ui/useModalState';
import useRegister from '../../hooks/auth/useRegister';
import useRedirectIfAuthenticated from '../../hooks/auth/useRedirectIfAuthenticated';

const RegisterForm = () => {
    useRedirectIfAuthenticated('/');

    const { email, setEmail, error: emailError, handleBlur: handleEmailBlur } = useEmailValidation();
    const { password, setPassword, error: passwordError, handleBlur: handlePasswordBlur } = usePasswordValidation();
    const { register, isLoading, error, data } = useRegister();
    const { isOpen, openModal, closeModal } = useModalState();
    const navigate = useNavigate();

    useEffect(() => {
        if (data && data.success) {
            openModal();
        }
    }, [data, openModal]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        await register({ email, password });
    };

    const handleModalClose = () => {
        closeModal();
        navigate('/');
    };

    return (
        <form className="w-full max-w-md space-y-4 md:space-y-6 xl:max-w-xl" onSubmit={handleSubmit}>
            <h1 className="text-xl font-bold text-gray-900 dark:text-white">귀하의 계정을 생성하세요</h1>
            <div className="h-0.5 w-full bg-gray-200 dark:bg-gray-700"></div>
            {error && <Alert color="red">{error}</Alert>}
            <div>
                <Label htmlFor="email" className="mb-2 block dark:text-white">
                    이메일
                </Label>
                <TextInput
                    id="email"
                    placeholder="이메일 주소"
                    required
                    type="text"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    onBlur={handleEmailBlur}
                />
                {emailError && <p className="mt-2 text-sm text-red-600 dark:text-red-500">{emailError}</p>}
            </div>
            <div>
                <Label htmlFor="password" className="mb-2 block dark:text-white">
                    비밀번호
                </Label>
                <TextInput
                    id="password"
                    type="password"
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    onBlur={handlePasswordBlur}
                />
                {passwordError && <p className="mt-2 text-sm text-red-600 dark:text-red-500">{passwordError}</p>}
            </div>
            <Button type="submit" className="w-full" disabled={isLoading}>
                회원가입
            </Button>
            <Modal show={isOpen} onClose={handleModalClose} position="center">
                <Modal.Header>가입 완료를 위한 이메일 인증 안내</Modal.Header>
                <Modal.Body>
                    <div className="space-y-6 p-6">
                        <p className="text-base leading-relaxed text-gray-500 dark:text-gray-400">
                            이메일로 보내드린 인증 링크를 클릭하시면 가입이 완료됩니다. 이메일을 확인하시고, 인증 링크를
                            클릭하여 가입을 마무리해 주세요.
                        </p>
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <div className="flex justify-end w-full">
                        <Button onClick={handleModalClose}>확인</Button>
                    </div>
                </Modal.Footer>
            </Modal>
            <div className="flex flex-col items-center justify-center">
                <p className="text-sm text-gray-900 dark:text-white font-medium">
                    이미 계정이 있나요?&nbsp;
                    <Link to="/login" className="font-medium text-primary-600 hover:underline dark:text-primary-500">
                        로그인
                    </Link>
                </p>
            </div>
        </form>
    );
};

export default RegisterForm;
