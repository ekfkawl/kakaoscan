import React, { FC, useState } from 'react';
import { Button, Modal } from 'flowbite-react';
import { MdCheck } from 'react-icons/md';
import { MusicInfo } from '../../types/profileData';
import ProfileMusic from '../ProfileMusic';

interface ProfileThumbPopupProps {
    show: boolean;
    onClose: () => void;
    profileCaptureUrl?: string;
    storyUrl?: string;
    phoneNumber?: string;
    musicInfo?: MusicInfo[];
}

const ProfileThumbPopup: FC<ProfileThumbPopupProps> = ({
    show,
    onClose,
    profileCaptureUrl,
    storyUrl,
    phoneNumber,
    musicInfo,
}) => {
    const [isCopied, setIsCopied] = useState(false);

    return (
        <>
            {show && (
                <Modal
                    onClose={onClose}
                    popup
                    show={show}
                    size="sm"
                    position="center"
                    theme={{
                        content: {
                            inner: 'relative rounded-lg bg-white shadow dark:bg-gray-800 flex flex-col max-h-[90vh] w-full sm:max-w-md',
                        },
                    }}
                >
                    <Modal.Header />
                    <Modal.Body>
                        {profileCaptureUrl && (
                            <div className="flex justify-center">
                                <img src={profileCaptureUrl} alt="" />
                            </div>
                        )}
                        <div className="my-2">{musicInfo && <ProfileMusic musicInfo={musicInfo} />}</div>
                        <hr className="my-6 border-gray-200 dark:border-gray-700 sm:mx-auto" />
                        <div className="flex justify-end space-x-2">
                            {navigator.clipboard && phoneNumber && (
                                <Button
                                    onClick={() =>
                                        navigator.clipboard.writeText(phoneNumber).then(() => {
                                            setIsCopied(true);
                                            setTimeout(() => setIsCopied(false), 500);
                                        })
                                    }
                                >
                                    {isCopied ? <MdCheck /> : '번호/아이디 복사'}
                                </Button>
                            )}
                            {storyUrl && <Button onClick={() => window.open(storyUrl, '_blank')}>카카오스토리</Button>}
                        </div>
                    </Modal.Body>
                </Modal>
            )}
        </>
    );
};

export default ProfileThumbPopup;
