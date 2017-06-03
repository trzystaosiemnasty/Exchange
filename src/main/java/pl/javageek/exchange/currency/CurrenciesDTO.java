package pl.javageek.exchange.currency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class CurrenciesDTO {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    private String publicationDate;
    @ElementCollection
    private List<CurrencyDTO> items;

    @Override
    public String toString() {
        return "Currencies{" +
                "items=" + items +
                ", publicationDate='" + publicationDate + '\'' +
                '}';
    }
}
