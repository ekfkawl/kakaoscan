import {useEffect, useRef} from 'react';
import {StompSubscription} from '@stomp/stompjs';
import {useWebSocket} from '../../providers/StompProvider';

export function useSubscription<T>(
    dest: string,
    onMessage: (data: T) => void
): void {
    const context = useWebSocket();
    const handlerRef = useRef(onMessage);
    const subRef = useRef<StompSubscription | null>(null);

    useEffect(() => {
        handlerRef.current = onMessage;
    }, [onMessage]);

    useEffect(() => {
        const client = context?.client;
        const connected = !!context?.isConnected && !!client?.connected;

        if (!connected || !client) {
            if (subRef.current) {
                try { subRef.current.unsubscribe(); } catch {}
                subRef.current = null;
            }
            return;
        }

        if (subRef.current) return;

        const subId = `sub:${dest}`;
        subRef.current = client.subscribe(dest, (message) => {
            try {
                const body = (message.body ?? '').trim();
                if (!body) return;
                const data = JSON.parse(body) as T;
                handlerRef.current?.(data);
            } catch (err) {
                console.error('[useSubscription] message handling error:', err);
            }
        }, { id: subId, ack: 'auto' });

        return () => {
            if (subRef.current) {
                try { subRef.current.unsubscribe(); } catch {}
                subRef.current = null;
            }
        };
    }, [context?.client, context?.isConnected, dest]);
}
