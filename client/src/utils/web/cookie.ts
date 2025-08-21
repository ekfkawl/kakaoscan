import { Cookies } from 'react-cookie';

const cookies = new Cookies();

export const setCookie = (name: string, value: string, option?: any) => {
    return cookies.set(name, value, { ...option });
};

export const getCookie = (name: string) => {
    return cookies.get(name);
};

export const hasRefreshHint = (): boolean => {
    return document.cookie
        .split(';')
        .some(c => c.trim().startsWith('rt_present=1'));
};