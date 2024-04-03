import React from 'react';
import { Link } from 'react-router-dom';
import { Alert, Button, Checkbox, Label, TextInput } from 'flowbite-react';
import useEmailValidation from '../../hooks/validation/useEmailValidation';
import usePasswordValidation from '../../hooks/validation/usePasswordValidation';
import useLogin from '../../hooks/auth/useLogin';
import useRedirectIfAuthenticated from '../../hooks/auth/useRedirectIfAuthenticated';
import { GoogleOAuthProvider } from '@react-oauth/google';
import GoogleLoginButton from './GoogleLoginButton';

const LoginForm = () => {
    useRedirectIfAuthenticated('/');

    const { email, setEmail } = useEmailValidation();
    const { password, setPassword } = usePasswordValidation();
    const { login, isLoading, error: loginError } = useLogin();
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        await login({ email, password });
    };

    return (
        <form className="w-full max-w-md space-y-4 md:space-y-6 xl:max-w-xl" onSubmit={handleSubmit}>
            <h1 className="text-xl font-bold text-gray-900 dark:text-white">귀하의 계정에 로그인하세요</h1>
            <div className="items-center space-x-0 space-y-3 sm:flex sm:space-x-4 sm:space-y-0">
                {process.env.REACT_APP_GOOGLE_OAUTH_CLIENT_ID && (
                    <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_OAUTH_CLIENT_ID}>
                        <GoogleLoginButton />
                    </GoogleOAuthProvider>
                )}
            </div>
            <div className="h-0.5 w-full bg-gray-200 dark:bg-gray-700"></div>
            {loginError && <Alert color="red">{loginError}</Alert>}
            <div>
                <Label htmlFor="email" className="mb-2 block dark:text-white">
                    이메일
                </Label>
                <TextInput
                    id="email"
                    placeholder="이메일 주소"
                    required
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
            </div>
            <div>
                <Label htmlFor="password" className="mb-2 block dark:text-white">
                    비밀번호
                </Label>
                <TextInput
                    id="password"
                    required
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            <Button type="submit" className="w-full" disabled={isLoading}>
                로그인
            </Button>
            <div className="max-w-xs mx-auto">
                <div className="flex items-center justify-between">
                    <Link to="/" className="text-sm font-medium text-gray-500 hover:underline dark:text-gray-300">
                        비밀번호 찾기
                    </Link>
                    <Link
                        to="/register"
                        className="text-sm font-medium text-gray-500 hover:underline dark:text-gray-300"
                    >
                        회원가입
                    </Link>
                    <div className="flex items-start">
                        <div className="flex h-5 items-center">
                            <Checkbox id="remember-description" />
                        </div>
                        <div className="ml-2 text-sm">
                            <Label htmlFor="remember-description" className="text-gray-500 dark:text-gray-300">
                                로그인 유지
                            </Label>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    );
};

export default LoginForm;
