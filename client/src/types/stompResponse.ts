export interface StompResponse {
    messageId: string;
    email: string;
    content: string | null;
    hasNext: boolean;
    createdAt: string;
}