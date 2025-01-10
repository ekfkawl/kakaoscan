package com.kakaoscan.server.domain.search.model;

import com.kakaoscan.server.domain.search.entity.NewPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewNumberSearch implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int count;

    private String numbers;

    public NewNumberSearch(List<NewPhoneNumber> newPhoneNumbers) {
        StringBuilder numbers = new StringBuilder();
        for (NewPhoneNumber newPhoneNumber : newPhoneNumbers) {
            numbers.append(newPhoneNumber.getTargetPhoneNumber());
        }

        this.count = newPhoneNumbers.size();
        this.numbers = numbers.toString();
    }
}
