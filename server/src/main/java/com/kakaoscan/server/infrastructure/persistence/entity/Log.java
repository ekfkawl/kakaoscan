package com.kakaoscan.server.infrastructure.persistence.entity;

import com.kakaoscan.server.infrastructure.logging.enums.LogLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "logs", indexes = {
        @Index(name = "idx_request_id", columnList = "requestId")
})
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Timestamp date;

    @Column(nullable = false)
    private String logger;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private LogLevel level;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String exception;

    private String threadName;

    private String requestId;

    protected Log() {
    }
}
