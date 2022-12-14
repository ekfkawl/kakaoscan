package com.kakaoscan.profile.domain.server;

import com.kakaoscan.profile.domain.bridge.BridgeInstance;
import com.kakaoscan.profile.domain.bridge.ClientQueue;
import com.kakaoscan.profile.domain.client.NettyClientInstance;
import com.kakaoscan.profile.domain.enums.MessageSendType;
import com.kakaoscan.profile.domain.model.UseCount;
import com.kakaoscan.profile.domain.service.AccessLimitService;
import com.kakaoscan.profile.domain.service.UserRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.kakaoscan.profile.utils.StrUtils.isNumeric;

@Log4j2
@RequiredArgsConstructor
@Component
public class WebSocketServerHandler extends TextWebSocketHandler {

    private final NettyClientInstance nettyClientInstance;
    private final AccessLimitService accessLimitService;
    private final UserRequestService userRequestService;
    private final BridgeInstance bi;

    private static final Map<WebSocketSession, String> clientsRemoteAddress = new ConcurrentHashMap<>();

    private static final int EVG_WAITING_SEC = 20;
    private static final int REQUEST_TIMEOUT_TICK = 50 * 1000;

    @Value("${kakaoscan.all.date.maxcount}")
    private int allLimitCount;

    @Value("${kakaoscan.server.count}")
    private long serverCount;

    @Value("${kakaoscan.user.date.maxcount}")
    private int userLimitCount;

    public String getRemoteAddress(WebSocketSession session) {
        Map<String, Object> map = session.getAttributes();
        return (String) map.get("remoteAddress");
    }

    public void removeSessionHash(WebSocketSession session) {
        bi.getClients().remove(session.getId());
        clientsRemoteAddress.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String receive = message.getPayload();

        if (receive.length() == 0) {
            return;
        }

        if (isNumeric(receive) && receive.length() == 11) { // receive phone number
            // ?????? ?????? ?????? ??????
            UseCount useCount = accessLimitService.getUseCount();
            if (useCount.getTotalCount() >= allLimitCount * serverCount) {
                session.sendMessage(new TextMessage(MessageSendType.ACCESS_LIMIT.getType()));
                return;
            }

            // ??????????????? ?????? ?????? ??????
            if (userRequestService.getUseCount(getRemoteAddress(session)) >= userLimitCount) {
                session.sendMessage(new TextMessage(String.format(MessageSendType.LOCAL_ACCESS_LIMIT.getType(), userLimitCount)));
                return;
            }

            long connectTick = bi.getClients().get(session.getId()).getRequestTick();

            if (connectTick == Long.MAX_VALUE) {
                bi.getClients().put(session.getId(), new ClientQueue(System.currentTimeMillis(), Long.MAX_VALUE, receive, "", false, false));
            }

        } else if (MessageSendType.HEARTBEAT.getType().equals(receive)) {
            try {
                long turn = bi.getTurn(session.getId());

                ClientQueue clientQueue = bi.getClients().get(session.getId());

                // ?????? ?????? ??????
                String viewMessage = String.format(MessageSendType.REMAINING_QUEUE.getType(), turn, turn * EVG_WAITING_SEC);

                if (turn == 0) {

                    UseCount useCount = accessLimitService.getUseCount();

                    int connectIndex = 0;
                    for (int i = 0; i < useCount.getCount().length; i++) {
                        if (useCount.getCount()[i] < allLimitCount) {
                            connectIndex = i;
                            break;
                        }
                    }

                    if (!clientQueue.isConnected()) {
                        clientQueue.setConnected(true);
                        nettyClientInstance.connect(connectIndex, session.getId());
                    }

                    // time out
                    if (clientQueue.getConnectedTick() != Long.MAX_VALUE) {
                        if (System.currentTimeMillis() > clientQueue.getConnectedTick() + REQUEST_TIMEOUT_TICK) {
                            session.sendMessage(new TextMessage(MessageSendType.REQUEST_TIME_OUT.getType()));
                            removeSessionHash(session);
                            return;
                        }
                    } else {
                        clientQueue.setConnectedTick(System.currentTimeMillis());
                        bi.getClients().put(session.getId(), clientQueue);
                    }

                    clientQueue = bi.getClients().get(session.getId());
                    if (clientQueue.isFail()) {
                        session.sendMessage(new TextMessage(MessageSendType.SERVER_INSTANCE_NOT_RUN.getType()));
                        removeSessionHash(session);
                        return;
                    }

                    // send phone number
                    if (clientQueue.getRequest().length() > 0) {
                        bi.socketSend(String.format("[%s]%s:%s<%s>", session.getId(), MessageSendType.PROFILE.getType(), clientQueue.getRequest(), getRemoteAddress(session)));
                    }

                    viewMessage = MessageSendType.TURN_LOCAL.getType();

                    String res = bi.getClients().get(session.getId()).getResponse();
                    // check server response
                    if (res.length() > 0) {
                        viewMessage = res;

                        removeSessionHash(session);
                    }
                }

                session.sendMessage(new TextMessage(viewMessage));
            } catch (Exception e) {
                session.sendMessage(new TextMessage(MessageSendType.REQUEST_TIME_OUT.getType()));
                removeSessionHash(session);

                log.error("[socket message handler error] " + e);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String remoteAddress = getRemoteAddress(session);

        if (remoteAddress.length() != 32) {
            session.sendMessage(new TextMessage(MessageSendType.EMPTY_IP.getType()));
            return;
        }

        // ????????? ????????? ?????? ??????
        for (Map.Entry<WebSocketSession, String> ss : clientsRemoteAddress.entrySet()) {
            if (remoteAddress.equals(ss.getValue())) {
                ss.getKey().sendMessage(new TextMessage(MessageSendType.CONNECT_CLOSE_IP.getType()));
                removeSessionHash(ss.getKey());
            }
        }

        bi.getClients().put(session.getId(), new ClientQueue(Long.MAX_VALUE, Long.MAX_VALUE, "", "", false, false));

        clientsRemoteAddress.put(session, remoteAddress);

        log.info("[web client connect] " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeSessionHash(session);

        log.info("[web client disconnect] " + session.getId());
    }
}