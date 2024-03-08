import React, { useCallback, useEffect, useState } from 'react';
import { TabsRef } from 'flowbite-react';
import { MusicInfo, ProfileData } from '../../types/profileData';
import { timestampToDate } from '../../utils/datetime/convert';

export interface UseProfileDataProps {
    data: any;
    clearProfileItems: () => void;
    clearBackgroundItems: () => void;
    addProfileItem: (item: any) => void;
    addBackgroundItem: (item: any) => void;
    tabsRef: React.RefObject<TabsRef>;
    setIsProfileCardVisible?: (isVisible: boolean) => void;
}

function renderFeeds(feeds: any[], addItem: (item: any) => void) {
    feeds.forEach((feed: { id: string; contents: any[]; extra?: any; updatedAt: number; isCurrent: boolean }) => {
        feed.contents.forEach((content) => {
            let srcUrl = content.value;
            if (srcUrl.length === 0) {
                return;
            }

            if (feed.extra && feed.extra.originalAnimatedBackgroundImageUrl) {
                srcUrl = feed.extra.originalAnimatedBackgroundImageUrl;
            }

            addItem({
                id: feed.id,
                src: srcUrl,
                thumb: content.value,
                subHtml: `<p>${timestampToDate(feed.updatedAt)}</p>`,
            });
        });
    });
}

export const useProfileData = ({
    data,
    clearProfileItems,
    clearBackgroundItems,
    addProfileItem,
    addBackgroundItem,
    tabsRef,
    setIsProfileCardVisible,
}: UseProfileDataProps) => {
    const [profileData, setProfileData] = useState<ProfileData>({
        musicInfo: [],
        name: '',
        profileCaptureUrl: '',
        profileImageUrl: '',
        statusMessage: '',
        storyWebUrl: '',
    });

    const addMusicInfo = useCallback((newMusicInfo: MusicInfo) => {
        setProfileData((prevState) => ({
            ...prevState,
            musicInfo: [...(prevState.musicInfo || []), newMusicInfo],
        }));
    }, []);

    useEffect(() => {
        if (!data) {
            return;
        }

        let profileData;
        if (typeof data === 'string') {
            profileData = JSON.parse(data);
        } else {
            profileData = data;
        }

        setProfileData({
            profileCaptureUrl: `data:image/bmp;base64,${profileData.profile.screenBase64}`,
            profileImageUrl: profileData.profile.profileImageUrl || '/default-profile.jpg',
            name: profileData.profile.nickName || '이름 없음',
            storyWebUrl: profileData.profile.storyWebUrl,
            statusMessage: profileData.profile.statusMessage || '상태메세지 없음',
        });

        if (profileData.profile.musics && profileData.profile.musics.contentsInfo) {
            profileData.profile.musics.contentsInfo.forEach((music: any) => {
                addMusicInfo({
                    contentName: music.contentName,
                    artistName: music.artistList.map((artist: any) => artist.artistName).join(', '),
                    imageUrl: music.contentImgPath,
                    updatedAt: music.updatedAt,
                });
            });
        }

        clearProfileItems();
        clearBackgroundItems();

        if (profileData.profile.profileFeeds && profileData.profile.profileFeeds.feeds) {
            renderFeeds(profileData.profile.profileFeeds.feeds, addProfileItem);
        }
        if (profileData.profile.backgroundFeeds && profileData.profile.backgroundFeeds.feeds) {
            renderFeeds(profileData.profile.backgroundFeeds.feeds, addBackgroundItem);
        }

        if (setIsProfileCardVisible) {
            setIsProfileCardVisible(true);
        }
        tabsRef.current?.setActiveTab(0);
    }, [
        data,
        addMusicInfo,
        clearProfileItems,
        clearBackgroundItems,
        addProfileItem,
        addBackgroundItem,
        setIsProfileCardVisible,
        tabsRef,
    ]);

    return { profileData };
};
