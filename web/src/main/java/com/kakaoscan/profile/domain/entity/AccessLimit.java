package com.kakaoscan.profile.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDate;

@Entity(name = "tb_access_limit")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccessLimit implements Serializable {
    @Id
    private LocalDate date;

    /**
     * 일 사용 횟수
     */
    private long useCount;
    private long useCount2;
    private long useCount3;
}
