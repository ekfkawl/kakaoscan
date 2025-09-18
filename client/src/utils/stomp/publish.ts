import type {Client, StompHeaders} from '@stomp/stompjs';

export function publishSafe(
    client: Client | null | undefined,
    isConnected: boolean,
    destination: string,
    content?: unknown,
    headers?: StompHeaders,
): void {
    if (!client || !isConnected || !client.connected) return;

    const body =
        content !== undefined
            ? (typeof content === 'string' ? content : JSON.stringify(content))
            : undefined;

    const finalHeaders: StompHeaders = {
        ...(body && typeof content !== 'string' ? { 'content-type': 'application/json' } : {}),
        ...(headers ?? {}),
    };

    client.publish({ destination, body, headers: finalHeaders });
}
