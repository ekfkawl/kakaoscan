import { useCallback } from 'react';
import { useWebSocket } from '../../components/WebSocketContext';

function useSendMessage() {
    const context = useWebSocket();

    return useCallback(
        (destination: string, content?: any) => {
            if (context?.client && context.client.connected && context.isConnected) {

                context.client.publish({
                    destination,
                    body: JSON.stringify(content),
                });
            }
        },
        [context?.client, context?.isConnected, context?.client?.connected],
    );
}

export default useSendMessage;
