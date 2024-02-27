import React, { PropsWithChildren, useCallback, useEffect, useRef, useState } from 'react';
import SearchBar from '../components/SearchBar/SearchBar';
import Faq from '../components/Faq';
import useStomp from '../hooks/websocket/useStomp';
import { Tabs, TabsRef, Toast } from 'flowbite-react';
import { MdInfo } from 'react-icons/md';
import { StompResponse } from '../types/stompResponse';
import { HiClipboardList, HiPhotograph, HiUserCircle } from 'react-icons/hi';
import Gallery from '../components/Gallery/Gallery';
import { useGalleryItems } from '../hooks/ui/useGalleryItems';
import ProfileCard from '../components/ProfileCard/ProfileCard';
import useScrollToComponent from '../hooks/ui/useScrollToComponent';
import ScrollToTopButton from '../components/ScrollToTopButton';
import usePhoneNumberFormat from '../hooks/formats/usePhoneNumberFormat';
import ProfileThumbPopup from '../components/Popup/ProfileThumbPopup';
import { useProfileData } from '../hooks/profile/useProfileData';

const HYPHEN_PHONE_NUMBER_LENGTH: number = 13;
const PHONE_NUMBER_LENGTH: number = 11;
const TOAST_DEFAULT_MESSAGE: string = '전화번호 입력 후 엔터 키를 누르면 프로필 조회를 시작합니다.';
const TOAST_SUCCESS_MESSAGE: string = '프로필 조회가 완료되었습니다!';

const SearchPage: React.FC<PropsWithChildren<{}>> = () => {
    const [receivedMessage, setReceivedMessage] = useState<StompResponse | null>(null);
    const [searchBarText, setSearchBarText] = useState<string>('');
    const [phoneNumber, setFormattedPhoneNumber, handlePhoneNumberChange] = usePhoneNumberFormat();
    const { sendMessage } = useStomp('/ws', setReceivedMessage);
    const tabsRef = useRef<TabsRef>(null);
    const [, setActiveTab] = useState(0);
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
    const [isProfileCardVisible, setIsProfileCardVisible] = useState<boolean>(false);

    const { profileData } = useProfileData({
        json: (receivedMessage && receivedMessage.jsonContent && receivedMessage.content) || '',
        clearProfileItems: clearProfileItems,
        clearBackgroundItems: clearBackgroundItems,
        addProfileItem: addProfileItem,
        addBackgroundItem: addBackgroundItem,
        tabsRef,
        setIsProfileCardVisible,
    });

    const scrollTopRef = useRef<HTMLDivElement>(null);
    const { isVisible: isVisibleScrollToTop } = useScrollToComponent(scrollTopRef);

    const handleSendMessage = useCallback(
        (content: string) => {
            sendMessage('/pub/send', { content });
        },
        [sendMessage],
    );

    const handleSearchBarKeyPress = useCallback(
        (e: React.KeyboardEvent<HTMLInputElement>) => {
            const trimmedValue = e.currentTarget.value.trim();
            if (e.key === 'Enter' && trimmedValue.length === HYPHEN_PHONE_NUMBER_LENGTH) {
                handleSendMessage(trimmedValue);
                setSearchBarText(trimmedValue);
            }
        },
        [handleSendMessage],
    );

    const [showProfileThumbPopup, setProfileThumbPopup] = useState(false);

    useEffect(() => {
        let timeoutId: NodeJS.Timeout | null = null;
        if (receivedMessage?.hasNext) {
            if (receivedMessage.content && receivedMessage.content.length === PHONE_NUMBER_LENGTH) {
                setSearchBarText(receivedMessage.content);
                setFormattedPhoneNumber(receivedMessage.content);
            }
            timeoutId = setTimeout(() => handleSendMessage(searchBarText), 100);
        }

        return () => {
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
        };
    }, [receivedMessage, searchBarText, handleSendMessage, setFormattedPhoneNumber]);

    useEffect(() => {
        if (receivedMessage && receivedMessage.jsonContent && receivedMessage.content) {
            tabsRef.current?.setActiveTab(0);
            setIsProfileCardVisible(true);
        }
    }, [receivedMessage]);

    return (
        <section className="bg-white dark:bg-gray-900">
            <div className="relative">
                <SearchBar
                    value={phoneNumber}
                    onChange={handlePhoneNumberChange}
                    onKeyPress={handleSearchBarKeyPress}
                />
                <MessageToast
                    message={
                        (!receivedMessage?.jsonContent && receivedMessage?.content) ||
                        (receivedMessage?.jsonContent && TOAST_SUCCESS_MESSAGE) ||
                        TOAST_DEFAULT_MESSAGE
                    }
                />
                {isProfileCardVisible && profileData && (
                    <>
                        <ProfileCard
                            profileImageUrl={profileData.profileImageUrl}
                            name={profileData.name}
                            statusMessage={profileData.statusMessage}
                            onImageClick={() => setProfileThumbPopup(true)}
                        />
                        <ProfileThumbPopup
                            show={showProfileThumbPopup}
                            onClose={() => setProfileThumbPopup(false)}
                            storyUrl={profileData.url}
                            profileCaptureUrl={profileData.profileCaptureUrl}
                            phoneNumber={phoneNumber}
                            musicInfo={profileData.musicInfo}
                        />
                    </>
                )}
                <div ref={scrollTopRef}>{!isVisibleScrollToTop && <ScrollToTopButton />}</div>
                <Tabs
                    aria-label="Tabs with underline"
                    style="underline"
                    ref={tabsRef}
                    onActiveTabChange={(tab) => setActiveTab(tab)}
                >
                    <Tabs.Item title="프로필" icon={HiUserCircle}>
                        <Gallery items={profileItems} />
                        {receivedMessage &&
                            receivedMessage.jsonContent &&
                            receivedMessage.content &&
                            profileItems.length === 0 && (
                                <p className="mt-3 mb-4 max-w-sm text-gray-500 dark:text-gray-400">
                                    등록 된 프로필 사진이 없습니다.
                                </p>
                            )}
                    </Tabs.Item>
                    <Tabs.Item title="백그라운드" icon={HiPhotograph}>
                        <Gallery items={backgroundItems} />
                        {receivedMessage &&
                            receivedMessage.jsonContent &&
                            receivedMessage.content &&
                            backgroundItems.length === 0 && (
                                <p className="mt-3 mb-4 max-w-sm text-gray-500 dark:text-gray-400">
                                    등록 된 배경 사진이 없습니다.
                                </p>
                            )}
                    </Tabs.Item>
                    <Tabs.Item active title="FAQ" icon={HiClipboardList}>
                        <Faq />
                    </Tabs.Item>
                </Tabs>
            </div>
        </section>
    );
};

const MessageToast: React.FC<{ message: string }> = ({ message }) => (
    <div className="pt-24 pb-12 md:pt-28 md:pb-14 lg:pt-28 lg:pb-60">
        <div className="flex justify-center">
            <div className="pl-4 text-sm font-normal">
                <Toast>
                    <MdInfo className="h-5 w-5 text-[#03C75C] dark:text-blue-500" />
                    <div className="pl-4 text-sm font-normal">{message}</div>
                </Toast>
            </div>
        </div>
    </div>
);

export default SearchPage;
