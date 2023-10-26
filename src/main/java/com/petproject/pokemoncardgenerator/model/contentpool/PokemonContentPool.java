package com.petproject.pokemoncardgenerator.model.contentpool;

import static com.petproject.pokemoncardgenerator.model.contentpool.DetailsContentPool.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.petproject.pokemoncardgenerator.model.details.CreatureType;
import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;

/**
 * content pool of different animals, which could be used for pokemon generation
 */
public class PokemonContentPool {

	private static final CreatureType REPTILE = CreatureType.builder().name("reptile")
			.details(getMergedDetails(DetailsContentPool.ALL_WEARABLES, WITH_TAIL, WITH_SKIN)).build();
	private static final CreatureType CLAM = CreatureType.builder().name("clam")
			.details(List.of(WITH_SHELL, WITH_CRYSTAL_CORE)).build();
	private static final CreatureType PENGUIN = CreatureType.builder().name("penguin")
			.details(getMergedDetails(ALL_WEARABLES, HOLDABLE_WEAPONS, WITH_TAIL, WITH_FUR)).build();
	private static final CreatureType ORCA = CreatureType.builder().name("orca")
			.details(List.of(WITH_TAIL, WITH_SKIN, WEARING_ARMOR)).build();
	private static final CreatureType SHARK = CreatureType.builder().name("shark")
			.details(List.of(WITH_TAIL, WITH_FINS, WEARING_ARMOR)).build();
	private static final CreatureType SQUID = CreatureType.builder().name("squid")
			.details(List.of(WITH_CRYSTAL_CORE, WITH_TENTACLES)).build();
	private static final CreatureType CRUSTACEAN = CreatureType.builder().name("crustacean")
			.details(List.of(WITH_CLAWS, WITH_SHELL, WEARING_ARMOR, WITH_CRYSTAL_CORE, WITH_CARAPACE)).build();
	private static final CreatureType TORTOISE = CreatureType.builder().name("tortoise")
			.details(getMergedDetails(ALL_WEARABLES, WITH_TAIL, WITH_SHELL, WITH_CARAPACE)).build();
	private static final CreatureType SEA_HORSE = CreatureType.builder().name("sea-horse")
			.details(getMergedDetails(ALL_WEARABLES, WITH_TAIL, WITH_SHELL)).build();
	private static final CreatureType SEA_SNAKE = CreatureType.builder().name("sea-snake")
			.details(getMergedDetails(HEAD_WEARABLES, WITH_TAIL, WITH_SCALES, WEARING_ARMOR)).build();
	private static final CreatureType FISH = CreatureType.builder().name("fish")
			.details(List.of(WITH_TAIL, WITH_SCALES, WEARING_ARMOR)).build();
	private static final CreatureType OCTOPUS = CreatureType.builder().name("octopus")
			.details(List.of(WITH_TENTACLES, WEARING_ARMOR)).build();
	private static final CreatureType DRAGON = CreatureType.builder().name("dragon")
			.details(getMergedDetails(LIZARD_DETAILS, WITH_CRYSTAL_CORE)).build();
	private static final CreatureType SERPENT = CreatureType.builder().name("serpent").details(NO_HAND_REPTILE_DETAILS)
			.build();
	private static final CreatureType CROCODILE = CreatureType.builder().name("crocodile")
			.details(NO_HAND_REPTILE_DETAILS).build();

	private static final CreatureType SWAN = CreatureType.builder().name("swan").details(BIRD_DETAILS).build();

	private static final CreatureType BIRD = CreatureType.builder().name("bird").details(BIRD_DETAILS).build();
	private static final CreatureType PARROT = CreatureType.builder().name("parrot").details(BIRD_DETAILS).build();
	private static final CreatureType OWL = CreatureType.builder().name("owl").details(BIRD_DETAILS).build();
	private static final CreatureType EAGLE = CreatureType.builder().name("eagle").details(BIRD_DETAILS).build();
	private static final CreatureType HAWK = CreatureType.builder().name("hawk").details(BIRD_DETAILS).build();
	private static final CreatureType FALCON = CreatureType.builder().name("falcon").details(BIRD_DETAILS).build();
	private static final CreatureType CROW = CreatureType.builder().name("crow").details(BIRD_DETAILS).build();
	private static final CreatureType OSTRICH = CreatureType.builder().name("ostrich").details(BIRD_DETAILS).build();

	private static final CreatureType LIZARD = CreatureType.builder().name("lizard").details(LIZARD_DETAILS).build();
	private static final CreatureType CHAMELEON = CreatureType.builder().name("chameleon").details(LIZARD_DETAILS)
			.build();
	private static final CreatureType FRILLED_LIZARD = CreatureType.builder().name("frilled-lizard")
			.details(NO_HAND_REPTILE_DETAILS).build();
	private static final CreatureType GECKO = CreatureType.builder().name("gecko").details(LIZARD_DETAILS).build();

