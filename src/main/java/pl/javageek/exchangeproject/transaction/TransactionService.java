package pl.javageek.exchangeproject.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.javageek.exchangeproject.currency.CurrenciesDTO;
import pl.javageek.exchangeproject.currency.CurrencyDTO;
import pl.javageek.exchangeproject.exchange.ExchangeService;
import pl.javageek.exchangeproject.user.User;
import pl.javageek.exchangeproject.user.UserService;
import lombok.val;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final String URL = "http://webtask.***************:8068/currencies";

    private final TransactionRepository transactionRepository;

    private final ExchangeService exchangeService;

    private final UserService userService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, ExchangeService exchangeService, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.exchangeService = exchangeService;
        this.userService = userService;
    }

    @Transactional
    User buy(Transaction transaction, User user) {
        val restTemplate = new RestTemplate();
        val currenciesDTO = restTemplate.getForObject(URL, CurrenciesDTO.class);
        val amount = transaction.getAmount();
        val code = transaction.getCode();
        val price = currenciesDTO.getItems().stream()
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .map(CurrencyDTO::getSellPrice)
                .map(p -> p.multiply(new BigDecimal(amount)))
                .orElseThrow(RuntimeException::new);
        exchangeService.sellCurrencies(code, amount, price);
        user = userService.buyCurrencies(code, amount, price, user);
        transactionRepository.saveAndFlush(transaction);
        return user;
    }


    @Transactional
    User sell(Transaction transaction, User user) {
        val restTemplate = new RestTemplate();
        val currenciesDTO = restTemplate.getForObject(URL, CurrenciesDTO.class);
        val amount = transaction.getAmount();
        val code = transaction.getCode();
        val price = currenciesDTO.getItems().stream().filter(c -> c.getCode().equals(code))
                .findFirst()
                .map(CurrencyDTO::getPurchasePrice)
                .map(p -> p.multiply(new BigDecimal(amount)))
                .orElseThrow(RuntimeException::new);
        exchangeService.buyCurrencies(code, amount, price);
        user = userService.sellCurrencies(code, amount, price, user);
        transactionRepository.saveAndFlush(transaction);
        return user;
    }
}
