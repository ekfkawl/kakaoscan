import './index.css';
import React from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from 'react-redux';
import store from './redux/store';
import App from './App';
import { Flowbite } from 'flowbite-react';

const container = document.getElementById('root');
if (container) {
    const root = createRoot(container);
    root.render(
        <Flowbite
            theme={{
                theme: {
                    tabs: {
                        tablist: {
                            tabitem: {
                                base: 'flex items-center justify-center p-4 rounded-t-lg text-sm font-medium first:ml-0 disabled:cursor-not-allowed disabled:text-gray-400 disabled:dark:text-gray-500 focus:ring-4 focus:ring-green-300 dark:focus:ring-blue-400 focus:outline-none',
                                styles: {
                                    default: {
                                        active: {
                                            on: 'bg-gray-100 text-green-500 dark:bg-gray-800 dark:text-blue-500',
                                        },
                                    },
                                    underline: {
                                        base: 'rounded-t-lg',
                                        active: {
                                            on: 'text-green-500 rounded-t-lg border-b-2 border-green-500 active dark:text-blue-500 dark:border-blue-500',
                                        },
                                    },
                                    pills: {
                                        base: '',
                                        active: {
                                            on: 'rounded-lg bg-blue-600 text-white',
                                        },
                                    },
                                    fullWidth: {
                                        base: 'ml-0 first:ml-0 w-full rounded-none flex',
                                        active: {
                                            on: 'p-4 text-gray-900 bg-gray-100 active dark:bg-gray-700 dark:text-white rounded-none',
                                        },
                                    },
                                },
                            },
                        },
                    },
                    toast: {
                        root: {
                            base: 'flex w-full items-center rounded-lg bg-white p-4 text-gray-800 shadow dark:bg-gray-800 dark:text-gray-400',
                            closed: 'opacity-0 ease-out',
                        },
                        toggle: {
                            base: '-mx-1.5 -my-1.5 ml-auto inline-flex h-8 w-8 rounded-lg bg-white p-1.5 text-gray-400 hover:bg-gray-100 hover:text-gray-900 focus:ring-2 focus:ring-gray-300 dark:bg-gray-800 dark:text-gray-500 dark:hover:bg-gray-700 dark:hover:text-white',
                            icon: 'h-5 w-5 shrink-0',
                        },
                    },
                },
            }}
        >
            <Provider store={store}>
                <App />
            </Provider>
        </Flowbite>,
    );
}
