export interface User {
    name: string;
    email: string;
    profileUrl: string;
}

export interface AuthState {
    isInitialized: boolean;
    token: string | null;
    user: User | null;
}