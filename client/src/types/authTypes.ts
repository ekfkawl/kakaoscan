export interface User {
    role: string;
    email: string;
    profileUrl: string;
    authenticationType: 'LOCAL' | 'GOOGLE';
}

export interface AuthState {
    isInitialized: boolean;
    token: string | null;
    user: User | null;
}