package com.petproject.pokemoncardgenerator.model.contentpool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;
import com.petproject.pokemoncardgenerator.model.details.enums.Rarity;

/**
 * content pool of different pieces of style for card generation
 */
public class StyleDetailsContentPool {

	private StyleDetailsContentPool() {
	}

	public static String getRandomDetailAdjective(PokemonElement element) {
		List<String> joinedAdjectives = Stream.concat(StyleDetailsContentPool.GLOBAL_DETAIL_ADJECTIVES.stream(),
				StyleDetailsContentPool.DETAIL_ADJECTIVES_BY_ELEMENT.get(element).stream()).toList();
		return joinedAdjectives.get(ThreadLocalRandom.current().nextInt(joinedAdjectives.size() - 1));
	}

	public static String getRandomRarityAdjective(Rarity rarity) {
		List<String> adjectives = Rarity.getRarityAdjectives(rarity);
		return adjectives.get(ThreadLocalRandom.current().nextInt(adjectives.size() - 1));
	}

	public static String getRandomSeriesAdjective(Integer seriesIndex) {
		if (seriesIndex == null) {
			return "";
		}

		List<String> adjectives = StyleDetailsContentPool.getSeriesAdjectives(seriesIndex);
		return adjectives.get(ThreadLocalRandom.current().nextInt(adjectives.size() - 1));
	}

	public static String getRandomAmbience(PokemonElement element) {
		List<String> copyOfAmbiences = new ArrayList<>(StyleDetailsContentPool.AMBIENCE_BY_ELEMENT.get(element));
		copyOfAmbiences.remove(copyOfAmbiences.size() - 1);
		//Get a random ambience, but don't return the last one, which is for fully evolved pokemon.
		return copyOfAmbiences.get(ThreadLocalRandom.current().nextInt(copyOfAmbiences.size() - 1));
	}

	public static List<String> getEnvironments(PokemonElement element) {
		return StyleDetailsContentPool.ENVIRONMENTS_BY_ELEMENT.get(element);
	}

	/**
	 * @return evolution list adjectives
	 */
	private static List<String> getSeriesAdjectives(Integer seriesIndex) {
		if (seriesIndex == 0) {
			return List.of("chibi cute", "chibi young");
		}
		if (seriesIndex == 1) {
			return List.of("young", "", "dynamic");
		}
		if (seriesIndex == 2) {
			return List.of("gigantic", "massive");
		}

		return List.of("");
	}

	public static String getStyleSuffix(Integer seriesIndex) {
		if (seriesIndex == null) {
			return "anime sketch";
		}
		if (seriesIndex == 0) {
			return "anime chibi drawing style, pastel background";
		}
		if (seriesIndex == 1) {
			return "anime sketch with watercolor";
		}
		if (seriesIndex == 2) {
			return "polished final by studio ghibli";
		}

		return "";
	}

	public static final List<String> GLOBAL_DETAIL_ADJECTIVES = List.of("white", "dark", "golden", "regal", "ornate",
			"ancient");

	public static final Map<PokemonElement, List<String>> DETAIL_ADJECTIVES_BY_ELEMENT = Map.of( //
			PokemonElement.NEUTRAL, List.of("white", "shiny", "prismatic", "opal", "diamond"), //
			PokemonElement.FIRE, List.of("red and white", "orange and black", "fiery", "ruby"), //
			PokemonElement.WATER,
			List.of("blue and white", "white and black", "teal and navy", "blue crystal", "cyan glittering",
					"sapphire"), //
			PokemonElement.GRASS,
			List.of("green and brown", "white and green", "stone", "wooden", "leafy", "green runic"), //
			PokemonElement.ELECTRIC, List.of("yellow and teal", "yellow and black", "golden", "lightning-charged"), //
			PokemonElement.PSYCHIC, List.of("amethyst", "purple cosmic", "galaxy-pattern", "violet hypnotic"), //
			PokemonElement.FIGHTING, List.of("red and black", "rocky", "stone", "brown and grey"));

	public static final Map<PokemonElement, List<String>> AMBIENCE_BY_ELEMENT = Map.of( //
			PokemonElement.NEUTRAL,
			List.of("pastel colors", "bright lighting", "soft ambient light", "faded prismatic bokeh background",
					"silver galaxy background"), //
			PokemonElement.FIRE,
			List.of("red and purple ambient lighting", "blue and red ambient lighting", "lava texture background",
					"orange galaxy background"), //
			PokemonElement.WATER,
			List.of("teal and blue ambient lighting", "aurora background", "sparkling blue background",
					"gleaming bubble background", "sapphire blue galaxy background"), //
			PokemonElement.GRASS,
			List.of("green and orange ambient lighting", "green and teal ambient lighting", "emerald bokeh lighting",
					"sunlight ray ambience", "emerald galaxy background"), //
			PokemonElement.ELECTRIC,
			List.of("yellow and teal ambient lighting", "lightning background", "orange galaxy background"), //
			PokemonElement.PSYCHIC,
			List.of("pink bokeh lighting", "violet shadows", "dreamy background", "galaxy background"), //
			PokemonElement.FIGHTING,
			List.of("orange ambient lighting", "red and purple ambient lighting", "orange and blue ambient lighting",
					"galaxy background"));

	public static final Map<PokemonElement, List<String>> ENVIRONMENTS_BY_ELEMENT = Map.of( //
			PokemonElement.NEUTRAL, List.of("village", "field", "grassland"), //
			PokemonElement.FIRE, List.of("volcano", "desert"), //
			PokemonElement.WATER, List.of("ocean", "lake", "river"), //
			PokemonElement.GRASS, List.of("forest", "jungle", "woods"), //
			PokemonElement.ELECTRIC, List.of("mountain", "city", "thunderstorm"), //
			PokemonElement.PSYCHIC, List.of("castle", "cave", "crypt"), //
			PokemonElement.FIGHTING, List.of("arena", "ruins", "canyon"));

}
