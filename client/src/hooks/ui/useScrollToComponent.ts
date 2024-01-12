import React, { useEffect, useState } from 'react';

const useScrollToComponent = (ref: React.RefObject<HTMLElement>) => {
    const [isVisible, setIsVisible] = useState(true);

    const checkVisibility = () => {
        if (ref.current) {
            const rect = ref.current.getBoundingClientRect();
            setIsVisible(rect.bottom > 0 && rect.top < window.innerHeight);
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
