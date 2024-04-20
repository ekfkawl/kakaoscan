import React from 'react';
import { FaCopy } from 'react-icons/fa';
import { useCopyToClipboard } from '../hooks/ui/useCopyToClipboard';

interface CopyButtonProps {
    displayText: React.ReactNode;
    textToCopy: string;
}

const CopyButton: React.FC<CopyButtonProps> = ({ displayText, textToCopy }) => {
    const { isClicked, copyToClipboard } = useCopyToClipboard();

    return (
        <p className="flex items-center">
            {displayText}
            <span
                className={`ml-1 mb-1 text-sm ${
                    isClicked ? 'text-green-300 dark:text-green-400' : 'text-gray-300 dark:text-gray-400'
                }`}
            >
                <FaCopy className="cursor-pointer" onClick={() => copyToClipboard(textToCopy)} />
            </span>
        </p>
    );
};

export default CopyButton;
