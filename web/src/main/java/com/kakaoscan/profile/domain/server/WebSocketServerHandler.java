package com.kakaoscan.profile.domain.server;

import com.kakaoscan.profile.domain.bridge.BridgeInstance;
import com.kakaoscan.profile.domain.bridge.ClientQueue;
import com.kakaoscan.profile.domain.client.NettyClientInstance;
import com.kakaoscan.profile.domain.enums.MessageSendType;
import com.kakaoscan.profile.domain.enums.RecordType;
import com.kakaoscan.profile.domain.kafka.service.KafkaProducerService;
import com.kakaoscan.profile.domain.model.KafkaMessage;
import com.kakaoscan.profile.domain.model.UseCount;
import com.kakaoscan.profile.domain.respon.enums.Role;
import com.kakaoscan.profile.domain.server.exception.InvalidAccess;
import com.kakaoscan.profile.domain.service.AccessLimitService;
import com.kakaoscan.profile.domain.service.UserRequestService;
import com.kakaoscan.profile.global.oauth.OAuthAttributes;
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
@Component
@RequiredArgsConstructor
public class WebSocketServerHandler extends TextWebSocketHandler {
    @Value("${kakaoscan.all.date.maxcount}")
    private int allLimitCount;

    @Value("${kakaoscan.server.count}")
    private long serverCount;

    @Value("${kakaoscan.user.date.maxcount}")
    private int userLimitCount;

    private static final int EVG_WAITING_SEC = 20;
    private static final int REQUEST_TIMEOUT_TICK = 5 * 1000;

    private static final Map<WebSocketSession, String> clientsRemoteAddress = new ConcurrentHashMap<>();

    private final NettyClientInstance nettyClientInstance;
    private final AccessLimitService accessLimitService;
    private final UserRequestService userRequestService;
    private final BridgeInstance bi;
    private final KafkaProducerService producerService;

    public String getRemoteAddress(WebSocketSession session) {
        Map<String, Object> map = session.getAttributes();
        return (String) map.get("remoteAddress");
    }

    public OAuthAttributes getOAuthUser(WebSocketSession session) {
        Map<String, Object> map = session.getAttributes();
        Object userObj = map.get("user");

        if (userObj instanceof OAuthAttributes) {
            return (OAuthAttributes) userObj;
        } else {
            return null;
        }
    }

    public void removeSessionHash(WebSocketSession session) {
        bi.getClients().remove(session.getId());
        clientsRemoteAddress.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {

            String receive = message.getPayload();
            if (receive.length() == 0) {
                return;
            }

            OAuthAttributes user = getOAuthUser(session);
            if (user == null) {
                throw new InvalidAccess(MessageSendType.USER_NOT_FOUND.getType());
            }
            if (Role.GUEST.equals(user.getRole())) {
                throw new InvalidAccess(MessageSendType.USER_NO_PERMISSION.getType());
            }

            ClientQueue clientQueue = bi.getClients().get(session.getId());
            if (clientQueue == null) {
                return;
            }
            if (clientQueue.getLastSendTick() != 0 && System.currentTimeMillis() > clientQueue.getLastSendTick()) {
                throw new InvalidAccess(MessageSendType.REQUEST_TIME_OUT.getType());
            }

            if (isNumeric(receive) && receive.length() == 11) { // receive phone number

                // 전체 일일 사용 제한
                UseCount useCount = accessLimitService.getUseCount();
                if (useCount.getTotalCount() >= allLimitCount * serverCount) {
                    throw new InvalidAccess(MessageSendType.ACCESS_LIMIT.getType());
                }

                // 클라이언트 일일 사용 제한
                if (userRequestService.getUseCount(getOAuthUser(session).getEmail()) >= userLimitCount) {
                    throw new InvalidAccess(String.format(MessageSendType.LOCAL_ACCESS_LIMIT.getType(), userLimitCount));
                }

                // put turn
                if (clientQueue.getRequestTick() == Long.MAX_VALUE) {
                    bi.getClients().put(session.getId(), new ClientQueue(System.currentTimeMillis(), 0, System.currentTimeMillis() + REQUEST_TIMEOUT_TICK, receive, "", false, false));
                }

            } else if (MessageSendType.HEARTBEAT.getType().equals(receive)) {
                // update
                clientQueue.setLastSendTick(System.currentTimeMillis() + REQUEST_TIMEOUT_TICK);
                bi.getClients().put(session.getId(), clientQueue);

                long turn = bi.getTurn(session.getId());

                // 남은 평균 시간
                String viewMessage = String.format(MessageSendType.REMAINING_QUEUE.getType(), turn, turn * EVG_WAITING_SEC);

                if (turn == 0) {

                    if (!clientQueue.isConnected()) {
                        clientQueue.setConnected(true);
                        nettyClientInstance.connect(0, session.getId());
                    }

                    // check connected
                    clientQueue = bi.getClients().get(session.getId());
                    if (clientQueue.isFail()) {
                        throw new InvalidAccess(String.format(MessageSendType.SERVER_INSTANCE_NOT_RUN.getType()));
                    }

                    // time out
                    if (clientQueue.getLastReceivedTick() != 0) {
                        if (System.currentTimeMillis() > clientQueue.getLastReceivedTick()) {
                            throw new InvalidAccess(String.format(MessageSendType.REQUEST_TIME_OUT.getType()));
                        }
                    } else {
                        clientQueue.setLastReceivedTick(System.currentTimeMillis() + REQUEST_TIMEOUT_TICK);
                        bi.getClients().put(session.getId(), clientQueue);
                    }

                    // send (세션/메세지타입/번호/아이피해시/이메일)
                    if (clientQueue.getRequest().length() > 0) {
                        bi.socketSend(String.format("[%s]%s:%s<%s>(%s)", session.getId(), MessageSendType.PROFILE.getType(), clientQueue.getRequest(), getRemoteAddress(session), user.getEmail()));
                    }

                    viewMessage = MessageSendType.TURN_LOCAL.getType();

                    ClientQueue queue = bi.getClients().get(session.getId());
                    // check server response
                    if (queue.getResponse().length() > 0) {
                        viewMessage = queue.getResponse();
                        removeSessionHash(session);

                        producerService.send(user.getEmail(), new KafkaMessage(RecordType.UPSERT, viewMessage, queue.getRequest()));
                    }
                }

                session.sendMessage(new TextMessage(viewMessage));
            }
        } catch (InvalidAccess e) {
            session.sendMessage(new TextMessage(e.getMessage()));
            removeSessionHash(session);
        } catch (Exception e) {
            log.error("[socket message handler error] " + e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String remoteAddress = getRemoteAddress(session);

        if (remoteAddress.length() != 32) {
            session.sendMessage(new TextMessage(MessageSendType.EMPTY_IP.getType()));
            return;
        }

        // 동일한 아이피 접속 체크
        for (Map.Entry<WebSocketSession, String> ss : clientsRemoteAddress.entrySet()) {
            if (remoteAddress.equals(ss.getValue())) {
                ss.getKey().sendMessage(new TextMessage(MessageSendType.CONNECT_CLOSE_IP.getType()));
                removeSessionHash(ss.getKey());
            }
        }

        bi.getClients().put(session.getId(), new ClientQueue(Long.MAX_VALUE, 0, 0, "", "", false, false));

        clientsRemoteAddress.put(session, remoteAddress);

        log.info("[web client connect] " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeSessionHash(session);

        log.info("[web client disconnect] " + session.getId());
    }
}