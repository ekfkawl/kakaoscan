package com.kakaoscan.server.infrastructure.service;

import com.kakaoscan.server.application.dto.response.AppLogs;
import com.kakaoscan.server.infrastructure.logging.enums.LogLevel;
import com.kakaoscan.server.infrastructure.persistence.entity.Log;
import com.kakaoscan.server.infrastructure.persistence.repository.LogRepository;
import com.querydsl.core.QueryResults;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoggingService {
    private final LogRepository logRepository;

    @Transactional(readOnly = true)
    public AppLogs findAndFilterLogs(LocalDateTime startDate, LocalDateTime endDate, LogLevel level, String keyword, int page, int pageSize) {
        QueryResults<Log> logQueryResults = logRepository.findAndFilterLogs(startDate, endDate, level, keyword, page, pageSize);

        List<Log> logs = logQueryResults.getResults();

        return AppLogs.convertToAppLogs(logs, logQueryResults.getTotal());
    }
}
