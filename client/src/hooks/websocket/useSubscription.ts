import { useEffect, useState } from 'react';
import { useWebSocket } from '../../components/WebSocketContext';

export function useSubscription<T>(dest: string, onMessage: (data: T) => void) {
    const context = useWebSocket();
    const [attempt, setAttempt] = useState(0);

    useEffect(() => {
        if (context?.client && context.isConnected && context.client.connected) {
            const subscription = context.client.subscribe(dest, (message) => {
                const data: T = JSON.parse(message.body);
                onMessage(data);
            });

            return () => subscription.unsubscribe();
        } else {
            const timer = setTimeout(() => {
                setAttempt(attempt + 1);
            }, 250);

            return () => clearTimeout(timer);
        }
    }, [context?.client, context?.isConnected, dest, onMessage, attempt]);
}
