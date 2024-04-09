package com.kakaoscan.server.infrastructure.persistence.repository;

import com.kakaoscan.server.infrastructure.logging.enums.LogLevel;
import com.kakaoscan.server.infrastructure.persistence.entity.Log;
import com.querydsl.core.QueryResults;

import java.time.LocalDateTime;

public interface CustomLogRepository {
    QueryResults<Log> findAndFilterLogs(LocalDateTime startDate, LocalDateTime endDate, LogLevel level, String keyword, int page, int pageSize);
}
