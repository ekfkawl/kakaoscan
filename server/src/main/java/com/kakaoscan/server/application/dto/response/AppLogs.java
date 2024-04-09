package com.kakaoscan.server.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kakaoscan.server.infrastructure.logging.enums.LogLevel;
import com.kakaoscan.server.infrastructure.persistence.entity.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppLogs {
    private final List<AppLogResponse> appLogList = new ArrayList<>();
    private long totalCount;

    public void addLog(AppLogResponse appLogResponse) {
        this.appLogList.add(appLogResponse);
    }

    public static AppLogs convertToAppLogs(List<Log> logs, long totalCount) {
        AppLogs appLogs = new AppLogs();

        logs.forEach(log -> {
            AppLogResponse appLogResponse = new AppLogResponse(
                    log.getId(),
                    log.getDate().toLocalDateTime(),
                    log.getLogger(),
                    log.getLevel(),
                    log.getMessage(),
                    log.getException(),
                    log.getThreadName(),
                    log.getRequestId()
            );
            appLogs.addLog(appLogResponse);
        });

        appLogs.totalCount = totalCount;

        return appLogs;
    }

    @Getter
    @AllArgsConstructor
    public static class AppLogResponse {
        private long id;
        private LocalDateTime date;
        private String logger;
        private LogLevel level;
        private String message;
        private String exception;
        private String threadName;
        private String requestId;
    }
}
