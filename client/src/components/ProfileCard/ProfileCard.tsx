import React from 'react';
import './ProfileCard.css';

interface ProfileCardProps {
    profileImageUrl?: string;
    name?: string;
    statusMessage?: string;
    onImageClick?: () => void;
}

const ProfileCard: React.FC<ProfileCardProps> = ({ profileImageUrl, name, statusMessage, onImageClick }) => {
    return (
        <div className="flex flex-col items-center pb-8 sm:flex-row">
            <img
                className="mx-auto mb-4 w-24 h-24 rounded-full sm:ml-0 sm:mr-6 profile-img"
                src={profileImageUrl || '/default-profile.jpg'}
                alt="avatar"
                onClick={onImageClick}
            />
            <div className="text-center sm:text-left">
                <h3 className="text-xl font-bold tracking-tight text-gray-900 dark:text-white">
                    {name || '이름 없음'}
                </h3>
                <p className="mt-3 mb-4 max-w-sm text-gray-500 dark:text-gray-400">
                    {statusMessage || '상태메세지 없음'}
                </p>
            </div>
        </div>
    );
};

export default ProfileCard;
