import React, { useCallback, useEffect, useState } from 'react';
import timestampToDate from '../../utils/datetime/convert';
import { TabsRef } from 'flowbite-react';
import { MusicInfo, ProfileData } from '../../types/profileData';

export interface UseProfileDataProps {
    json: string;
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
    json,
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
        url: '',
    });

    const addMusicInfo = useCallback((newMusicInfo: MusicInfo) => {
        setProfileData((prevState) => ({
            ...prevState,
            musicInfo: [...(prevState.musicInfo || []), newMusicInfo],
        }));
    }, []);

    useEffect(() => {
        if (!json) {
            return;
        }

        const profileJson = JSON.parse(json);
        setProfileData({
            profileCaptureUrl: `data:image/bmp;base64,${profileJson.profile.screenBase64}`,
            profileImageUrl: profileJson.profile.profileImageUrl || '/default-profile.jpg',
            name: profileJson.profile.nickName || '이름 없음',
            url: profileJson.profile.storyWebUrl,
            statusMessage: profileJson.profile.statusMessage || '상태메세지 없음',
        });

        if (profileJson.profile.musics && profileJson.profile.musics.contentsInfo) {
            profileJson.profile.musics.contentsInfo.forEach((music: any) => {
                addMusicInfo({
                    contentName: music.contentName,
                    artistName: music.artistList.map((artist: any) => artist.artistName).join(', '),
                    imageUrl: music.contentImgPath,
                    updatedAt: music.updatedAt
                });
            });
        }

        clearProfileItems();
        clearBackgroundItems();

        if (profileJson.profile.profileFeeds && profileJson.profile.profileFeeds.feeds) {
            renderFeeds(profileJson.profile.profileFeeds.feeds, addProfileItem);
        }
        if (profileJson.profile.backgroundFeeds && profileJson.profile.backgroundFeeds.feeds) {
            renderFeeds(profileJson.profile.backgroundFeeds.feeds, addBackgroundItem);
        }

        if (setIsProfileCardVisible) {
            setIsProfileCardVisible(true);
        }
        tabsRef.current?.setActiveTab(0);
    }, [
        json,
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
