export interface User {
    role: string;
    email: string;
    items: UserItem[];
    profileUrl: string;
    authenticationType: 'LOCAL' | 'GOOGLE';
}

export interface AuthState {
    isInitialized: boolean;
    token: string | null;
    user: User | null;
}

export interface UserItem {
    productType: string;
    productName: string;
    expiredAt: string;
}

export interface UserWrapper extends User {
    hasSnapshotPreservation: boolean;
}