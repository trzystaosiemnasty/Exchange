package pl.javageek.exchangeproject.exchange;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class Exchange {
    @Id
    private Long id;

    private BigDecimal availablePLN;

    @ElementCollection
    private Map<String, Integer> portfolio;

    public Exchange() {
        this.id = 1L;
        this.availablePLN = new BigDecimal("1000");
        Map<String, Integer> portfolio = new HashMap<>();
        portfolio.put("USD", 1000);
        portfolio.put("EUR", 2000);
        portfolio.put("CHF", 3000);
        portfolio.put("RUB", 4000);
        portfolio.put("CZK", 5000);
        portfolio.put("GBP", 6000);
        this.portfolio = portfolio;
    }
}
