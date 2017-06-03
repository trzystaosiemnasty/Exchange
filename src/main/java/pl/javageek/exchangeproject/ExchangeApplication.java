package pl.javageek.exchangeproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.javageek.exchangeproject.exchange.Exchange;
import pl.javageek.exchangeproject.exchange.ExchangeRepository;

@SpringBootApplication
public class ExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeApplication.class, args);
	}

    @Bean
    CommandLineRunner init(ExchangeRepository exchangeRepository) {
        return (evt) -> {
            Exchange exchange = exchangeRepository.saveAndFlush(new Exchange());
            System.out.println(exchange);
        };
    }
}
