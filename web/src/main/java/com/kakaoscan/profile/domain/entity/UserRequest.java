package com.kakaoscan.profile.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDate;

@Entity(name = "tb_user_request")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserRequest implements Serializable {
    @Id
    private String email;

    private String remoteAddress;

    private long useCount;

    @UpdateTimestamp
    private LocalDate lastUseDt;

    @OneToOne
    @JoinColumn(name = "email")
    private User user;

    public void setUseCount(long useCount) {
        this.useCount = useCount;
    }
}
