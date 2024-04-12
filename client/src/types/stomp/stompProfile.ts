export interface StompProfile {
    messageId: string;
    email: string;
    content: string | null;
    reconnectContent: string | null;
    hasNext: boolean;
    jsonContent: boolean;
    createdAt: string;
}