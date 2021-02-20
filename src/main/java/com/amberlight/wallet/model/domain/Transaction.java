package com.amberlight.wallet.model.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * A domain object representing a transaction.
 */
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "transactions")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="wallet_id")
    private Wallet wallet;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "date")
    private Date date;

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", wallet=" + wallet +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }

}
