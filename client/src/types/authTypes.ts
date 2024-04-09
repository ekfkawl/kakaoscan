export interface User {
    role: string;
    email: string;
    profileUrl: string;
}

export interface AuthState {
    isInitialized: boolean;
    token: string | null;
    user: User | null;
}