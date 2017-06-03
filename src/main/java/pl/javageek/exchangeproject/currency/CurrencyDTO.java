package pl.javageek.exchangeproject.currency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class CurrencyDTO {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    private String name;
    private String code;
    private Long unit;
    private BigDecimal purchasePrice;
    private BigDecimal sellPrice;
    private BigDecimal averagePrice;

    @Override
    public String toString() {
        return "Currency{" +
                "averagePrice=" + averagePrice +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", unit=" + unit +
                ", purchasePrice=" + purchasePrice +
                ", sellPrice=" + sellPrice +
                '}';
    }
}
