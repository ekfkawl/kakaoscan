import React, {createContext, ReactNode, useContext, useEffect, useRef, useState} from 'react';
import {useSelector} from 'react-redux';
import {Client, IFrame, StompSubscription} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type {RootState} from '../redux/store';
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

    const hbSubRef = useRef<StompSubscription | null>(null);
    const hbTimerRef = useRef<number | null>(null);
    const lastPongAtRef = useRef<number>(0);

    const clearAppHeartbeat = () => {
        if (hbTimerRef.current !== null) {
            clearInterval(hbTimerRef.current);
            hbTimerRef.current = null;
        }
        if (hbSubRef.current) {
            try { hbSubRef.current.unsubscribe(); } catch {}
            hbSubRef.current = null;
        }
    };

    useEffect(() => {
        if (!isInitialized) return;

        if (!token) {
            if (client) {
                const toClose = client;
                setClient(null);
                setIsConnected(false);
                clearAppHeartbeat();
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

                if (!hbSubRef.current) {
                    hbSubRef.current = c.subscribe('/user/queue/message/heartbeat', (msg) => {
                        if (genRef.current !== myGen) return;
                        if (msg.body === 'PONG') {
                            lastPongAtRef.current = Date.now();
                        }
                    });
                }

                if (hbTimerRef.current === null) {
                    lastPongAtRef.current = Date.now();
                    hbTimerRef.current = window.setInterval(() => {
                        if (c.connected) {
                            c.publish({ destination: '/pub/heartbeat' });
                            const now = Date.now();
                            if (now - lastPongAtRef.current > 40000) {
                                c.deactivate().finally(() => c.activate());
                            }
                        }
                    }, 1000) as unknown as number;
                }
            },

            onDisconnect: () => {
                if (genRef.current !== myGen) return;
                setIsConnected(false);
                clearAppHeartbeat();
            },
            onWebSocketClose: () => {
                if (genRef.current !== myGen) return;
                setIsConnected(false);
                clearAppHeartbeat();
            },
            onWebSocketError: () => {
                if (genRef.current !== myGen) return;
                setIsConnected(false);
                clearAppHeartbeat();
            },
            onStompError: (frame: IFrame) => {
                if (genRef.current !== myGen) return;
                setIsConnected(false);
                clearAppHeartbeat();
                console.error('STOMP error:', frame.headers['message'], frame.body);
            },
        });

        c.activate();
        setClient(c);

        return () => {
            if (genRef.current === myGen) {
                setIsConnected(false);
                setClient(null);
                clearAppHeartbeat();
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
