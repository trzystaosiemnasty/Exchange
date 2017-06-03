package pl.javageek.exchange.transaction;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
class Transaction {

    @Id
    @GeneratedValue
    private Long id;
    private String code;
    private int amount;
}
