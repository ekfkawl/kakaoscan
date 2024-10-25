import React from 'react';
import { Button } from 'flowbite-react';
import { getAPIBaseURL } from '../../utils/web/url';

const NaverLoginButton: React.FC = () => {
    const NAVER_OAUTH_CLIENT_ID = process.env.REACT_APP_NAVER_OAUTH_CLIENT_ID;
    const NAVER_OAUTH_STATE = Math.random().toString().slice(2, 10);
    const NAVER_OAUTH_REDIRECT_URI = `${getAPIBaseURL()}/api/login/oauth2/naver`;

    return (
        <Button
            onClick={() =>
                window.location.href = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${NAVER_OAUTH_CLIENT_ID}&state=${NAVER_OAUTH_STATE}&redirect_uri=${NAVER_OAUTH_REDIRECT_URI}`
            }
            className="w-full md:w-full hover:bg-gray-50 dark:hover:bg-gray-700"
            color="gray"
        >
            <svg
                aria-hidden
                className="mr-2 h-5 w-5"
                viewBox="0 0 200 200"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
            >
                <g transform="translate(100, 100) scale(1.6) translate(-100, -100)">
                    <polygon
                        fill="#1ec800"
                        points="115.9,145.8 83.7,98.4 83.7,145.8 50,145.8 50,54.3 84.2,54.3 116.4,101.6 116.4,54.3 150,54.3 150,145.8 115.9,145.8"
                    />
                </g>
            </svg>
            네이버 계정으로 로그인
        </Button>
    );
};

export default NaverLoginButton;
