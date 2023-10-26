package com.petproject.pokemoncardgenerator.model.details.enums;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

public enum PokemonElement {
	NEUTRAL("Neutral", true), //
	FIRE("Fire", false), //
	WATER("Water", false), //
	GRASS("Grass", false), //
	ELECTRIC("Electric", false), //
	PSYCHIC("Psychic", false), //
	FIGHTING("Fighting", false), //

	UNKNOWN("Unknown", false);

	private static final Map<String, PokemonElement> LOOKUP_MAP;

	static {
		LOOKUP_MAP = Stream.of(PokemonElement.values()).collect(toMap(PokemonElement::getElementName, identity()));
	}

	public static PokemonElement get(String name) {
		return PokemonElement.LOOKUP_MAP.getOrDefault(name, PokemonElement.UNKNOWN);
	}

	private final String elementName;
	private final boolean isNeutral;

	PokemonElement(String element, boolean isNeutral) {
		this.elementName = element;
		this.isNeutral = isNeutral;
	}

	public String getElementName() {
		return elementName;
	}

	public boolean isNeutral() {
		return isNeutral;
	}

	public static PokemonElement getResist(PokemonElement element) {
		if (element == PokemonElement.FIRE) {
			return PokemonElement.GRASS;
		} else if (element == PokemonElement.WATER) {
			return PokemonElement.FIRE;
		} else if (element == PokemonElement.GRASS) {
			return PokemonElement.ELECTRIC;
		} else if (element == PokemonElement.PSYCHIC) {
			return PokemonElement.FIGHTING;
		} else {
			return null;
		}
	}

	public static PokemonElement getWeakness(PokemonElement element) {
		if (element == PokemonElement.FIRE) {
			return PokemonElement.WATER;
		} else if (element == PokemonElement.WATER) {
			return PokemonElement.ELECTRIC;
		} else if (element == PokemonElement.GRASS) {
			return PokemonElement.FIRE;
		} else if (element == PokemonElement.ELECTRIC) {
			return PokemonElement.FIGHTING;
		} else if (element == PokemonElement.NEUTRAL) {
			return PokemonElement.FIGHTING;
		} else if (element == PokemonElement.FIGHTING) {
			return PokemonElement.PSYCHIC;
		} else {
			return null;
		}
	}

}
