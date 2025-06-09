package com.petproject.pokemoncardgenerator.services.renderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.petproject.pokemoncardgenerator.Constants;
import com.petproject.pokemoncardgenerator.model.Pokemon;
import com.petproject.pokemoncardgenerator.model.details.Ability;
import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;

@Component
public class CardRenderer {

	private static final Logger LOGGER = LoggerFactory.getLogger(CardRenderer.class);

	public BufferedImage renderCard(Pokemon pokemon, BufferedImage pokemonImage) {
		LOGGER.info("Starting to generate image for given pokemon: {}", pokemon);
		BufferedImage cardTemplateImage = getCardTemplateImage(pokemon);

		if (cardTemplateImage == null) {
			LOGGER.info("Card template is absent, cannot proceed further");
			return null;
		}

		BufferedImage canvas = new BufferedImage(cardTemplateImage.getWidth(), cardTemplateImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = canvas.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		Font boldFont;
		Font regularFont;
		Font symbolFont;
		try (InputStream boldStream = getClass().getResourceAsStream("/generator/font/Cabin-Bold.ttf");
		     InputStream regularStream = getClass().getResourceAsStream("/generator/font/Cabin_Condensed-Regular.ttf");
		     InputStream symbolStream = getClass().getResourceAsStream("/generator/font/NotoSansSymbols2-Regular.ttf")) {

			boldFont = Font.createFont(Font.TRUETYPE_FONT, boldStream);
			regularFont = Font.createFont(Font.TRUETYPE_FONT, regularStream);
			symbolFont = Font.createFont(Font.TRUETYPE_FONT, symbolStream);

		} catch (FontFormatException | IOException e) {
			LOGGER.error("Error during creating font, using default fonts", e);
			boldFont = new Font(Constants.DEFAULT_FONT, Font.BOLD, 14);
			regularFont = new Font(Constants.DEFAULT_FONT, Font.BOLD, 14);
			symbolFont = new Font(Constants.DEFAULT_FONT, Font.BOLD, 14);
		}

		drawPokemonImageInsideTemplateCard(pokemonImage, cardTemplateImage, g);
		drawPokemonName(pokemon, g, boldFont);
		drawHP(pokemon, canvas, g, regularFont);
		drawAbilities(pokemon, canvas, g, boldFont, regularFont);
		drawWeaknessAndResist(pokemon, canvas, g);
		drawRarity(pokemon, canvas, g, regularFont, symbolFont);

		g.dispose();
		LOGGER.info("Finished image generation.");
		return canvas;
	}

	private BufferedImage getCardTemplateImage(Pokemon pokemon) {
		String cardTemplateName = pokemon.getElement().getElementName().toLowerCase() + "_card.png";
		try (InputStream is = getClass().getResourceAsStream("/generator/cards/templates/" + cardTemplateName)) {
			if (is == null) {
				LOGGER.error("Template not found in resources: " + cardTemplateName);
				return null;
			}
			return ImageIO.read(is);
		} catch (IOException e) {
			LOGGER.error("Error during reading pokemon template image", e);
			return null;
		}
	}

	private void drawWeaknessAndResist(Pokemon pokemon, BufferedImage canvas, Graphics2D g) {
		PokemonElement resistElement = PokemonElement.getResist(pokemon.getElement());
		PokemonElement weaknessElement = PokemonElement.getWeakness(pokemon.getElement());

		if (weaknessElement != null) {
			drawElement(g, weaknessElement, Constants.STATUS_X_GAP);
		}

		if (resistElement != null) {
			int resistX = Math.floorDiv(canvas.getWidth(), 2);
			drawElement(g, resistElement, resistX);
		}

		int retreatCostGap = canvas.getWidth() - Constants.STATUS_X_GAP;
		drawElement(g, PokemonElement.NEUTRAL, retreatCostGap);
	}

	private void drawElement(Graphics2D g, PokemonElement element, int positionX) {
		BufferedImage elementImage = null;
		String path = "/generator/elements/" + element.getElementName().toLowerCase() + "_element.png";
		try (InputStream is = getClass().getResourceAsStream(path)) {
			if (is != null) {
				elementImage = ImageIO.read(is);
			}
		} catch (IOException e) {
			LOGGER.error("Error loading element image: " + path, e);
		}

		if (elementImage != null) {
			elementImage = resize(elementImage, Constants.STATUS_SIZE, Constants.STATUS_SIZE);
			g.drawImage(elementImage, positionX - Math.floorDiv(Constants.STATUS_SIZE, 2),
					Constants.STATUS_Y_POSITION - Math.floorDiv(Constants.STATUS_SIZE, 2), null);
		} else {
			LOGGER.warn("Element image not found or failed to load: " + path);
		}
	}

	private BufferedImage drawElementCost(List<String> elements) {
		BufferedImage canvas = new BufferedImage(Constants.ABILITY_COST_WIDTH, Constants.ABILITY_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = canvas.getGraphics();

		int cost = elements.size();
		int[][] positions = new int[cost][2];

		int centerX = Math.floorDiv(Constants.ABILITY_COST_WIDTH, 2);
		int centerY = Math.floorDiv(Constants.ABILITY_HEIGHT, 2);
		int xOffset = centerX - (Math.floorDiv(Constants.ELEMENT_SIZE, 2) + Math.floorDiv(Constants.ABILITY_GAP, 2));
		int xOffsetDown = xOffset + Constants.ELEMENT_SIZE + Constants.ABILITY_GAP;
		int yOffset = centerY - Math.floorDiv((Constants.ELEMENT_SIZE + Constants.ABILITY_GAP), 2);
		int yOffsetDown = yOffset + Constants.ELEMENT_SIZE + Constants.ABILITY_GAP;

		if (cost == 1) {
			positions[0][0] = centerX;
			positions[0][1] = centerY;
		} else if (cost == 2) {
			positions[0][0] = xOffset;
			positions[0][1] = centerY;
			positions[1][0] = xOffsetDown;
			positions[1][1] = centerY;
		} else if (cost == 3) {
			positions[0][0] = xOffset;
			positions[0][1] = yOffset;
			positions[1][0] = xOffsetDown;
			positions[1][1] = yOffset;
			positions[2][0] = centerX;
			positions[2][1] = yOffsetDown;
		} else if (cost == 4) {
			positions[0][0] = xOffset;
			positions[0][1] = yOffset;
			positions[1][0] = xOffsetDown;
			positions[1][1] = yOffset;
			positions[2][0] = xOffset;
			positions[2][1] = yOffsetDown;
			positions[3][0] = xOffsetDown;
			positions[3][1] = yOffsetDown;
		}

		for (int i = 0; i < cost; i++) {
			String element = elements.get(i);
			BufferedImage elementImage = null;
			String path = "/generator/elements/" + element.toLowerCase() + "_element.png";
			try (InputStream is = getClass().getResourceAsStream(path)) {
				if (is != null) {
					elementImage = ImageIO.read(is);
				}
			} catch (IOException e) {
				LOGGER.error("Error loading element cost image: " + path, e);
			}

			if (elementImage != null) {
				elementImage = resize(elementImage, Constants.ELEMENT_SIZE, Constants.ELEMENT_SIZE);
				g.drawImage(elementImage, positions[i][0] - Math.floorDiv(Constants.ELEMENT_SIZE, 2),
						positions[i][1] - Math.floorDiv(Constants.ELEMENT_SIZE, 2), null);
			} else {
				LOGGER.warn("Missing cost icon: " + path);
			}
		}

		g.dispose();
		return canvas;
	}

	public BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage buf = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = buf.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return buf;
	}
}