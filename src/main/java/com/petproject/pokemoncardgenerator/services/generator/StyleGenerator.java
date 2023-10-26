package com.petproject.pokemoncardgenerator.services.generator;

import static com.petproject.pokemoncardgenerator.Constants.MAIN_STYLE_SUFFIX;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.petproject.pokemoncardgenerator.model.Style;
import com.petproject.pokemoncardgenerator.model.contentpool.PokemonContentPool;
import com.petproject.pokemoncardgenerator.model.contentpool.StyleDetailsContentPool;
import com.petproject.pokemoncardgenerator.model.details.CreatureType;
import com.petproject.pokemoncardgenerator.model.details.PokemonDetail;
import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;
import com.petproject.pokemoncardgenerator.model.details.enums.Rarity;

@Service
public class StyleGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(StyleGenerator.class);

	public Style createStyle(Style inheritedStyle, PokemonElement element, Rarity rarity, Integer serieIndex,
			String subjectOverride) {
		Style style = new Style();

		if (inheritedStyle != null) {
			StyleGenerator.LOGGER.info("Inherited style is present. Generating style based on it...");

			style.setSubject(inheritedStyle.getSubject());
			style.setSubjectAdjectives(inheritedStyle.getSubjectAdjectives());
			style.setDetail(inheritedStyle.getDetail());
			style.setDetailAdjective(inheritedStyle.getDetailAdjective());
			style.setEnvironment(inheritedStyle.getEnvironment());

		} else {
			StyleGenerator.LOGGER.info("Inherited style is not present. Generating new style...");

			CreatureType subject = getCreatureType(element, subjectOverride);
			style.setSubject(subject.getName());

			PokemonDetail detail;
			if (subject.getDetails().size() > 1) {
				detail = subject.getDetails().get(ThreadLocalRandom.current()
						.nextInt(subject.getDetails().size() == 1 ? 1 : subject.getDetails().size() - 1));

				String detailAdjective = StyleDetailsContentPool.getRandomDetailAdjective(element);
				style.setDetail(detail.generateDetailWithAdjective(detailAdjective));
				style.setDetailAdjective(detailAdjective);
			}

			List<String> potentialEnvironments = StyleDetailsContentPool.getEnvironments(element);
			style.setEnvironment(
					potentialEnvironments.get(ThreadLocalRandom.current().nextInt(potentialEnvironments.size() - 1)));
		}

		String rarityPrefix = StyleDetailsContentPool.getRandomRarityAdjective(rarity);
		String seriesPrefix = StyleDetailsContentPool.getRandomSeriesAdjective(serieIndex);
		String elementPrefix = String.format("%s-type", element.name().toLowerCase());

		String sizePrefix = "";
		if (serieIndex != null) {
			sizePrefix = seriesPrefix;
			if (rarity.ordinal() >= 2) {
				sizePrefix += String.format(" %s", rarityPrefix);
			}
		} else {
			sizePrefix = rarityPrefix;
		}

		style.setSubjectAdjectives(List.of(sizePrefix, elementPrefix));

		if (rarity.ordinal() >= 2 && serieIndex != null && serieIndex == 2) {
			style.setAmbience(StyleDetailsContentPool.AMBIENCE_BY_ELEMENT.get(element)
					.get(StyleDetailsContentPool.AMBIENCE_BY_ELEMENT.get(element).size() - 1));
		} else {
			style.setAmbience(StyleDetailsContentPool.getRandomAmbience(element));
		}

		style.setStyleSuffix(
				String.format("%s %s", StyleDetailsContentPool.getStyleSuffix(serieIndex), MAIN_STYLE_SUFFIX));

		StyleGenerator.LOGGER.info("Output style of the card: {}", style);
		return style;
	}

	private CreatureType getCreatureType(PokemonElement element, String subjectOverride) {
		if (subjectOverride != null && !subjectOverride.isBlank()) {
			return PokemonContentPool.getClosestMatch(subjectOverride);
		} else {
			Set<CreatureType> potentialSubjects = PokemonContentPool.getCreatureTypes(element);
			return (CreatureType) potentialSubjects.toArray()[ThreadLocalRandom.current()
					.nextInt(potentialSubjects.size())];
		}
	}
}
