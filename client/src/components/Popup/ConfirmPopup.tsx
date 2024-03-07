import { Button, Modal } from 'flowbite-react';
import { FC } from 'react';

interface ConfirmPopupProps {
    show: boolean;
    onClose: () => void;
    title: string;
    description: string;
    learnMoreLink?: string;
    onConfirm: () => void;
}

const ConfirmPopup: FC<ConfirmPopupProps> = ({ show, onClose, title, description, learnMoreLink, onConfirm }) => {
    return (
        <>
            {show && (
                <Modal
                    show={show}
                    position="center"
                    size="lg"
                    theme={{
                        content: {
                            inner: 'relative rounded-lg bg-white shadow dark:bg-gray-800 flex flex-col max-h-[90vh]',
                        },
                    }}
                >
                    <Modal.Body className="p-9">
                        <div className="mb-4 text-sm text-gray-500 dark:text-gray-400">
                            <h3 className="mb-3 text-2xl font-bold text-gray-900 dark:text-white">{title}</h3>
                            <p>{description}</p>
                        </div>

                        <div className="items-center justify-between space-y-4 pt-4 sm:flex sm:space-y-0">
                            <a
                                href={learnMoreLink}
                                className="font-medium text-primary-600 hover:underline dark:text-primary-500"
                            >
                                {(learnMoreLink && '자세히 알아보기') || ''}
                            </a>
                            <div className="items-center space-y-4 sm:flex sm:space-x-4 sm:space-y-0">
                                <Button
                                    color="gray"
                                    onClick={onClose}
                                    size="sm"
                                    className="w-full dark:bg-gray-700 dark:enabled:hover:bg-gray-600 [&>span]:dark:bg-gray-700 [&>span]:dark:enabled:hover:bg-gray-600"
                                >
                                    취소
                                </Button>
                                <Button
                                    onClick={() => {
                                        onConfirm();
                                        onClose();
                                    }}
                                    size="sm"
                                    className="w-full"
                                >
                                    확인
                                </Button>
                            </div>
                        </div>
                    </Modal.Body>
                </Modal>
            )}
        </>
    );
};

export default ConfirmPopup;
