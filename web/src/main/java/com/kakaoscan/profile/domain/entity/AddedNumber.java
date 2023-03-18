package com.kakaoscan.profile.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "tb_added_list")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AddedNumber implements Serializable {
    @Id
    private String phoneNumberHash;

    @CreationTimestamp
    private LocalDateTime createDt;
}
