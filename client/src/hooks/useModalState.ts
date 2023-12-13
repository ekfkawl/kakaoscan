import { useState } from 'react';

const useModalState = (initialState: boolean = false) => {
    const [isOpen, setIsOpen] = useState<boolean>(initialState);

    const openModal = () => setIsOpen(true);
    const closeModal = () => setIsOpen(false);

    return { isOpen, openModal, closeModal };
};

export default useModalState;
