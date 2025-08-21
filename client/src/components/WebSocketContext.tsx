import React, { createContext, ReactNode, useContext, useEffect, useRef, useState } from 'react';
import { useSelector } from 'react-redux';
import { Client, IFrame } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { RootState } from '../redux/store';
import {getAPIBaseURL} from "../utils/web/url";

interface WebSocketContextType {
    client: Client | null;
    isConnected: boolean;
}

export const WebSocketContext = createContext<WebSocketContextType | null>(null);
export const useWebSocket = () => useContext(WebSocketContext);

interface Props { children: ReactNode; }

export const WebSocketProvider: React.FC<Props> = ({ children }) => {
    const token = useSelector((s: RootState) => s.auth.token ?? '');
    const isInitialized = useSelector((s: RootState) => s.auth.isInitialized);

    const [client, setClient] = useState<Client | null>(null);
    const [isConnected, setIsConnected] = useState(false);

    const genRef = useRef(0);

    useEffect(() => {
        if (!isInitialized) return;

        if (!token) {
            if (client) {
                const toClose = client;
                setClient(null);
                setIsConnected(false);
                toClose.deactivate().catch(() => {});
            }
            return;
        }

        const myGen = ++genRef.current;

        const c = new Client({
            webSocketFactory: () => new SockJS(`${getAPIBaseURL()}/ws`),
            connectHeaders: { Authorization: `Bearer ${token}` },

            heartbeatOutgoing: 10000,
            heartbeatIncoming: 10000,

            reconnectDelay: 3000,

            debug: () => {},

            onConnect: () => {
                if (genRef.current !== myGen) return;
                setIsConnected(true);
            },
            onDisconnect: () => {
                if (genRef.current !== myGen) return;
                setIsConnected(false);
            },
            onWebSocketClose: () => {
                if (genRef.current !== myGen) return;
                setIsConnected(false);
            },
            onWebSocketError: () => {
                if (genRef.current !== myGen) return;
                setIsConnected(false);
            },
            onStompError: (frame: IFrame) => {
                if (genRef.current !== myGen) return;
                setIsConnected(false);
                console.error('STOMP error:', frame.headers['message'], frame.body);
            },
        });

        c.activate();
        setClient(c);

        return () => {
            if (genRef.current === myGen) {
                setIsConnected(false);
                setClient(null);
                c.deactivate().catch(() => {});
            }
        };
    }, [token, isInitialized]);

    return (
        <WebSocketContext.Provider value={{ client, isConnected }}>
            {children}
        </WebSocketContext.Provider>
    );
};
