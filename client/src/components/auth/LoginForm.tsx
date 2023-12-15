import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Alert, Button, Checkbox, Label, TextInput } from 'flowbite-react';
import useEmailValidation from '../../hooks/validation/useEmailValidation';
import usePasswordValidation from '../../hooks/validation/usePasswordValidation';
import useLogin from '../../hooks/auth/useLogin';
import useRedirectIfAuthenticated from '../../hooks/auth/useRedirectIfAuthenticated';

const LoginForm = () => {
    useRedirectIfAuthenticated('/');

    const navigate = useNavigate();
    const { email, setEmail } = useEmailValidation();
    const { password, setPassword } = usePasswordValidation();
    const { login, isLoading, error: loginError } = useLogin();
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        let res = await login({ email, password });
        if (res && res.success) {
            navigate('/');
        }
    };

    return (
        <form
            className="w-full max-w-md space-y-4 md:space-y-6 xl:max-w-xl"
            onSubmit={handleSubmit}
        >
            <h1 className="text-xl font-bold text-gray-900 dark:text-white">
                귀하의 계정에 로그인하세요
            </h1>
            <div className="items-center space-x-0 space-y-3 sm:flex sm:space-x-4 sm:space-y-0">
                <Button
                    color="gray"
                    href="#"
                    className="w-full md:w-full hover:bg-gray-50 dark:hover:bg-gray-700"
                >
                    <svg
                        aria-hidden
                        className="mr-2 h-5 w-5"
                        viewBox="0 0 21 20"
                        fill="none"
                        xmlns="http://www.w3.org/2000/svg"
                    >
                        <g clipPath="url(#clip0_13183_10121)">
                            <path
                                d="M20.3081 10.2303C20.3081 9.55056 20.253 8.86711 20.1354 8.19836H10.7031V12.0492H16.1046C15.8804 13.2911 15.1602 14.3898 14.1057 15.0879V17.5866H17.3282C19.2205 15.8449 20.3081 13.2728 20.3081 10.2303Z"
                                fill="#3F83F8"
                            />
                            <path
                                d="M10.7019 20.0006C13.3989 20.0006 15.6734 19.1151 17.3306 17.5865L14.1081 15.0879C13.2115 15.6979 12.0541 16.0433 10.7056 16.0433C8.09669 16.0433 5.88468 14.2832 5.091 11.9169H1.76562V14.4927C3.46322 17.8695 6.92087 20.0006 10.7019 20.0006V20.0006Z"
                                fill="#34A853"
                            />
                            <path
                                d="M5.08857 11.9169C4.66969 10.6749 4.66969 9.33008 5.08857 8.08811V5.51233H1.76688C0.348541 8.33798 0.348541 11.667 1.76688 14.4927L5.08857 11.9169V11.9169Z"
                                fill="#FBBC04"
                            />
                            <path
                                d="M10.7019 3.95805C12.1276 3.936 13.5055 4.47247 14.538 5.45722L17.393 2.60218C15.5852 0.904587 13.1858 -0.0287217 10.7019 0.000673888C6.92087 0.000673888 3.46322 2.13185 1.76562 5.51234L5.08732 8.08813C5.87733 5.71811 8.09302 3.95805 10.7019 3.95805V3.95805Z"
                                fill="#EA4335"
                            />
                        </g>
                        <defs>
                            <clipPath id="clip0_13183_10121">
                                <rect
                                    width="20"
                                    height="20"
                                    fill="white"
                                    transform="translate(0.5)"
                                />
                            </clipPath>
                        </defs>
                    </svg>
                    구글 계정으로 로그인
                </Button>
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
                    <Link
                        to="/"
                        className="text-sm font-medium text-gray-500 hover:underline dark:text-gray-300"
                    >
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
                            <Label
                                htmlFor="remember-description"
                                className="text-gray-500 dark:text-gray-300"
                            >
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
