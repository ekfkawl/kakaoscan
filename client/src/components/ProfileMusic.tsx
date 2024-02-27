import React from 'react';
import { Carousel } from 'flowbite-react';
import { MusicInfo } from '../types/profileData';

interface ProfileMusicProps {
    musicInfo: MusicInfo[];
}

const ProfileMusic: React.FC<ProfileMusicProps> = ({ musicInfo }) => {
    return (
        <div className="h-80">
            <Carousel slide={false}>
                {musicInfo.map((music, index) => (
                    <div
                        key={index}
                        className="flex h-full items-center justify-center bg-gray-400 dark:bg-gray-700 text-white"
                        style={{
                            backgroundImage: `url(${music.imageUrl.replace('http://', 'https://')})`,
                            backgroundSize: 'cover',
                            backgroundPosition: 'center',
                        }}
                    >
                        <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
                            <div className="text-center p-4">
                                <p className="font-bold">{music.contentName}</p>
                                <p>{music.artistName}</p>
                            </div>
                        </div>
                    </div>
                ))}
            </Carousel>
        </div>
    );
};

export default ProfileMusic;
