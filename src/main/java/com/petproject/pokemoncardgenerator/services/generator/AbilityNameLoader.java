package com.petproject.pokemoncardgenerator.services.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.petproject.pokemoncardgenerator.model.details.Ability;
import com.petproject.pokemoncardgenerator.services.ai.ServiceForGeneratingGenerativeAIResults;

/**
 * preload list of real pokemon abilities or generate new with AI.
 * resourceFile is a json file containing ability names by ability key
 * if name was not found or there is no such file, proceed with API request for generating ability name. if that fails as well, Unnamed Ability would be returned.
 */
@Service
public class AbilityNameLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbilityNameLoader.class);

	@Value("classpath:generator/abilities/ability_names.json")
	private Resource resourceFile;

	private final ServiceForGeneratingGenerativeAIResults serviceForGeneratingAIResults;

	private Map<String, List<String>> keyToAbilityNamesPool;

	public AbilityNameLoader(ServiceForGeneratingGenerativeAIResults serviceForGeneratingAIResults) {
		this.serviceForGeneratingAIResults = serviceForGeneratingAIResults;
	}

	public String getAbilityName(Ability ability) {

		if (keyToAbilityNamesPool == null) {
			populateContentPool();
		}

		String key = ability.getKey();
		AbilityNameLoader.LOGGER.info("Fetching ability name from the key: {}...", key);

		if (keyToAbilityNamesPool.containsKey(key)) {

			AbilityNameLoader.LOGGER.info("Key is present in predefined content pool. Random name would be pulled");
			List<String> randomNames = keyToAbilityNamesPool.get(key);
			return randomNames.get(ThreadLocalRandom.current().nextInt(randomNames.size() - 1));

		} else {
			if (serviceForGeneratingAIResults.isAIEnabled()) {
				AbilityNameLoader.LOGGER.info(
						"Key is not present in content pool. Request to AI would be done to generate name.");
				return serviceForGeneratingAIResults.generateAbilityName(ability);
			}
		}

		AbilityNameLoader.LOGGER.info("Ability name loading failed. Proceed with unnamed ability");
		return "Unnamed Ability";
	}

	private void populateContentPool() {
		if (keyToAbilityNamesPool == null) {
			try {
				String content = resourceFile.getContentAsString(StandardCharsets.UTF_8);
				keyToAbilityNamesPool = JSON.parseObject(content, new TypeReference<>() {

				});
			} catch (IOException e) {
				AbilityNameLoader.LOGGER.error("Exception during loading predefined ability names", e);
				keyToAbilityNamesPool = new HashMap<>();
			}
		}

	}

}
