import { useCallback, useEffect, useRef, useState } from 'react';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import store from '../../redux/store';
import { StompResponse } from '../../types/stompResponse';

function useStomp(url: string, onMessageReceived: (message: StompResponse) => void) {
    const clientRef = useRef<Client | null>(null);
    const subscriptionRef = useRef<StompSubscription | null>(null);
    const [isConnected, setIsConnected] = useState(false);

    const connect = useCallback(() => {
        if (clientRef.current) {
            return;
        }

        const client = new Client({
            connectHeaders: {
                Authorization: `Bearer ${store.getState().auth.token ?? ''}`,
            },
            webSocketFactory: () => new SockJS(`${process.env.REACT_APP_API_URL}${url}`),
            reconnectDelay: 1000,
            onConnect: () => {
                setIsConnected(true);
                subscriptionRef.current = client.subscribe('/user/queue/message', (message: IMessage) => {
                    const messageData: StompResponse = JSON.parse(message.body);
                    onMessageReceived(messageData);
                });
                console.log('connected');
            },
            onStompError: (frame) => {
                console.error('stomp error:', frame);
            },
            onDisconnect: () => {
                setIsConnected(false);
                console.log('disconnected');
            },
        });

        clientRef.current = client;
        client.activate();
    }, [url, onMessageReceived]);

    const disconnect = useCallback(() => {
        if (clientRef.current) {
            if (subscriptionRef.current) {
                subscriptionRef.current.unsubscribe();
                subscriptionRef.current = null;
            }
            clientRef.current.deactivate();
            clientRef.current = null;
            setIsConnected(false);
        }
    }, []);

    useEffect(() => {
        connect();
        return () => {
            disconnect();
        };
    }, [connect, disconnect]);

    const sendMessage = useCallback(
        (destination: string, body: any) => {
            if (clientRef.current && isConnected) {
                clientRef.current.publish({ destination, body: JSON.stringify(body) });
            }
        },
        [isConnected],
    );

    return { sendMessage, isConnected };
}

export default useStomp;
