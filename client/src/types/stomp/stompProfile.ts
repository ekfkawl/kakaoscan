export interface StompProfile {
    messageId: string;
    email: string;
    content: string | null;
    hasNext: boolean;
    jsonContent: boolean;
    createdAt: string;
}