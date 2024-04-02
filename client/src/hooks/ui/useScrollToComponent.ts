import React, { useEffect, useState } from 'react';
import ChannelService from "../../ChannelService";

const useScrollToComponent = (ref: React.RefObject<HTMLElement>) => {
    const [isVisible, setIsVisible] = useState(true);

    const checkVisibility = () => {
        if (ref.current) {
            const rect = ref.current.getBoundingClientRect();
            if (rect.bottom > 0 && rect.top < window.innerHeight) {
                setIsVisible(true);
                ChannelService.showChannelButton();
            }else {
                setIsVisible(false);
                ChannelService.hideChannelButton();
            }
        }
    };

    useEffect(() => {
        window.addEventListener('scroll', checkVisibility);
        checkVisibility();
        return () => {
            window.removeEventListener('scroll', checkVisibility);
        };
    }, [ref]);

    return { isVisible };
};

export default useScrollToComponent;
