package com.kakaoscan.profile.domain.entity;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "tb_cache")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Cache implements Serializable {
    /**
     * 전화번호
     */
    @Id
    private String phoneNumber;
    /**
     * 수정 날짜
     */
    @UpdateTimestamp
    private LocalDateTime modifyDt;
}
