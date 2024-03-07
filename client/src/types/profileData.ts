export interface ProfileData {
    profileCaptureUrl: string;
    profileImageUrl?: string;
    name?: string;
    url?: string;
    statusMessage?: string;
    musicInfo?: MusicInfo[];
}

export interface MusicInfo {
    imageUrl: string;
    contentName: string;
    artistName: string;
    updatedAt: number;
}