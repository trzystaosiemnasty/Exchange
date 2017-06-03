package pl.javageek.exchangeproject.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@ToString(exclude = "password")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotEmpty
    @Column(unique=true)
    @JsonIgnore
    private String username;

    @NotNull
    @NotEmpty
    @JsonIgnore
    @Column(length = 60)
    private String password;

    private BigDecimal availablePLN;

    @ElementCollection
    private List<PortfolioItem> portfolio = new LinkedList<>(); //Map would be better, but doesn't work with Thymeleaf

    @ElementCollection
    private Map<String, BigDecimal> prices = new HashMap<>();
}
