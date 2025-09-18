import {useCallback} from 'react';
import {useWebSocket} from '../../providers/StompProvider';
import type {StompHeaders} from '@stomp/stompjs';
import {publishSafe} from "../../utils/stomp/publish"; // ← 추가

export const useSendMessage = () => {
    const context = useWebSocket();

    return useCallback(
        (destination: string, content?: unknown, headers?: StompHeaders) => {
            publishSafe(context?.client ?? null, !!context?.isConnected, destination, content, headers);
        },
        [context?.client, context?.isConnected, context?.client?.connected],
    );
};
