import React from 'react';

interface ProfileCardProps {
    profileImageUrl: string;
    name: string;
    url: string;
    statusMessage: string;
}

const ProfileCard: React.FC<ProfileCardProps> = ({ profileImageUrl, name, url, statusMessage }) => {
    return (
        <div className="flex flex-col items-center pb-8 sm:flex-row">
            <img
                className="mx-auto mb-4 w-24 h-24 rounded-full sm:ml-0 sm:mr-6"
                src={profileImageUrl || '/default-profile.jpg'}
                alt="avatar"
            />
            <div className="text-center sm:text-left">
                <h3 className="text-xl font-bold tracking-tight text-gray-900 dark:text-white">{name}</h3>
                <div className="text-gray-500 dark:text-gray-400">
                    <a href={url} target="_blank" rel="noopener noreferrer">
                        {url}
                    </a>
                </div>
                <p className="mt-3 mb-4 max-w-sm text-gray-500 dark:text-gray-400">{statusMessage}</p>
            </div>
        </div>
    );
};

export default ProfileCard;
