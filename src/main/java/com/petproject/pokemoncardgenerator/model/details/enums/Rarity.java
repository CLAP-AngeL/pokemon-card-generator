package com.petproject.pokemoncardgenerator.model.details.enums;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public enum Rarity {
	COMMON("Common"), //
	UNCOMMON("Uncommon"), //
	RARE("Rare"), //
	UNKNOWN("Unknown");

	private static final Map<String, Rarity> LOOKUP_MAP;

	static {
		LOOKUP_MAP = Stream.of(Rarity.values()).collect(toMap(Enum::name, identity()));
	}

	public static Rarity get(String name) {
		return Rarity.LOOKUP_MAP.getOrDefault(name, Rarity.UNKNOWN);
	}

	private final String rarityName;

	Rarity(String rarityName) {
		this.rarityName = rarityName;
	}

	public String getRarityName() {
		return rarityName;
	}

	public static List<String> getRarityAdjectives(Rarity rarity) {
		if (rarity.equals(Rarity.COMMON)) {
			return List.of("simple", "basic");
		}
		if (rarity.equals(Rarity.UNCOMMON)) {
			return List.of("strong", "rare", "special");
		}
		if (rarity.equals(Rarity.RARE)) {
			return List.of("legendary", "epic", "mythical");
		} else {
			return new ArrayList<>();
		}
	}

}
