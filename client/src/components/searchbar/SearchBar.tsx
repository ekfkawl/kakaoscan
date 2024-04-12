import React from 'react';
import './SearchBar.css';

interface SearchBarProps {
    onKeyPress?: React.KeyboardEventHandler<HTMLInputElement>;
    value?: string;
    onChange?: React.ChangeEventHandler<HTMLInputElement>;
    onMenuClick?: () => void;
    onSearchClick?: () => void;
}

const SearchBar: React.FC<SearchBarProps> = React.memo(
    ({ onKeyPress, value, onChange, onMenuClick, onSearchClick }) => {
        return (
            <div className="search_area dark:bg-gray-900">
                <div className="search_group_inner flex justify-between">
                    <div className="search_logo" onClick={onMenuClick}>
                        <span className="ico_svg">
                            <svg
                                className="w-[22px] h-[22px] text-gray-800 dark:text-white"
                                aria-hidden="true"
                                xmlns="http://www.w3.org/2000/svg"
                                fill="none"
                                viewBox="0 0 16 12"
                            >
                                <path
                                    stroke="currentColor"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth="1.8"
                                    d="M1 1h14M1 6h14M1 11h7"
                                />
                            </svg>
                        </span>
                    </div>

                    <input
                        type="text"
                        placeholder="전화번호 입력"
                        maxLength={13}
                        className="search_input_box flex-1 py-2 px-3 border-none bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:outline-none focus:ring-0 focus:border-none text-lg md:text-xl font-semibold"
                        value={value}
                        onChange={onChange}
                        onKeyPress={onKeyPress}
                    />

                    <div className="search_logo">
                        <span className="ico_svg" onClick={onSearchClick}>
                            <svg
                                className="w-[22px] h-[22px] text-gray-800 dark:text-white"
                                aria-hidden="true"
                                xmlns="http://www.w3.org/2000/svg"
                                fill="none"
                                viewBox="0 0 20 20"
                            >
                                <path
                                    stroke="currentColor"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth="2"
                                    d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z"
                                />
                            </svg>
                        </span>
                    </div>
                </div>
            </div>
        );
    },
);

export default SearchBar;
