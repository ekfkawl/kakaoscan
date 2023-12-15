import { Avatar, DarkThemeToggle, Dropdown, Navbar } from 'flowbite-react';
import React from 'react';
import { FaGithub } from 'react-icons/fa';
import useLogout from '../../hooks/auth/useLogout';
import { useNavigate } from 'react-router-dom';

const HeaderWithUser = () => {
    const navigate = useNavigate();
    const { logout } = useLogout();
    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    return (
        <header>
            <Navbar className="dark:bg-gray-900">
                <Navbar.Brand href="/">
                    <img src="/favicon.png" className="mr-3 h-6 sm:h-9 dark:hidden" alt="Kakaoscan Logo" />
                    <img src="/favicon-dark.png" className="mr-3 h-6 sm:h-9 hidden dark:block" alt="Kakaoscan Logo" />
                    <span className="self-center whitespace-nowrap text-xl quicksand font-semibold dark:text-white">
                        kakaoscan
                    </span>
                </Navbar.Brand>
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
                            label={<Avatar size="sm" alt="User Menu" img="/favicon.png" rounded />}
                        >
                            <Dropdown.Header>
                                <strong className="block text-sm">email@test.com</strong>
                            </Dropdown.Header>
                            <Dropdown.Item className="text-gray-500 dark:text-gray-400" onClick={handleLogout}>
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
                        href="#"
                        active
                        className="text-gray-900 bg-transparent border-b border-gray-100 md:border-0 dark:border-gray-700 dark:text-white"
                    >
                        Menu1
                    </Navbar.Link>
                    <Navbar.Link href="#">Menu2</Navbar.Link>
                </Navbar.Collapse>
            </Navbar>
        </header>
    );
};

export default HeaderWithUser;
