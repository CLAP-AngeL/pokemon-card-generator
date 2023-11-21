package com.petproject.pokemoncardgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PokemonCardGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokemonCardGeneratorApplication.class, args);
	}

}
