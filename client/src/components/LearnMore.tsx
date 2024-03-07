import React from 'react';
import { Link } from 'react-router-dom';

interface LearnMoreProps {
    to: string;
    text: string;
}

const LearnMore: React.FC<LearnMoreProps> = ({ to, text }) => {
    return (
        <Link
            to={to}
            className="inline-flex items-center justify-center text-base text-primary-600 hover:text-primary-800 dark:text-primary-500 dark:hover:text-primary-700"
        >
            {text}
            <svg
                aria-hidden
                className="ml-1 h-5 w-5"
                fill="currentColor"
                viewBox="0 0 20 20"
                xmlns="http://www.w3.org/2000/svg"
            >
                <path
                    fillRule="evenodd"
                    d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                    clipRule="evenodd"
                />
            </svg>
        </Link>
    );
};

export default LearnMore;
