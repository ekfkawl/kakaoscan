import React, { PropsWithChildren } from 'react';
import SearchBar from '../components/searchbar/SearchBar';

const AuthPage: React.FC<PropsWithChildren<{}>> = () => {
    return (
        <section className="bg-white dark:bg-gray-900">
            <div className="relative grid grid-cols-1 min-h-screen">

                <SearchBar />

            </div>
        </section>
    );
};

export default AuthPage;
