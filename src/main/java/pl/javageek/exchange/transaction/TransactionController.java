package pl.javageek.exchange.transaction;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pl.javageek.exchange.currency.CurrenciesDTO;
import pl.javageek.exchange.currency.CurrencyDTO;
import pl.javageek.exchange.ex.Exchange;
import pl.javageek.exchange.ex.ExchangeService;
import pl.javageek.exchange.user.User;
import pl.javageek.exchange.user.UserRepository;

import java.security.Principal;
import java.util.stream.Collectors;

@RestController
public class TransactionController {

    private final UserRepository repository;

    private final ExchangeService exchangeService;

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(UserRepository repository, ExchangeService exchangeService, TransactionService transactionService) {
        this.repository = repository;
        this.exchangeService = exchangeService;
        this.transactionService = transactionService;
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public Object getByUsername(Principal principal) {
        val username = principal.getName();
        val optUser = repository.findByUsername(username).orElseThrow(RuntimeException::new);
        return getPrices(optUser);
    }

    private User getPrices(User user) {
        val restTemplate = new RestTemplate();
        val URL = "http://webtask.***************:8068/currencies";
        val currenciesDTO = restTemplate.getForObject(URL, CurrenciesDTO.class);
        val prices = currenciesDTO.getItems().stream()
                .collect(Collectors.toMap(CurrencyDTO::getCode, CurrencyDTO::getPurchasePrice));
        user.setPrices(prices);
        return user;
    }

    @RequestMapping(value = "exchange", method = RequestMethod.GET)
    public Exchange getExchange() {
        return exchangeService.getExchange();
    }

    @RequestMapping(value = "buy", method = RequestMethod.POST)
    public User buy(@RequestBody Transaction transaction, Principal principal) {
        if (transaction.getAmount() <= 0) {
            throw new RuntimeException("Amount can't be zero or lower");
        }
        System.out.println(transaction);
        val username = principal.getName();
        val optUser = repository.findByUsername(username);
        val user = transactionService.buy(transaction, optUser.orElseThrow(RuntimeException::new));
        return getPrices(user);
    }

    @RequestMapping(value = "sell", method = RequestMethod.POST)
    public User sell(@RequestBody Transaction transaction, Principal principal) {
        if (transaction.getAmount() <= 0) {
            throw new RuntimeException("Amount can't be zero or lower");
        }
        System.out.println(transaction);
        val username = principal.getName();
        val optUser = repository.findByUsername(username);
        val user = transactionService.sell(transaction, optUser.orElseThrow(RuntimeException::new));
        return getPrices(user);
    }
}
