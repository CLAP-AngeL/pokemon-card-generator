package com.petproject.pokemoncardgenerator.services.generator;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.petproject.pokemoncardgenerator.model.Pokemon;
import com.petproject.pokemoncardgenerator.model.PokemonParameters;
import com.petproject.pokemoncardgenerator.services.ai.ServiceForGeneratingGenerativeAIResults;
import com.petproject.pokemoncardgenerator.services.renderer.CardRenderer;

/**
 * uniting service for generating parameters for card and generating card images themselves
 */
@Service
public class CardProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CardProcessor.class);

	private final CardGenerator cardGenerator;
	private final CardRenderer cardRenderer;

	private final ServiceForGeneratingGenerativeAIResults serviceForGeneratingAIResults;

	public CardProcessor(CardGenerator cardGenerator, CardRenderer cardRenderer,
			ServiceForGeneratingGenerativeAIResults serviceForGeneratingAIResults) {
		this.cardGenerator = cardGenerator;
		this.cardRenderer = cardRenderer;
		this.serviceForGeneratingAIResults = serviceForGeneratingAIResults;
	}

	public List<BufferedImage> generateCards(PokemonParameters parameters) {
		LOGGER.info("Input parameters for generating pokemon cards: {}\n Starting to generate pokemons...", parameters);

		List<Pokemon> pokemonSeries = cardGenerator.generatePokemons(parameters);
		List<BufferedImage> pokemonCards = new ArrayList<>();

		LOGGER.info("Starting to generate pokemon cards...");
		for (Pokemon pokemon : pokemonSeries) {
			BufferedImage pokemonImage = serviceForGeneratingAIResults.generatePokemonImage(pokemon.getImagePrompt());
			if (pokemonImage != null) {
				pokemonCards.add(cardRenderer.renderCard(pokemon, pokemonImage));
			}
		}

		LOGGER.info("Finished generating pokemon cards");
		return pokemonCards.stream().filter(Objects::nonNull).toList();
	}
}
