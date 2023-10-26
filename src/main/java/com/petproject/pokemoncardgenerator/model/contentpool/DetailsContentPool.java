package com.petproject.pokemoncardgenerator.model.contentpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.petproject.pokemoncardgenerator.model.details.PokemonDetail;

/**
 * content pool of predefined details, which are used in pokemon content pool
 */
public class DetailsContentPool {

	public static final List<PokemonDetail> HOLDABLE_WEAPONS = new ArrayList<>();

	public static final PokemonDetail WITH_CLAWS = PokemonDetail.withDetail("claws", "");
	public static final PokemonDetail WITH_TAIL = PokemonDetail.withDetail("tail", "a");
	public static final PokemonDetail WITH_HORNS = PokemonDetail.withDetail("horns", "");
	public static final PokemonDetail WITH_HOOVES = PokemonDetail.withDetail("hooves", "");
	public static final PokemonDetail WITH_TUSKS = PokemonDetail.withDetail("tusks", "");
	public static final PokemonDetail WITH_FUR = PokemonDetail.withDetail("fur", "");
	public static final PokemonDetail WITH_SKIN = PokemonDetail.withDetail("skin", "");
	public static final PokemonDetail WITH_ANTLERS = PokemonDetail.withDetail("antlers", "");
	public static final PokemonDetail WITH_SCALES = PokemonDetail.withDetail("scales", "");
	public static final PokemonDetail WITH_SHELL = PokemonDetail.withDetail("shell", "");
	public static final PokemonDetail WITH_CRYSTAL_CORE = PokemonDetail.withDetail("crystal core", "a");
	public static final PokemonDetail WITH_HALO = PokemonDetail.withDetail("halo", "a");
	public static final PokemonDetail WITH_WINGS = PokemonDetail.withDetail("wings", "");
	public static final PokemonDetail WITH_FINS = PokemonDetail.withDetail("fins", "");
	public static final PokemonDetail WITH_TENTACLES = PokemonDetail.withDetail("tentacles", "");
	public static final PokemonDetail WITH_FEATHERS = PokemonDetail.withDetail("feathers", "");
	public static final PokemonDetail WITH_TALONS = PokemonDetail.withDetail("talons", "");
	public static final PokemonDetail WITH_BEAK = PokemonDetail.withDetail("beak", "a");
	public static final PokemonDetail WITH_CARAPACE = PokemonDetail.withDetail("carapace", "");
	public static final PokemonDetail WITH_TEXTURE = PokemonDetail.withDetail("texture", "");

	// clothes
	public static final PokemonDetail WEARING_ARMOR = PokemonDetail.wearingDetail("armor", "");
	public static final PokemonDetail WEARING_BRACERS = PokemonDetail.wearingDetail("bracers", "");
	public static final PokemonDetail WEARING_GEMS = PokemonDetail.wearingDetail("gemstones", "");

	public static final PokemonDetail WEARING_MASK = PokemonDetail.wearingDetail("mask", "a");
	public static final PokemonDetail WEARING_CROWN = PokemonDetail.wearingDetail("crown", "a");
	public static final PokemonDetail WEARING_CRYSTAL_HEADBAND = PokemonDetail.wearingDetail("crystal headband", "a");

	// weapons
	public static final PokemonDetail HOLD_SWORD = PokemonDetail.withHoldableWeapon("sword", "a");
	public static final PokemonDetail HOLD_BOW = PokemonDetail.withHoldableWeapon("bow", "a");
	public static final PokemonDetail HOLD_STAFF = PokemonDetail.withHoldableWeapon("staff", "a");
	public static final PokemonDetail HOLD_SHIELD = PokemonDetail.withHoldableWeapon("shield", "a");
	public static final PokemonDetail HOLD_AXE = PokemonDetail.withHoldableWeapon("axe", "an");
	public static final PokemonDetail HOLD_DAGGER = PokemonDetail.withHoldableWeapon("dagger", "a");
	public static final PokemonDetail HOLD_SPEAR = PokemonDetail.withHoldableWeapon("spear", "a");
	public static final PokemonDetail HOLD_MACE = PokemonDetail.withHoldableWeapon("mace", "a");
	public static final PokemonDetail HOLD_HAMMER = PokemonDetail.withHoldableWeapon("hammer", "a");
	public static final PokemonDetail HOLD_CLUB = PokemonDetail.withHoldableWeapon("club", "a");
	public static final PokemonDetail HOLD_LANCE = PokemonDetail.withHoldableWeapon("lance", "a");
	public static final PokemonDetail HOLD_WHIP = PokemonDetail.withHoldableWeapon("whip", "a");
	public static final PokemonDetail HOLD_GLAIVE = PokemonDetail.withHoldableWeapon("glaive", "a");
	public static final List<PokemonDetail> HEAD_WEARABLES = List.of(DetailsContentPool.WEARING_MASK,
			DetailsContentPool.WEARING_CROWN, DetailsContentPool.WEARING_CRYSTAL_HEADBAND);
	public static final List<PokemonDetail> BODY_WEARABLES = List.of(DetailsContentPool.WEARING_ARMOR,
			DetailsContentPool.WEARING_BRACERS, DetailsContentPool.WEARING_GEMS);
	public static final List<PokemonDetail> ALL_WEARABLES = DetailsContentPool.getMergedDetails(
			DetailsContentPool.BODY_WEARABLES, DetailsContentPool.HEAD_WEARABLES);
	public static final List<PokemonDetail> LIZARD_DETAILS = DetailsContentPool.getMergedDetails(
			DetailsContentPool.HOLDABLE_WEAPONS, DetailsContentPool.ALL_WEARABLES, DetailsContentPool.WITH_TAIL,
			DetailsContentPool.WITH_SCALES);
	public static final List<PokemonDetail> NO_HAND_REPTILE_DETAILS = DetailsContentPool.getMergedDetails(
			DetailsContentPool.ALL_WEARABLES, DetailsContentPool.WITH_TAIL, DetailsContentPool.WITH_SCALES);
	public static final List<PokemonDetail> BIRD_DETAILS = DetailsContentPool.getMergedDetails(
			DetailsContentPool.ALL_WEARABLES, DetailsContentPool.WITH_TAIL, DetailsContentPool.WITH_FEATHERS,
			DetailsContentPool.WITH_BEAK);
	public static final List<PokemonDetail> INSECT_DETAILS = List.of(DetailsContentPool.WITH_CRYSTAL_CORE,
			DetailsContentPool.WITH_WINGS);

	private DetailsContentPool() {
	}

	public static List<PokemonDetail> getMergedDetails(List<PokemonDetail> mainList, PokemonDetail... otherDetails) {
		List<PokemonDetail> details = new ArrayList<>(mainList);
		Collections.addAll(details, otherDetails);
		return details;
	}

	public static List<PokemonDetail> getMergedDetails(List<PokemonDetail> mainList, List<PokemonDetail> secondList,
			PokemonDetail... otherDetails) {
		List<PokemonDetail> firstPart = new ArrayList<>(mainList);
		List<PokemonDetail> secondPart = new ArrayList<>(secondList);
		List<PokemonDetail> merged = new ArrayList<>(Stream.concat(firstPart.stream(), secondPart.stream()).toList());

		Collections.addAll(merged, otherDetails);
		return merged;
	}

}
