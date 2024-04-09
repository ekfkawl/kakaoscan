import { Avatar, DarkThemeToggle, Dropdown, Navbar } from 'flowbite-react';
import React, { useCallback, useEffect, useState } from 'react';
import { FaCoins, FaGithub } from 'react-icons/fa';
import useLogout from '../../hooks/auth/useLogout';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import useUser from '../../hooks/auth/useUser';
import { StompPoint } from '../../types/stomp/stompPoint';
import { useSubscription } from '../../hooks/websocket/useSubscription';
import {
    faCreditCard,
    faFolderOpen,
    faRightFromBracket,
    faShoppingCart,
    faUser,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useSendMessage } from '../../hooks/websocket/useSendMessage';

const HeaderWithUser = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { logout } = useLogout();
    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };
    const user = useUser();
    const sendMessage = useSendMessage();
    const [stompPointResponse, setStompPointResponse] = useState<StompPoint | null>(null);

    useSubscription<StompPoint>('/user/queue/message/point', setStompPointResponse);
    const handleSendPoint = useCallback(() => {
        sendMessage('/pub/points');
    }, [sendMessage]);

    useEffect(() => {
        let intervalId = setInterval(() => {
            handleSendPoint();
        }, 500);

        return () => {
            clearInterval(intervalId);
        };
    }, [handleSendPoint, sendMessage]);

    const isActive = (path: string) => location.pathname === path;

    return (
        <header>
            <Navbar className="dark:bg-gray-900">
                <Link to="/" className="flex items-center">
                    <img src="/favicon.png" className="mr-3 h-6 sm:h-9 dark:hidden" alt="Kakaoscan Logo" />
                    <img src="/favicon-dark.png" className="mr-3 h-6 sm:h-9 hidden dark:block" alt="Kakaoscan Logo" />
                    <span className="self-center whitespace-nowrap text-xl quicksand font-semibold dark:text-white">
                        kakaoscan
                    </span>
                </Link>
                <div className="flex items-center gap-1 lg:order-2 lg:gap-3">
                    <DarkThemeToggle
                        iconDark={FaGithub}
                        iconLight={FaGithub}
                        onClick={(e) => {
                            e.preventDefault();
                            window.open('https://github.com/ekfkawl/kakaoscan', '_blank');
                        }}
                    />
                    <DarkThemeToggle />
                    <div className="ml-1 mr-1 lg:mr-0">
                        <Dropdown
                            arrowIcon={false}
                            inline
                            label={
                                <Avatar
                                    size="sm"
                                    alt="User Menu"
                                    img={user?.profileUrl || '/default-profile.jpg'}
                                    rounded
                                />
                            }
                        >
                            <Dropdown.Item onClick={() => navigate('/my-page')}>
                                <strong className="block text-sm">
                                    <FontAwesomeIcon icon={faUser} className="mr-2" />
                                    {user?.email}
                                </strong>
                            </Dropdown.Item>
                            <Dropdown.Header>
                                <strong className="block text-sm">
                                    <FaCoins className="mr-2 text-yellow-300 dark:text-yellow-200" />
                                    {stompPointResponse == null
                                        ? '로딩 중..'
                                        : stompPointResponse?.message ||
                                          new Intl.NumberFormat('ko-KR').format(stompPointResponse?.balance)}{' '}
                                    P
                                </strong>
                            </Dropdown.Header>
                            <Dropdown.Item onClick={() => navigate('/admin/payment')}>
                                <FontAwesomeIcon icon={faCreditCard} className="mr-2" />
                                결제 관리
                            </Dropdown.Item>
                            <Dropdown.Divider />
                            <Dropdown.Item className="text-sm" onClick={() => navigate('/payment-history')}>
                                <FontAwesomeIcon icon={faShoppingCart} className="mr-2" />
                                구매 내역
                            </Dropdown.Item>
                            <Dropdown.Item className="text-sm" onClick={() => navigate('/search-history')}>
                                <FontAwesomeIcon icon={faFolderOpen} className="mr-2" />
                                조회 내역
                            </Dropdown.Item>
                            <Dropdown.Item className="text-sm" onClick={handleLogout}>
                                <FontAwesomeIcon icon={faRightFromBracket} className="mr-2" />
                                로그아웃
                            </Dropdown.Item>
                        </Dropdown>
                    </div>
                    <div className="ml-1 mr-1 lg:mr-0">
                        <Navbar.Toggle theme={{ icon: 'h-5 w-5 shrink-0' }} />
                    </div>
                </div>
                <Navbar.Collapse
                    theme={{
                        list: 'mt-4 flex flex-col lg:mt-0 lg:flex-row lg:space-x-8 lg:text-base lg:font-medium',
                    }}
                    className="lg:order-1"
                >
                    <Navbar.Link
                        active={isActive('/shop')}
                        className="cursor-pointer"
                        onClick={() => navigate('/shop')}
                    >
                        상점
                    </Navbar.Link>
                    <Navbar.Link
                        active={isActive('/policy')}
                        className="cursor-pointer"
                        onClick={() => navigate('/policy')}
                    >
                        정책
                    </Navbar.Link>
                </Navbar.Collapse>
            </Navbar>
        </header>
    );
};

export default HeaderWithUser;
