package com.kakaoscan.profile.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UseCount {
    private long[] count;
    private long totalCount;
}
