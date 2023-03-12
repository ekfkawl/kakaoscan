package com.kakaoscan.profile.domain.model;

import com.kakaoscan.profile.domain.enums.RecordType;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessage {
    @NonNull
    private RecordType type;
    @NonNull
    private String message;
    private String subMessage;

    public KafkaMessage(@NonNull RecordType type, @NonNull String message) {
        this.type = type;
        this.message = message;
    }
}