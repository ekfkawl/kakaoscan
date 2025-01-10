package com.kakaoscan.server.infrastructure.persistence.repository;

import com.kakaoscan.server.infrastructure.logging.enums.LogLevel;
import com.kakaoscan.server.infrastructure.persistence.entity.Log;
import com.kakaoscan.server.infrastructure.persistence.entity.QLog;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CustomLogRepositoryImpl implements CustomLogRepository {
    private final JPAQueryFactory factory;

    @Override
    public QueryResults<Log> findAndFilterLogs(LocalDateTime startDate, LocalDateTime endDate, LogLevel level, String keyword, int page, int pageSize) {
        QLog log = QLog.log;

        Timestamp startTimestamp = Timestamp.valueOf(startDate);
        Timestamp endTimestamp = Timestamp.valueOf(endDate);

        BooleanExpression whereCondition = log.date.goe(startTimestamp)
                .and(log.date.loe(endTimestamp)
                .and(level != null ? log.level.eq(level) : null));

        if (keyword != null && !keyword.trim().isEmpty()) {
            BooleanExpression keywordCondition = log.message.likeIgnoreCase("%" + keyword + "%")
                    .or(log.requestId.eq(keyword));

            whereCondition = whereCondition.and(keywordCondition);
        }

        QueryResults<Log> results = factory.selectFrom(log)
                .where(whereCondition)
                .orderBy(log.date.desc())
                .offset((long) (page - 1) * pageSize)
                .limit(pageSize)
                .fetchResults();

        return results;
    }
}