	private static final CreatureType BUTTERFLY = CreatureType.builder().name("butterfly").details(INSECT_DETAILS)
			.build();
	private static final CreatureType MANTIS = CreatureType.builder().name("mantis").details(INSECT_DETAILS).build();
	private static final CreatureType BEETLE = CreatureType.builder().name("beetle").details(INSECT_DETAILS).build();
	private static final CreatureType LADYBUG = CreatureType.builder().name("ladybug").details(INSECT_DETAILS).build();
	private static final CreatureType DRAGONFLY = CreatureType.builder().name("dragonfly").details(INSECT_DETAILS)
			.build();
	private static final CreatureType SPIDER = CreatureType.builder().name("spider").details(INSECT_DETAILS).build();
	private static final CreatureType SCORPION = CreatureType.builder().name("scorpion").details(INSECT_DETAILS)
			.build();

	private static final CreatureType WOLF = CreatureType.builder().name("wolf")
			.details(getMergedDetails(ALL_WEARABLES, WITH_TAIL, WITH_CLAWS, WITH_FUR)).build();
	private static final CreatureType BEAR = CreatureType.builder().name("bear")
			.details(getMergedDetails(ALL_WEARABLES, WITH_CLAWS, WITH_FUR)).build();
	private static final CreatureType MONKEY = CreatureType.builder().name("monkey")
			.details(getMergedDetails(ALL_WEARABLES, HOLDABLE_WEAPONS, WITH_TAIL, WITH_FUR)).build();
	private static final CreatureType GORILLA = CreatureType.builder().name("gorilla")
			.details(getMergedDetails(ALL_WEARABLES, HOLDABLE_WEAPONS, WITH_TAIL, WITH_FUR)).build();

	private static final CreatureType BULL = CreatureType.builder().name("bull")
			.details(getMergedDetails(ALL_WEARABLES, WITH_HORNS, WITH_HOOVES, WITH_SKIN)).build();
	private static final CreatureType BISON = CreatureType.builder().name("bison")
			.details(getMergedDetails(ALL_WEARABLES, WITH_HORNS, WITH_HOOVES, WITH_SKIN)).build();
	private static final CreatureType ELEPHANT = CreatureType.builder().name("elephant")
			.details(getMergedDetails(ALL_WEARABLES, WITH_HOOVES, WITH_TUSKS, WITH_SKIN)).build();
	private static final CreatureType BOAR = CreatureType.builder().name("boar")
			.details(getMergedDetails(ALL_WEARABLES, WITH_HOOVES, WITH_TUSKS, WITH_SKIN)).build();
	private static final CreatureType TIGER = CreatureType.builder().name("tiger")
			.details(getMergedDetails(ALL_WEARABLES, WITH_CLAWS, WITH_FUR)).build();
	private static final CreatureType LYNX = CreatureType.builder().name("lynx")
			.details(getMergedDetails(ALL_WEARABLES, WITH_CLAWS, WITH_FUR)).build();
	private static final CreatureType LION = CreatureType.builder().name("lion")
			.details(getMergedDetails(ALL_WEARABLES, WITH_CLAWS, WITH_FUR)).build();
	private static final CreatureType RABBIT = CreatureType.builder().name("rabbit")
			.details(getMergedDetails(ALL_WEARABLES, HOLDABLE_WEAPONS, WITH_FUR)).build();
	private static final CreatureType FOX = CreatureType.builder().name("fox")
			.details(getMergedDetails(ALL_WEARABLES, WITH_TAIL, WITH_FUR)).build();
	private static final CreatureType DEER = CreatureType.builder().name("deer")
			.details(getMergedDetails(ALL_WEARABLES, WITH_HOOVES, WITH_ANTLERS)).build();
	private static final CreatureType IBEX = CreatureType.builder().name("ibex")
			.details(getMergedDetails(ALL_WEARABLES, WITH_HOOVES, WITH_ANTLERS)).build();
	private static final CreatureType GOAT = CreatureType.builder().name("goat")
			.details(getMergedDetails(ALL_WEARABLES, WITH_HOOVES, WITH_ANTLERS)).build();
	private static final CreatureType HORSE = CreatureType.builder().name("horse")
			.details(getMergedDetails(ALL_WEARABLES, WITH_HOOVES)).build();
	private static final CreatureType CAT = CreatureType.builder().name("cat")
			.details(getMergedDetails(ALL_WEARABLES, WITH_CLAWS, WITH_FUR)).build();

	public static final List<CreatureType> MARINE_CREATUTES = List.of(PokemonContentPool.REPTILE,
			PokemonContentPool.CLAM, PokemonContentPool.PENGUIN, PokemonContentPool.SHARK, PokemonContentPool.SQUID,
			PokemonContentPool.CRUSTACEAN, PokemonContentPool.TORTOISE, PokemonContentPool.SEA_HORSE,
			PokemonContentPool.FISH, PokemonContentPool.OCTOPUS, PokemonContentPool.SERPENT,
			PokemonContentPool.CROCODILE, PokemonContentPool.SWAN, PokemonContentPool.SEA_SNAKE,
			PokemonContentPool.ORCA);

