package com.petproject.pokemoncardgenerator.services.ai;

import static com.petproject.pokemoncardgenerator.Constants.SUBJECT_TYPE;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.petproject.pokemoncardgenerator.Models;
import com.petproject.pokemoncardgenerator.model.Pokemon;
import com.petproject.pokemoncardgenerator.model.details.Ability;
import com.petproject.pokemoncardgenerator.services.rest.RestCommunicatorWrapper;

@Service
public class ServiceForGeneratingGenerativeAIResults {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceForGeneratingGenerativeAIResults.class);
	private static final String ERROR_DURING_API_REQUEST = "Error occurred during API request for generating pokemon card";
	private static final String DEFAULT_NAME = "Unnamed";

	private final RestCommunicatorWrapper restCommunicator;

	public ServiceForGeneratingGenerativeAIResults(RestCommunicatorWrapper restCommunicator) {
		this.restCommunicator = restCommunicator;
	}

	public String generateAbilityName(Ability ability) {
		String mixed = ability.isMixedElement() ? "mixed" : "pure";
		String extraPower = "standard";
		String prompt = String.format(
				"Generate only one a unique, original, creative name for ability with such parameters: %s %s %s %s",
				ability.getElement().getElementName(), ability.getCost(), mixed, extraPower);
		prompt = prompt.concat(". Without using the words from parameters, in one word without punctuation signs:");

		ServiceForGeneratingGenerativeAIResults.LOGGER.info(
				"Making a request to generate ability name with following prompt: {}", prompt);

		String abilityName = restCommunicator.executeTextGenerationRestCall(Models.TEXT_GENERATION_MODEL, prompt);
		if (abilityName != null) {
			if (abilityName.length() > 30) {
				ServiceForGeneratingGenerativeAIResults.LOGGER.info(
						"Ability name is too long. Making request again...");

				abilityName = restCommunicator.executeTextGenerationRestCall(Models.TEXT_GENERATION_MODEL, prompt);
				if (abilityName == null || abilityName.length() > 30) {
					ServiceForGeneratingGenerativeAIResults.LOGGER.error(
							ERROR_DURING_API_REQUEST);
					return DEFAULT_NAME;
				}
			}

			ServiceForGeneratingGenerativeAIResults.LOGGER.info("Successfully generated ability name: {}", abilityName);
		} else {
			ServiceForGeneratingGenerativeAIResults.LOGGER.error(
					"Error occurred during API request for generating ability name");
			abilityName = "Unnamed Ability";
		}

		return abilityName;
	}

	public BufferedImage generatePokemonImage(String imagePrompt) {
		BufferedImage pokemonImage = restCommunicator.executeImageGenerationRestCall(Models.IMAGE_GENERATION_MODEL,
				imagePrompt);

		if (pokemonImage != null) {
			ServiceForGeneratingGenerativeAIResults.LOGGER.info("Successfully generated pokemon image");
		} else {
			ServiceForGeneratingGenerativeAIResults.LOGGER.error(
					"Error occurred during API request for generating card image");
		}

		return pokemonImage;
	}

	/**
	 * @param pokemon
	 * @param visualDescription
	 * @return description of pokemon, the return being cut on the last dot, so it would not be that long.
	 * 70 tokens are being used during the setting following conditions.
	 * Conditions and prompts may vary from model to the model, model being used there is mistralai/Mistral-7B-Instruct-v0.1.
	 */
	public String generatePokemonDescription(Pokemon pokemon, String visualDescription) {

		StringBuilder sb = new StringBuilder();

		sb.append(String.format(
				"Generate in 2 sentences a very brief, original, creative Pokedex description for %s, %s ",
				pokemon.getName(), visualDescription));
		sb.append("It has the following abilities: ");

		for (Ability ability : pokemon.getAbilities()) {
			sb.append(ability.getName());
			sb.append(", ");
		}
		sb.append(". ");

		sb.append("Be creative about its day-to-day life. ");
		sb.append(String.format("(do not use the word %s or %s-type or the abilitiy names):",
				pokemon.getStyle().getSubject(), pokemon.getElement().getElementName().toLowerCase()));

		ServiceForGeneratingGenerativeAIResults.LOGGER.info(
				"Making a request to generate pokemon description with following prompt: {}", sb);

		String desc = restCommunicator.executeTextGenerationRestCall(Models.TEXT_GENERATION_MODEL, sb.toString());
		if (desc != null) {
			ServiceForGeneratingGenerativeAIResults.LOGGER.info("Successfully generated description: {}", desc);
		} else {
			ServiceForGeneratingGenerativeAIResults.LOGGER.error(
					"Error occurred during API request for generating pokemon description");
			desc = "";
		}

		// making clear that description looks complete, as there might be not enough given tokens to generate the full sentence
		return desc.contains(".") ? desc.substring(0, desc.indexOf(".") + 1) : desc;
	}

	public String generatePokemonName(Pokemon pokemon) {
		String additionalModifier = pokemon.getRarity().ordinal() != 0 ? "single-word" : "short, single-word";

		String prompt = String.format("Generate only one a unique, original, creative, %s %s name for %s",
				additionalModifier, SUBJECT_TYPE, pokemon.getVisualDescription());
		prompt = prompt.concat(
				String.format(" (without using the word %s or %s) in one word without punctuation signs:", SUBJECT_TYPE,
						pokemon.getElement().getElementName()));

		return getProperName(prompt);
	}

	/***
	 *
	 * @param prompt
	 * @return complete name of the pokemon
	 * Currently following conditions are coupled to the model it was used with, mistralai/Mistral-7B-Instruct-v0.1
	 * For other models the prompts could look differently and the results as well, thus, it is better to play along firstly with the model.
	 */
	private String getProperName(String prompt) {
		ServiceForGeneratingGenerativeAIResults.LOGGER.info(
				"Making a request to generate pokemon name with following prompt: {}", prompt);

		String name = restCommunicator.executeTextGenerationRestCall(Models.TEXT_GENERATION_MODEL, prompt);
		if (name != null) {
			ServiceForGeneratingGenerativeAIResults.LOGGER.info("Successfully returned name: {}", name);
		} else {
			ServiceForGeneratingGenerativeAIResults.LOGGER.error(
					ERROR_DURING_API_REQUEST);
			return DEFAULT_NAME;
		}

		if (name.length() > 18) {
			ServiceForGeneratingGenerativeAIResults.LOGGER.info("Name is too long. Making request again...");

			name = restCommunicator.executeTextGenerationRestCall(Models.TEXT_GENERATION_MODEL, prompt);
			if (name == null) {
				ServiceForGeneratingGenerativeAIResults.LOGGER.error(
						ERROR_DURING_API_REQUEST);
				return DEFAULT_NAME;
			}
		}

		// eg "1. Fungus\\n2. Mushroom\\n3. Jungle\\n4. Armor\\n5. Green\\n6. Brown\\n7. Angry\\n8. Young\\n9. Epic\\n10. Grass\\n11. White\\n12. Mushroom\\n13. Jungle\\n\n"
		if (name.contains("\\n")) {
			name = name.substring(name.indexOf(".") + 1, name.indexOf("\\n")).trim();
		}

		// eg.\n\n1. Foliage
		if (name.contains("\n")) {
			name = name.substring(name.indexOf(".") + 1).trim();
		}

		// there may still be punctuational signs
		name = name.replaceAll("\\p{Punct}", "");
		name = StringUtils.capitalize(name.toLowerCase());

		ServiceForGeneratingGenerativeAIResults.LOGGER.info("Final name: {}", name);
		return name;
	}

	public boolean isAIEnabled() {
		return restCommunicator.isApiEnabled();
	}
}
