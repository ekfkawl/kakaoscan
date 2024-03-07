import React from 'react';

const ScrollToTopButton = () => {
    const scrollToTop = () => {
        window.scrollTo({ top: 0 });
    };

    return (
        <button
            onClick={scrollToTop}
            className="fixed bottom-4 right-4 bg-green-500 hover:bg-green-600 dark:bg-blue-500 dark:hover:bg-blue-600 text-white p-2 rounded-full shadow-lg z-50"
        >
            <svg
                className="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
            >
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 15l7-7 7 7"></path>
            </svg>
        </button>
    );
};

export default ScrollToTopButton;
