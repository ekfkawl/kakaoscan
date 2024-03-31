import React, { useRef, useState } from 'react';
import { Tabs, TabsRef } from 'flowbite-react';
import useScrollToComponent from '../hooks/ui/useScrollToComponent';
import { useGalleryItems } from '../hooks/ui/useGalleryItems';
import { useProfileData } from '../hooks/profile/useProfileData';
import ScrollToTopButton from '../components/ScrollToTopButton';
import { HiPhotograph, HiUserCircle } from 'react-icons/hi';
import Gallery from '../components/Gallery/Gallery';
import { useLocation } from 'react-router-dom';
import ProfileCard from '../components/ProfileCard/ProfileCard';
import ProfileThumbPopup from '../components/Popup/ProfileThumbPopup';

const SearchHistoryDetailPage = () => {
    const location = useLocation();
    const { targetHistory } = location.state || {};
    const tabsRef = useRef<TabsRef>(null);
    const scrollTopRef = useRef<HTMLDivElement>(null);
    const { isVisible: isVisibleScrollToTop } = useScrollToComponent(scrollTopRef);
    const {
        items: profileItems,
        addGalleryItem: addProfileItem,
        clearGalleryItems: clearProfileItems,
    } = useGalleryItems();
    const {
        items: backgroundItems,
        addGalleryItem: addBackgroundItem,
        clearGalleryItems: clearBackgroundItems,
    } = useGalleryItems();
    const [, setActiveTab] = useState(0);
    const { profileData } = useProfileData({
        data: targetHistory,
        clearProfileItems: clearProfileItems,
        clearBackgroundItems: clearBackgroundItems,
        addProfileItem: addProfileItem,
        addBackgroundItem: addBackgroundItem,
        tabsRef,
    });
    const [showProfileThumbPopup, setProfileThumbPopup] = useState(false);

    if (!targetHistory) {
        return (
            <div className="text-center">
                <p className="text-gray-500 dark:text-gray-400 mb-2 text-4xl">🤔</p>
                <p className="text-gray-500 dark:text-gray-400 mb-2">잘못 된 접근입니다.</p>
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-screen-lg">
            <div ref={scrollTopRef}>{!isVisibleScrollToTop && <ScrollToTopButton />}</div>
            <ProfileCard
                profileImageUrl={targetHistory.profile.profileImageUrl}
                name={targetHistory.profile.nickName}
                statusMessage={targetHistory.profile.statusMessage}
                onImageClick={() => setProfileThumbPopup(true)}
            />
            <ProfileThumbPopup
                show={
                    showProfileThumbPopup &&
                    (targetHistory.profile.storyWebUrl || (!!profileData.musicInfo && profileData.musicInfo.length > 0))
                }
                onClose={() => setProfileThumbPopup(false)}
                storyUrl={targetHistory.profile.storyWebUrl}
                phoneNumber={targetHistory.targetPhoneNumber}
                musicInfo={profileData.musicInfo}
            />
            <Tabs
                className="my-1"
                aria-label="Tabs with underline"
                style="underline"
                ref={tabsRef}
                onActiveTabChange={(tab) => setActiveTab(tab)}
            >
                <Tabs.Item title={`프로필 (${profileItems.length})`} icon={HiUserCircle}>
                    <Gallery items={profileItems} />
                    {targetHistory && profileItems.length === 0 && (
                        <p className="mt-3 mb-4 max-w-sm text-gray-500 dark:text-gray-400">
                            등록 된 프로필 사진이 없습니다.
                        </p>
                    )}
                </Tabs.Item>
                <Tabs.Item title={`백그라운드 (${backgroundItems.length})`} icon={HiPhotograph}>
                    <Gallery items={backgroundItems} />
                    {targetHistory && backgroundItems.length === 0 && (
                        <p className="mt-3 mb-4 max-w-sm text-gray-500 dark:text-gray-400">
                            등록 된 배경 사진이 없습니다.
                        </p>
                    )}
                </Tabs.Item>
            </Tabs>
        </div>
    );
};

export default SearchHistoryDetailPage;
