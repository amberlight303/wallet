package com.amberlight.wallet.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO structure for transactions.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String id;

    @NotNull
    private Integer walletId;

    @NotNull
    @Positive
    @Digits(integer = 20, fraction = 2)
    private BigDecimal amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDto that = (TransactionDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "id='" + id + '\'' +
                ", walletId=" + walletId +
                ", amount=" + amount +
                '}';
    }
}
