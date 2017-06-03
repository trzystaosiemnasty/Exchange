package pl.javageek.exchange.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@Embeddable
class PortfolioItem {

    private String key;
    private Integer value;

    PortfolioItem() {
        this.key = "";
        this.value = null;
    }
}
