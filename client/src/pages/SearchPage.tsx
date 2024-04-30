import React, { PropsWithChildren, useCallback, useEffect, useRef, useState } from 'react';
import SearchBar from '../components/SearchBar/SearchBar';
import Faq from '../components/Faq';
import { Label, Radio, Tabs, TabsRef, Toast } from 'flowbite-react';
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
import { useFetchData } from '../hooks/useFetchData';
import { SearchCostResponse } from '../types/searchCost';
import { formatDate } from '../utils/format/format';
import MessagePopup from '../components/Popup/MessagePopup';

const TOAST_DEFAULT_MESSAGE: string = '전화번호 입력 후 엔터 키를 누르면 프로필 조회를 시작합니다.';
const TOAST_SUCCESS_MESSAGE: string = '프로필 조회가 완료되었습니다!';

const SearchPage: React.FC<PropsWithChildren<{}>> = () => {
    const sendMessage = useSendMessage();
    const [stompProfileResponse, setStompProfileResponse] = useState<StompProfile | null>(null);

    const [showSearchConfirmPopup, setShowSearchConfirmPopup] = useState(false);
    const [showSearchConfigPopup, setShowSearchConfigPopup] = useState(false);
    const [isSearchId, setIsSearchId] = useState(false);
    const [phoneNumber, setPhoneNumber, , handleChangeForNumber, handleChangeForId] = usePhoneNumberFormat();
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

    useEffect(() => {
        if (stompProfileResponse && stompProfileResponse.jsonContent && stompProfileResponse.content) {
            tabsRef.current?.setActiveTab(0);
            setIsProfileCardVisible(true);
        }
    }, [stompProfileResponse]);

    const handleConfirmSendMessage = useCallback(() => {
        sendMessage('/pub/search', { content: phoneNumber, isId: isSearchId });
        setShowSearchConfirmPopup(false);
    }, [isSearchId, phoneNumber, sendMessage]);

    const {
        data: searchCost,
        isLoading,
        error,
        fetchData,
    } = useFetchData<SearchCostResponse | null>('/api/search-cost', null, false);
    const handleOnSearch = (targetPhoneNumber: string) => {
        fetchData({ targetPhoneNumber: targetPhoneNumber, isId: isSearchId.toString() });
        setShowSearchConfirmPopup(true);
    };

    const handleSearchBarKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
        const trimmedValue = e.currentTarget.value.trim();
        if (e.key === 'Enter') {
            if ((isSearchId && trimmedValue.length >= 2) || (!isSearchId && trimmedValue.length === 13)) {
                handleOnSearch(trimmedValue);
            }
        }
    };

    const [showProfileThumbPopup, setProfileThumbPopup] = useState(false);

    return (
        <div className="relative">
            <SearchBar
                placeholder={isSearchId ? '아이디 입력' : '전화번호 입력'}
                maxLength={isSearchId ? 20 : 13}
                value={phoneNumber}
                onChange={isSearchId ? handleChangeForId : handleChangeForNumber}
                onKeyPress={handleSearchBarKeyPress}
                onSearchClick={() => {
                    handleOnSearch(phoneNumber);
                }}
                onMenuClick={() => {
                    setShowSearchConfigPopup(true);
                }}
            />
            {showSearchConfigPopup && (
                <MessagePopup
                    show={showSearchConfigPopup}
                    onClose={() => setShowSearchConfigPopup(false)}
                    title="설정"
                    description={
                        <SearchConfig
                            isSearchId={isSearchId}
                            setIsSearchId={setIsSearchId}
                            setPhoneNumber={setPhoneNumber}
                        />
                    }
                    onConfirm={() => {}}
                />
            )}
            {showSearchConfirmPopup && (
                <ConfirmPopup
                    show={showSearchConfirmPopup}
                    onClose={() => setShowSearchConfirmPopup(false)}
                    title="포인트 차감 안내"
                    description={
                        !isLoading && searchCost?.success ? (
                            <div className="break-all">
                                <p>
                                    프로필 조회에 성공하면{' '}
                                    <strong>
                                        {new Intl.NumberFormat('ko-KR').format(searchCost.data.cost)} 포인트
                                    </strong>
                                    가 차감됩니다. 계속 진행하시겠어요?
                                </p>
                                {renderDiscountMessage(searchCost)}
                            </div>
                        ) : (
                            <p>프로필 조회 비용을 불러올 수 없습니다. 올바른 번호 형식이 아닙니다.</p>
                        )
                    }
                    onConfirm={handleConfirmSendMessage}
                    learnMoreLink="/policy"
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
                <Tabs.Item title={`배경 (${backgroundItems.length})`} icon={HiPhotograph}>
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
            <div className="pr-1 pl-1 text-sm font-normal">
                <Toast>
                    <MdInfo className="h-6 w-6 lg:h-5 lg:w-5 text-[#03C75C] dark:text-blue-500" />
                    <div className="break-all pl-4 text-sm font-normal">{message}</div>
                </Toast>
            </div>
        </div>
    </div>
);

const renderDiscountMessage = (searchCostResponse: SearchCostResponse): React.ReactNode => {
    if (!searchCostResponse.data.expiredAtDiscount) return null;

    const formattedDate = formatDate(new Date(searchCostResponse.data.expiredAtDiscount));
    const messages: { [key: string]: string } = {
        DISCOUNT: `* ${formattedDate}까지,\n해당 번호의 프로필 조회 비용이 50% 할인됩니다!`,
        FREE: `* ${formattedDate}까지,\n해당 번호의 프로필 조회 비용이 무료입니다!`,
    };

    return <p className="mt-4">{messages[searchCostResponse.data.costType]}</p>;
};

interface SearchOptionsProps {
    isSearchId: boolean;
    setIsSearchId: (value: boolean) => void;
    setPhoneNumber: (value: string) => void;
}

const SearchConfig: React.FC<SearchOptionsProps> = ({ isSearchId, setIsSearchId, setPhoneNumber }) => {
    return (
        <fieldset className="flex max-w-md flex-col gap-4">
            <legend className="mb-4">조회 옵션을 선택해주세요.</legend>
            <div className="flex items-center gap-2">
                <Radio
                    id="search-number"
                    name="search-option"
                    value="Number"
                    checked={!isSearchId}
                    onChange={() => {
                        setIsSearchId(false);
                        setPhoneNumber('');
                    }}
                />
                <Label htmlFor="search-number">전화번호</Label>
            </div>
            <div className="flex items-center gap-2">
                <Radio
                    id="search-id"
                    name="search-option"
                    value="Id"
                    checked={isSearchId}
                    onChange={() => {
                        setIsSearchId(true);
                        setPhoneNumber('');
                    }}
                />
                <Label htmlFor="search-id">아이디</Label>
            </div>
        </fieldset>
    );
};

export default SearchPage;
