import React, { createContext, ReactNode, useContext, useEffect, useState } from 'react';
import { Client, IFrame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import store from '../redux/store';

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
    const [client, setClient] = useState<Client | null>(null);
    const [isConnected, setIsConnected] = useState(false);

    useEffect(() => {
        const connect = () => {
            const newClient = new Client({
                webSocketFactory: () => new SockJS(`${process.env.REACT_APP_API_URL}/ws`),
                connectHeaders: {
                    Authorization: `Bearer ${store.getState().auth.token ?? ''}`,
                },
                onConnect: () => {
                    setIsConnected(true);
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
            setClient(newClient);
        };

        connect();

        return () => {
            if (client) {
                client.deactivate();
            }
        };
    }, []);

    return <WebSocketContext.Provider value={{ client, isConnected }}>{children}</WebSocketContext.Provider>;
};
