import { useState } from 'react';

interface UseCopyToClipboard {
    isClicked: boolean;
    copyToClipboard: (text: string) => void;
}

export const useCopyToClipboard = (): UseCopyToClipboard => {
    const [isClicked, setIsClicked] = useState(false);

    const copyToClipboard = (text: string) => {
        navigator.clipboard
            .writeText(text)
            .then(() => {
                setIsClicked(true);
                setTimeout(() => setIsClicked(false), 500);
            })
            .catch((err) => console.error('copy failed', err));
    };

    return { isClicked, copyToClipboard };
};
