package pl.javageek.exchange;

import lombok.val;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.javageek.exchange.ex.Exchange;
import pl.javageek.exchange.ex.ExchangeRepository;

@SpringBootApplication
public class ExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeApplication.class, args);
	}

    @Bean
    CommandLineRunner init(ExchangeRepository exchangeRepository) {
        return (evt) -> {
            val exchange = exchangeRepository.saveAndFlush(new Exchange());
            System.out.println(exchange);
        };
    }
}
