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
import ProfileCard from '../components/ProfileCard';
import timestampToDate from '../utils/datetime/convert';
import useScrollToComponent from '../hooks/ui/useScrollToComponent';
import ScrollToTopButton from '../components/ScrollToTopButton';
import usePhoneNumberFormat from '../hooks/formats/usePhoneNumberFormat';

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
    const [profileData, setProfileData] = useState({
        profileImageUrl: '',
        name: '',
        url: '',
        statusMessage: '',
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
            const profileJson = JSON.parse(receivedMessage.content);
            setProfileData({
                // profileImageUrl: `data:image/bmp;base64,${profileJson.profile.screenBase64}`,
                profileImageUrl: profileJson.profile.profileImageUrl,
                name: profileJson.profile.nickName || '이름 없음',
                url: profileJson.profile.storyWebUrl || '',
                statusMessage: profileJson.profile.statusMessage || '상태메세지 없음',
            });

            clearProfileItems();
            clearBackgroundItems();

            renderFeeds(profileJson.profile.profileFeeds.feeds, addProfileItem);
            renderFeeds(profileJson.profile.backgroundFeeds.feeds, addBackgroundItem);

            tabsRef.current?.setActiveTab(0);
            setIsProfileCardVisible(true);
        }
    }, [receivedMessage, addProfileItem, addBackgroundItem, clearProfileItems, clearBackgroundItems]);

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
                    subHtml: `<p>${feed.isCurrent ? '현재 프로필' : timestampToDate(feed.updatedAt)}</p>`,
                });
            });
        });
    }

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
                    <ProfileCard
                        profileImageUrl={profileData.profileImageUrl}
                        name={profileData.name}
                        url={profileData.url}
                        statusMessage={profileData.statusMessage}
                    />
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
                    </Tabs.Item>
                    <Tabs.Item title="백그라운드" icon={HiPhotograph}>
                        <Gallery items={backgroundItems} />
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
