export const formatPhoneNumber = (inputValue: string): string => {
    if (!inputValue) return inputValue;

    const numbersAndHyphens = inputValue.replace(/[^\d-]/g, '');
    const numbers = numbersAndHyphens.replace(/-/g, '');

    if (numbers.length < 4) return numbers;
    if (numbers.length < 7) return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;

    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
};

export const timeSince = (dateString: string): string => {
    const date = new Date(dateString);
    const seconds = Math.floor((new Date().getTime() - date.getTime()) / 1000);

    let interval = seconds / 31536000;

    if (interval > 1) {
        return Math.floor(interval) + "년 전";
    }
    interval = seconds / 2592000;
    if (interval > 1) {
        return Math.floor(interval) + "개월 전";
    }
    interval = seconds / 86400;
    if (interval > 1) {
        return Math.floor(interval) + "일 전";
    }
    interval = seconds / 3600;
    if (interval > 1) {
        return Math.floor(interval) + "시간 전";
    }
    interval = seconds / 60;
    if (interval > 1) {
        return Math.floor(interval) + "분 전";
    }

    return Math.floor(seconds) + "초 전";
};
