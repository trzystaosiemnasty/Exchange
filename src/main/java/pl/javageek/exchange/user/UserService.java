package pl.javageek.exchange.user;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    User registerNewUserAccount(User user) throws UsernameExistsException {
        if (usernameExist(user.getUsername())) {
            throw new UsernameExistsException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = repository.saveAndFlush(user);
        return user;
    }

    @Transactional
    void updateUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.saveAndFlush(user);
    }

    boolean usernameExist(String username) {
        return repository.findByUsername(username).isPresent();
    }

    @Transactional
    public User sellCurrencies(String currency, int amount, BigDecimal price, User user) {
        val item = user.getPortfolio().stream()
                .filter(p -> p.getKey().equals(currency))
                .findFirst();
        if (item.isPresent()) {
            val currentAmount = item.get().getValue();
            val newAmount = currentAmount - amount;
            if (newAmount < 0) {
                throw new RuntimeException("Not enough money");
            }
            for (val pi : user.getPortfolio()) {
                if (pi.getKey().equals(currency)) {
                    pi.setValue(newAmount);
                }
            }
        } else {
            throw new RuntimeException("Not enough money");
        }
        user.setAvailablePLN(user.getAvailablePLN().add(price));
        return repository.saveAndFlush(user);
    }

    @Transactional
    public User buyCurrencies(String currency, int amount, BigDecimal price, User user) {
        val item = user.getPortfolio().stream().filter(p -> p.getKey().equals(currency)).findFirst();
        if (item.isPresent()) {
            val currentAmount = item.get().getValue();
            val newAmount = currentAmount + amount;
            for (PortfolioItem pi : user.getPortfolio()) {
                if (pi.getKey().equals(currency)) {
                    pi.setValue(newAmount);
                }
            }
        } else {
            user.getPortfolio().add(new PortfolioItem(currency, amount));
        }
        user.setAvailablePLN(user.getAvailablePLN().subtract(price));
        if (user.getAvailablePLN().doubleValue() < 0.0) {
            throw new RuntimeException("Not enough money");
        }
        return repository.saveAndFlush(user);
    }
}