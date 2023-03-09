package com.kakaoscan.profile.domain.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Getter
@RequiredArgsConstructor
@Component
public class NettyClient {
    @Value("${tcp.port}")
    private int port;

    private static final int READ_TIMEOUT = 0;
    private static final int SEND_TIMEOUT = 1;

    private final NettyClientInstance nettyClientInstance;
    private final NettyClientHandler nettyClientHandler;

    public void connect(Bootstrap bootstrap, String host) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            ch.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(4096));
                            pipeline.addLast(new IdleStateHandler(READ_TIMEOUT, SEND_TIMEOUT, 0), nettyClientHandler);
                        }
                    });
            bootstrap.connect().channel();
        } catch (Exception e) {
            log.error(String.format("[%s server connect fail] ", host) + e);
        }
    }
}
