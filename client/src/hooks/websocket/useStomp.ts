import { useCallback, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function useStomp(url: string) {
    const clientRef = useRef<Client | null>(null);

    const connect = useCallback(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS(url),
            reconnectDelay: 3000,
            onConnect: () => {
                console.log('connected');
            },
            onStompError: (frame) => {
                console.error('error:', frame);
            },
            onDisconnect: () => {
                console.log('disconnected');
            },
        });

        clientRef.current = client;
        client.activate();
    }, [url]);

    const handleVisibilityChange = useCallback(() => {
        if (document.visibilityState === 'visible' && clientRef.current) {
            console.log('check connection...');
            if (!clientRef.current.active) {
                console.log('reconnecting to server...');
                connect();
            }
        }
    }, [connect]);

    useEffect(() => {
        connect();
        document.addEventListener('visibilitychange', handleVisibilityChange);

        return () => {
            if (clientRef.current) {
                clientRef.current.deactivate().catch((err) => console.error('error client:', err));
            }
            document.removeEventListener('visibilitychange', handleVisibilityChange);
        };
    }, [connect, handleVisibilityChange]);

    const sendMessage = (destination: string, body: any) => {
        clientRef.current?.publish({ destination, body: JSON.stringify(body) });
    };

    return { sendMessage };
}

export default useStomp;
