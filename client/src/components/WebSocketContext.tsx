import React, { createContext, ReactNode, useContext, useEffect, useRef, useState } from 'react';
import { Client, IFrame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import store from '../redux/store';
import { getAPIBaseURL } from '../utils/web/url';

interface WebSocketContextType {
    client: Client | null;
    isConnected: boolean;
}

export const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const useWebSocket = (): WebSocketContextType | null => useContext(WebSocketContext);

interface WebSocketProviderProps {
    children: ReactNode;
}

export const WebSocketProvider: React.FC<WebSocketProviderProps> = ({ children }) => {
    const [isConnected, setIsConnected] = useState(false);
    const clientRef = useRef<Client | null>(null);
    const heartbeatRef = useRef<number | null>(null);
    const [, setClient] = useState<Client | null>(null);

    useEffect(() => {
        const connect = () => {
            const newClient = new Client({
                webSocketFactory: () => new SockJS(`${getAPIBaseURL()}/ws`),
                connectHeaders: {
                    Authorization: `Bearer ${store.getState().auth.token ?? ''}`,
                },
                onConnect: () => {
                    setIsConnected(true);
                    startHeartbeat();
                    console.log('connected');
                },
                onDisconnect: () => {
                    setIsConnected(false);
                    console.log('disconnected');
                },
                onStompError: (frame: IFrame) => {
                    setIsConnected(false);
                    setTimeout(connect, 5000);
                    console.error('broker reported error: ' + frame.headers['message']);
                    console.error('details: ' + frame.body);
                },
            });

            newClient.activate();
            clientRef.current = newClient;
            setClient(newClient);
        };

        connect();

        const startHeartbeat = () => {
            if (heartbeatRef.current !== null) {
                clearInterval(heartbeatRef.current);
            }

            const intervalId = setInterval(() => {
                if (clientRef.current && clientRef.current.connected) {
                    clientRef.current.publish({ destination: '/pub/heartbeat' });

                    const subscription = clientRef.current.subscribe('/user/queue/message/heartbeat', (message) => {
                        if (message.body === 'PONG') {
                            subscription.unsubscribe();
                        } else {
                            connect();
                        }
                    });
                } else {
                    connect();
                }
            }, 1000);

            heartbeatRef.current = intervalId as unknown as number;
        };

        return () => {
            if (clientRef.current) {
                clientRef.current.deactivate();
            }
        };
    }, []);

    return (
        <WebSocketContext.Provider value={{ client: clientRef.current, isConnected }}>
            {children}
        </WebSocketContext.Provider>
    );
};
