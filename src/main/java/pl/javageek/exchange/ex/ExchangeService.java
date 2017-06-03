package pl.javageek.exchange.ex;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;

    @Autowired
    public ExchangeService(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    public Exchange getExchange() {
        return exchangeRepository.findOne(1L);
    }

    @Transactional
    public void sellCurrencies(String currency, int amount, BigDecimal price) {
        val exchange = getExchange();
        exchange.setAvailablePLN(exchange.getAvailablePLN().add(price));
        val currentAmount = exchange.getPortfolio().get(currency);
        exchange.getPortfolio().put(currency, currentAmount - amount);
        if (currentAmount - amount < 0) {
            throw new RuntimeException("Exchange doesn't have enough money");
        }
        exchangeRepository.saveAndFlush(exchange);
    }

    @Transactional
    public void buyCurrencies(String currency, int amount, BigDecimal price) {
        Exchange exchange = getExchange();
        exchange.setAvailablePLN(exchange.getAvailablePLN().subtract(price));
        if (exchange.getAvailablePLN().doubleValue() < 0.0) {
            throw new RuntimeException("Exchange doesn't have enough money");
        }
        val currentAmount = exchange.getPortfolio().get(currency);
        exchange.getPortfolio().put(currency, currentAmount + amount);
        exchangeRepository.saveAndFlush(exchange);
    }
}
