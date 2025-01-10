package com.kakaoscan.server.infrastructure.constants;

public class RedisKeyPrefixes {
    public static final String LOCK_USER_POINTS_KEY_PREFIX = "userPointsLock:";
    public static final String LOCK_PEND_POINTS_PAYMENT_KEY_PREFIX = "pendPointsPaymentLock:";
    public static final String LOCK_SNAPSHOT_PRESERVATION_PAYMENT_KEY_PREFIX = "snapshotPreservationPaymentLock:";

    public static final String EVENT_KEY_PREFIX = "eventStatus:";
    public static final String POINT_CACHE_KEY_PREFIX = "pointCache:";
    public static final String TARGET_SEARCH_COST_KEY_PREFIX = "targetSearchCost:";
    public static final String INVALID_PHONE_NUMBER_KEY_PREFIX = "invalidPhoneNumber:";
}
