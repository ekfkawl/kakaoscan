import React, { PropsWithChildren, useCallback, useEffect, useRef, useState } from 'react';
import SearchBar from '../components/SearchBar/SearchBar';
import Faq from '../components/Faq';
import { Tabs, TabsRef, Toast } from 'flowbite-react';
import { MdInfo } from 'react-icons/md';
import { StompProfile } from '../types/stomp/stompProfile';
import { HiClipboardList, HiPhotograph, HiUserCircle } from 'react-icons/hi';
import Gallery from '../components/Gallery/Gallery';
import { useGalleryItems } from '../hooks/ui/useGalleryItems';
import ProfileCard from '../components/ProfileCard/ProfileCard';
import useScrollToComponent from '../hooks/ui/useScrollToComponent';
import ScrollToTopButton from '../components/ScrollToTopButton';
import usePhoneNumberFormat from '../hooks/formats/usePhoneNumberFormat';
import ProfileThumbPopup from '../components/Popup/ProfileThumbPopup';
import { useProfileData } from '../hooks/profile/useProfileData';
import ConfirmPopup from '../components/Popup/ConfirmPopup';
import { useSubscription } from '../hooks/websocket/useSubscription';
import { useSendMessage } from '../hooks/websocket/useSendMessage';

const HYPHEN_PHONE_NUMBER_LENGTH: number = 13;
const TOAST_DEFAULT_MESSAGE: string = '전화번호 입력 후 엔터 키를 누르면 프로필 조회를 시작합니다.';
const TOAST_SUCCESS_MESSAGE: string = '프로필 조회가 완료되었습니다!';

const SearchPage: React.FC<PropsWithChildren<{}>> = () => {
    const sendMessage = useSendMessage();
    const [stompProfileResponse, setStompProfileResponse] = useState<StompProfile | null>(null);

    const [showSearchConfirmPopup, setShowSearchConfirmPopup] = useState(false);
    const [phoneNumber, setFormattedPhoneNumber, handlePhoneNumberChange] = usePhoneNumberFormat();
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
        data: (stompProfileResponse && stompProfileResponse.jsonContent && stompProfileResponse.content) || '',
        clearProfileItems: clearProfileItems,
        clearBackgroundItems: clearBackgroundItems,
        addProfileItem: addProfileItem,
        addBackgroundItem: addBackgroundItem,
        tabsRef,
        setIsProfileCardVisible,
    });

    const scrollTopRef = useRef<HTMLDivElement>(null);
    const { isVisible: isVisibleScrollToTop } = useScrollToComponent(scrollTopRef);

    useSubscription<StompProfile>('/user/queue/message/search', setStompProfileResponse);
    const handleSendProfile = useCallback(
        (content: string) => {
            sendMessage('/pub/search', { content: content });
        },
        [sendMessage],
    );

    useEffect(() => {
        let timeoutId: NodeJS.Timeout | null = null;
        if (stompProfileResponse?.hasNext) {
            setFormattedPhoneNumber(phoneNumber || stompProfileResponse?.content || '');
            timeoutId = setTimeout(() => handleSendProfile(phoneNumber), 100);
        }

        return () => {
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
        };
    }, [stompProfileResponse, phoneNumber, handleSendProfile]);

    useEffect(() => {
        if (stompProfileResponse && stompProfileResponse.jsonContent && stompProfileResponse.content) {
            tabsRef.current?.setActiveTab(0);
            setIsProfileCardVisible(true);
        }
    }, [stompProfileResponse]);

    const handleConfirmSendMessage = useCallback(() => {
        handleSendProfile(phoneNumber);
        setShowSearchConfirmPopup(false);
    }, [handleSendProfile, phoneNumber]);

    const handleSearchBarKeyPress = useCallback((e: React.KeyboardEvent<HTMLInputElement>) => {
        const trimmedValue = e.currentTarget.value.trim();
        if (e.key === 'Enter' && trimmedValue.length === HYPHEN_PHONE_NUMBER_LENGTH) {
            setShowSearchConfirmPopup(true);
        }
    }, []);

    const [showProfileThumbPopup, setProfileThumbPopup] = useState(false);

    return (
        <div className="relative">
            <SearchBar
                value={phoneNumber}
                onChange={handlePhoneNumberChange}
                onKeyPress={handleSearchBarKeyPress}
                onSearchClick={() => {
                    if (phoneNumber.length === HYPHEN_PHONE_NUMBER_LENGTH) {
                        setShowSearchConfirmPopup(true);
                    }
                }}
            />
            {showSearchConfirmPopup && (
                <ConfirmPopup
                    show={showSearchConfirmPopup}
                    onClose={() => setShowSearchConfirmPopup(false)}
                    title="포인트 차감 안내"
                    description="프로필 조회에 성공하면 500 포인트가 차감됩니다. 계속 진행하시겠어요?"
                    onConfirm={handleConfirmSendMessage}
                    learnMoreLink="/"
                />
            )}
            <MessageToast
                message={
                    (!stompProfileResponse?.jsonContent && stompProfileResponse?.content) ||
                    (stompProfileResponse?.jsonContent && TOAST_SUCCESS_MESSAGE) ||
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
                        storyUrl={profileData.storyWebUrl}
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
                <Tabs.Item title={`프로필 (${profileItems.length})`} icon={HiUserCircle}>
                    <Gallery items={profileItems} />
                    {stompProfileResponse &&
                        stompProfileResponse.jsonContent &&
                        stompProfileResponse.content &&
                        profileItems.length === 0 && (
                            <p className="mt-3 mb-4 max-w-sm text-gray-500 dark:text-gray-400">
                                등록 된 프로필 사진이 없습니다.
                            </p>
                        )}
                </Tabs.Item>
                <Tabs.Item title={`백그라운드 (${backgroundItems.length})`} icon={HiPhotograph}>
                    <Gallery items={backgroundItems} />
                    {stompProfileResponse &&
                        stompProfileResponse.jsonContent &&
                        stompProfileResponse.content &&
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
    );
};

const MessageToast: React.FC<{ message: string }> = ({ message }) => (
    <div className="pt-24 pb-12 md:pt-28 md:pb-14 lg:pt-28 lg:pb-60">
        <div className="flex justify-center">
            <div className="pl-4 text-sm font-normal">
                <Toast>
                    <MdInfo className="h-6 w-6 lg:h-5 lg:w-5 text-[#03C75C] dark:text-blue-500" />
                    <div className="pl-4 text-sm font-normal">{message}</div>
                </Toast>
            </div>
        </div>
    </div>
);

export default SearchPage;
