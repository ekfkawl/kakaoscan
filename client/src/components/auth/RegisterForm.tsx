import React from "react";
import { Button, Label, TextInput } from "flowbite-react";
import { Link } from "react-router-dom";

const RegisterForm = () => {
    return (
        <form className="w-full max-w-md space-y-4 md:space-y-6 xl:max-w-xl" action="#">
            <h1 className="text-xl font-bold text-gray-900 dark:text-white">
                회원가입
            </h1>
            <div className="flex items-center">
                <div className="h-0.5 w-full bg-gray-200 dark:bg-gray-700"></div>
                <div className="h-0.5 w-full bg-gray-200 dark:bg-gray-700"></div>
            </div>
            <div>
                <Label htmlFor="email" className="mb-2 block dark:text-white">이메일</Label>
                <TextInput
                    id="email"
                    placeholder="이메일 주소"
                    required
                    type="email"
                />
            </div>
            <div>
                <Label htmlFor="password1" className="mb-2 block dark:text-white">비밀번호</Label>
                <TextInput
                    id="password1"
                    required
                    type="password"
                />
            </div>
            <div>
                <Label htmlFor="password2" className="mb-2 block dark:text-white">비밀번호 확인</Label>
                <TextInput
                    id="password2"
                    required
                    type="password"
                />
            </div>
            <Button type="submit" className="w-full">
                회원가입
            </Button>
            <div className="flex flex-col items-center justify-center">
                <p className="text-sm text-gray-900 dark:text-white font-medium">
                    이미 계정이 있나요?&nbsp;
                    <Link
                        to="/"
                        className="font-medium text-primary-600 hover:underline dark:text-primary-500"
                    >
                        로그인
                    </Link>
                </p>
            </div>
        </form>
    );
}

export default RegisterForm;