	public static final List<CreatureType> LAND_MAMMALS = List.of(PokemonContentPool.WOLF, PokemonContentPool.BEAR,
			PokemonContentPool.MONKEY, PokemonContentPool.GORILLA, PokemonContentPool.BULL, PokemonContentPool.BISON,
			PokemonContentPool.ELEPHANT, PokemonContentPool.BOAR, PokemonContentPool.TIGER, PokemonContentPool.LYNX,
			PokemonContentPool.LION, PokemonContentPool.RABBIT, PokemonContentPool.FOX, PokemonContentPool.DEER,
			PokemonContentPool.IBEX, PokemonContentPool.GOAT, PokemonContentPool.HORSE, PokemonContentPool.CAT);

	public static final List<CreatureType> REPTILES = List.of(PokemonContentPool.DRAGON, PokemonContentPool.LIZARD,
			PokemonContentPool.CHAMELEON, PokemonContentPool.FRILLED_LIZARD, PokemonContentPool.SERPENT,
			PokemonContentPool.GECKO);

	public static final List<CreatureType> INSECTS = List.of(PokemonContentPool.MANTIS, PokemonContentPool.BEETLE,
			PokemonContentPool.LADYBUG, PokemonContentPool.DRAGONFLY, PokemonContentPool.SPIDER,
			PokemonContentPool.SCORPION, PokemonContentPool.BUTTERFLY);

	public static final List<CreatureType> BIRDS = List.of(PokemonContentPool.BIRD, PokemonContentPool.PARROT,
			PokemonContentPool.OWL, PokemonContentPool.EAGLE, PokemonContentPool.HAWK, PokemonContentPool.FALCON,
			PokemonContentPool.CROW, PokemonContentPool.OSTRICH, PokemonContentPool.SWAN);

	private static final List<CreatureType> ALL_SUBJECTS_BY_NAME = Stream.of(PokemonContentPool.MARINE_CREATUTES,
			PokemonContentPool.LAND_MAMMALS, PokemonContentPool.REPTILES, PokemonContentPool.INSECTS,
			PokemonContentPool.BIRDS).flatMap(Collection::stream).toList();

	private static final Map<PokemonElement, Set<CreatureType>> CREATURES_BY_ELEMENT = Map.of(PokemonElement.NEUTRAL,
			Stream.of(PokemonContentPool.BIRDS, PokemonContentPool.LAND_MAMMALS).flatMap(Collection::stream)
					.collect(Collectors.toSet()), PokemonElement.FIRE,
			Stream.of(PokemonContentPool.LAND_MAMMALS, PokemonContentPool.REPTILES).flatMap(Collection::stream)
					.collect(Collectors.toSet()), PokemonElement.WATER,
			Stream.of(PokemonContentPool.MARINE_CREATUTES, PokemonContentPool.REPTILES).flatMap(Collection::stream)
					.collect(Collectors.toSet()), PokemonElement.GRASS,
			Stream.of(PokemonContentPool.INSECTS, PokemonContentPool.REPTILES, PokemonContentPool.LAND_MAMMALS)
					.flatMap(Collection::stream).collect(Collectors.toSet()), PokemonElement.ELECTRIC,
			Stream.of(PokemonContentPool.LAND_MAMMALS, PokemonContentPool.REPTILES, PokemonContentPool.BIRDS)
					.flatMap(Collection::stream).collect(Collectors.toSet()), PokemonElement.PSYCHIC,
			Stream.of(PokemonContentPool.INSECTS, PokemonContentPool.LAND_MAMMALS, PokemonContentPool.REPTILES,
					PokemonContentPool.BIRDS).flatMap(Collection::stream).collect(Collectors.toSet()),
			PokemonElement.FIGHTING,
			Stream.of(PokemonContentPool.LAND_MAMMALS, PokemonContentPool.INSECTS, PokemonContentPool.REPTILES)
					.flatMap(Collection::stream).collect(Collectors.toSet()));

	private PokemonContentPool() {
	}

	public static CreatureType getClosestMatch(String customSubject) {
		Optional<CreatureType> optionalCreatureType = PokemonContentPool.ALL_SUBJECTS_BY_NAME.stream()
				.filter(creatureType -> creatureType.getName().equals(customSubject)).findFirst();

		return optionalCreatureType.orElseGet(() -> generateCustomCreatureType(customSubject));
	}

	/**
	 * @param customSubject e.g. wolf with blue fur; mushroom fluffy cow
	 * @return custom user creature
	 */
	private static CreatureType generateCustomCreatureType(String customSubject) {
		if (customSubject != null && !customSubject.isBlank()) {
			CreatureType resultCreature = CreatureType.builder().name(customSubject).build();
			return resultCreature;
		}

		return PokemonContentPool.ALL_SUBJECTS_BY_NAME.stream().findFirst().get();
	}

	public static Set<CreatureType> getCreatureTypes(PokemonElement element) {
		return PokemonContentPool.CREATURES_BY_ELEMENT.get(element);
	}

}
