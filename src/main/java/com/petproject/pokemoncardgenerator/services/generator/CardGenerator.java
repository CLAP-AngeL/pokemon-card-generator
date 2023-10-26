package com.petproject.pokemoncardgenerator.services.generator;

import static com.petproject.pokemoncardgenerator.Constants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.petproject.pokemoncardgenerator.model.Pokemon;
import com.petproject.pokemoncardgenerator.model.PokemonParameters;
import com.petproject.pokemoncardgenerator.model.Style;
import com.petproject.pokemoncardgenerator.model.details.Ability;
import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;
import com.petproject.pokemoncardgenerator.model.details.enums.Rarity;
import com.petproject.pokemoncardgenerator.services.ai.ServiceForGeneratingGenerativeAIResults;

@Service
public class CardGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(CardGenerator.class);

	private final StyleGenerator styleGenerator;
	private final AbilityNameLoader abilityNameLoader;
	private final ServiceForGeneratingGenerativeAIResults serviceForGeneratingAIResults;

	public CardGenerator(StyleGenerator styleGenerator, AbilityNameLoader abilityNameLoader,
			ServiceForGeneratingGenerativeAIResults serviceForGeneratingAIResults) {
		this.styleGenerator = styleGenerator;
		this.abilityNameLoader = abilityNameLoader;
		this.serviceForGeneratingAIResults = serviceForGeneratingAIResults;
	}

	public List<Pokemon> generatePokemons(PokemonParameters parameters) {
		int generationAmount = ThreadLocalRandom.current().nextInt(1, 3);

		CardGenerator.LOGGER.info("Generating pokemon {} card(s) of {}", generationAmount,
				parameters.getElement().getElementName());
		return generateSeries(parameters.getElement(), generationAmount, parameters.getPokemonConcept());
	}

	private List<Pokemon> generateSeries(PokemonElement element, int n, String pokemonConceptOverride) {
		List<Pokemon> pokemons = new ArrayList<>();

		int rarityRange = Math.max(Rarity.values().length - n, 0);
		int startingRarityIndex = rarityRange > 0 ? ThreadLocalRandom.current().nextInt(0, rarityRange) : 0;

		Style cardStyle = null;

		for (int i = 0; i < n; i++) {
			int rarityIndex = Math.min(Rarity.values().length - 1, startingRarityIndex + i);
			Pokemon pokemon = generatePokemonCard(element, Rarity.values()[rarityIndex], cardStyle, n > 1 ? i : null,
					pokemonConceptOverride);

			if (i == 0) {
				cardStyle = pokemon.getStyle();
			}

			CardGenerator.LOGGER.info("Generated pokemon: {}", pokemon);
			pokemons.add(pokemon);
		}

		return pokemons;
	}

	public Pokemon generatePokemonCard(PokemonElement element, Rarity rarity, Style inheritedStyle, Integer serieIndex,
			String subjectOverride) {
		boolean isPartOfSeries = serieIndex != null;
		int maxAbilityPoints = isPartOfSeries ?
				CardGenerator.getPointsBudget(rarity.ordinal(), serieIndex) :
				CardGenerator.getPointsBudget(rarity.ordinal(), 1);

		int hpPoints = ThreadLocalRandom.current().nextInt(0, Math.floorDiv(maxAbilityPoints, 2));
		int abilityPoints = maxAbilityPoints - hpPoints;
		List<Integer> abilityCosts = CardGenerator.getAbilityPointsCosts(abilityPoints, rarity.ordinal());
		List<Ability> abilities = generateAbilities(element, abilityCosts);

		for (Ability ability : abilities) {
			ability.setName(abilityNameLoader.getAbilityName(ability));
		}

		int bonusHpPoints = maxAbilityPoints + hpPoints * ABILITY_TO_HP_PTS;
		int hp = 10 * bonusHpPoints;

		Style style = styleGenerator.createStyle(inheritedStyle, element, rarity, serieIndex, subjectOverride);

		Pokemon pokemon = Pokemon.builder().name("Untitled Card").abilities(abilities).hp(hp).element(element)
				.rarity(rarity).style(style).build();
		pokemon.setImagePrompt(getImagePrompt(pokemon));
		pokemon.setVisualDescription(getVisualDescription(pokemon));

		if (serviceForGeneratingAIResults.isAIEnabled()) {
			pokemon.setName(serviceForGeneratingAIResults.generatePokemonName(pokemon));
			pokemon.setDescription(
					serviceForGeneratingAIResults.generatePokemonDescription(pokemon, getVisualDescription(pokemon)));
		}

		pokemon.setImagePrompt(getImagePrompt(pokemon));
		pokemon.setVisualDescription(getVisualDescription(pokemon));

		return pokemon;
	}

	private String getVisualDescription(Pokemon pokemon) {
		StringBuilder sb = new StringBuilder();
		String subjectMainLine = getFullSubjectDescription(pokemon);
		sb.append(subjectMainLine);
		sb.append(". ");
		sb.append(String.format("It can be found in %s-like environments.", pokemon.getStyle().getEnvironment()));

		return sb.toString();
	}

	private String getFullSubjectDescription(Pokemon pokemon) {
		return getSubjectDescription(pokemon).concat(getDetailDescription(pokemon));
	}

	public String getImagePrompt(Pokemon pokemon) {
		List<String> segments = new ArrayList<>();
		String subjectMainLine = getSubjectDescription(pokemon);

		subjectMainLine = subjectMainLine.concat(getDetailDescription(pokemon));
		segments.add(subjectMainLine);
		segments.add(", ");
		segments.add(String.format("in a %s environment", pokemon.getStyle().getEnvironment()));
		segments.add(", ");
		segments.add(pokemon.getStyle().getAmbience());
		segments.add(", ");
		segments.add(pokemon.getStyle().getStyleSuffix());

		StringBuilder sb = new StringBuilder();
		for (String str : segments) {
			sb.append(str);
		}

		return sb.toString().replace("  ", " ").replace(" ,", ",");
	}

	private String getDetailDescription(Pokemon pokemon) {
		if (pokemon.getStyle().getDetail() != null && !pokemon.getStyle().getDetail().isBlank()
				&& pokemon.getRarity().ordinal() > 0) {
			return ", ".concat(pokemon.getStyle().getDetail());
		} else {
			return "";
		}
	}

	private String getSubjectDescription(Pokemon pokemon) {
		StringBuilder sb = new StringBuilder();
		sb.append("a");
		sb.append(" ");
		for (String adj : pokemon.getStyle().getSubjectAdjectives()) {
			sb.append(adj);
			sb.append(" ");
		}
		sb.append(pokemon.getStyle().getSubject());
		sb.append(" ");
		sb.append(SUBJECT_TYPE);

		return sb.toString().replace(" ,", ",");
	}

	private List<Ability> generateAbilities(PokemonElement element, List<Integer> abilityCosts) {
		List<Ability> list = new ArrayList<>();

		for (int cost : abilityCosts) {
			boolean isPrimary = cost == 0;
			PokemonElement abilityElement = null;

			if (!isPrimary && Math.random() < NEUTRAL_ELEMENT_CHANCE) {
				abilityElement = PokemonElement.NEUTRAL;
			} else {
				abilityElement = element;
			}

			Ability ability = generateAbility(abilityElement, cost);
			list.add(ability);
		}

		return list;
	}

	private Ability generateAbility(PokemonElement element, int cost) {
		boolean isMixed = !element.isNeutral() && cost > 1 && (Math.random() < MIXED_ELEMENT_CHANCE);

		return Ability.builder().name("New Ability").element(element).cost(cost).isMixedElement(isMixed).build();
	}

	/**
	 * @param abilityPoints
	 * @param rarityIndex
	 * @return list containing amount of points each ability cost (also determines how many abilities would be)
	 */
	private static List<Integer> getAbilityPointsCosts(int abilityPoints, int rarityIndex) {
		if (abilityPoints >= 6) {
			return Stream.of(4, abilityPoints - 4).toList();
		} else if (abilityPoints >= 4) {
			int firstAbilityCost = ThreadLocalRandom.current().nextInt(3, 4);
			if (firstAbilityCost == 4) {
				return Collections.singletonList(4);
			} else {
				return Stream.of(firstAbilityCost, abilityPoints - firstAbilityCost).toList();
			}
		} else if (abilityPoints == 3) {
			if (rarityIndex < 1) {
				return Stream.of(2, 1).toList();
			} else {
				double random = Math.random();
				return random < 0.5 ? Collections.singletonList(3) : Stream.of(2, 1).toList();
			}
		} else {
			return Collections.singletonList(abilityPoints);
		}
	}

	private static int getPointsBudget(int rarityBonus, Integer serieIndex) {
		int seriesBonus = serieIndex - 1;
		return BASE_POINTS + rarityBonus + seriesBonus;
	}
}
