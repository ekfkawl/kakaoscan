export const isEmail = (email: string): boolean => {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
};

export const isPassword = (password: string): boolean => {
    const regex = /^[A-Za-z\d~!@#$%^&*()_+]{8,16}$/;
    return regex.test(password);
};
