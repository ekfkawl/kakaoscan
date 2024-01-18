package com.kakaoscan.server.application.port;

import com.kakaoscan.server.domain.search.model.InvalidPhoneNumber;

public interface PhoneNumberCachePort {
    void cacheInvalidPhoneNumber(String phoneNumber, InvalidPhoneNumber invalidPhoneNumber);
    boolean isInvalidPhoneNumberCached(String phoneNumber);
}
