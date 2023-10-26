package com.petproject.pokemoncardgenerator.services.renderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

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
			CardRenderer.LOGGER.info("Card template is absent, cannot proceed further");
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
		try {
			boldFont = Font.createFont(Font.TRUETYPE_FONT,
					ResourceUtils.getFile("classpath:generator/font/Cabin-Bold.ttf"));
			regularFont = Font.createFont(Font.TRUETYPE_FONT,
					ResourceUtils.getFile("classpath:generator/font/Cabin_Condensed-Regular.ttf"));
			symbolFont = Font.createFont(Font.TRUETYPE_FONT,
					ResourceUtils.getFile("classpath:generator/font/NotoSansSymbols2-Regular.ttf"));
		} catch (FontFormatException | IOException e) {
			CardRenderer.LOGGER.error("Error during creating font, using default fonts", e);
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

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g.dispose();
		LOGGER.info("Finished image generation.");
		return canvas;
	}

	private void drawRarity(Pokemon pokemon, BufferedImage canvas, Graphics2D g, Font regularFont, Font symbolFont) {
		int rarityPositionX = 58;
		int rarityPositionY = 580;
		g.setFont(regularFont.deriveFont(Font.ITALIC, 12f));
		g.setColor(Color.BLACK);
		drawString(g, pokemon.getDescription(), rarityPositionX, rarityPositionY);

		int raritySymbolPositionX = canvas.getWidth() - 74;
		int raritySymbolPositionY = 634;

		String[] raritySymbols = { "\u2B24", "\u25C6", "\u2605" };
		float[] raritySymbolSizes = { 12f, 12f, 14f };

		g.setFont(symbolFont.deriveFont(raritySymbolSizes[pokemon.getRarity().ordinal()]));

		g.drawString(raritySymbols[pokemon.getRarity().ordinal()], raritySymbolPositionX, raritySymbolPositionY);
	}

	private void drawAbilities(Pokemon pokemon, BufferedImage canvas, Graphics2D g, Font boldFont, Font regularFont) {
		int abilityXPosition = Math.floorDiv((canvas.getWidth() - Constants.ABILITY_WIDTH), 2);
		int abilityPositionCenterY = 450;
		int abilityOriginY = 0;

		if (pokemon.getAbilities().size() == 1) {
			abilityOriginY = abilityPositionCenterY - (Math.floorDiv(Constants.ABILITY_HEIGHT, 2));
		} else if (pokemon.getAbilities().size() == 2) {
			abilityOriginY =
					abilityPositionCenterY - (Constants.ABILITY_HEIGHT + Math.floorDiv(Constants.ABILITY_COST_GAP, 2));
		}

		//Draw the abilities in reverse order so that the first ability is at the bottom.
		List<Ability> reversedAbilities = IntStream.range(0, pokemon.getAbilities().size()) //
				.map(i -> pokemon.getAbilities().size() - 1 - i).mapToObj(pokemon.getAbilities()::get).toList();

		int i = 0;
		for (Ability ability : reversedAbilities) {

			BufferedImage abilityImage = drawAbility(ability, boldFont, regularFont);
			int abilityY = abilityOriginY + (i * (Constants.ABILITY_HEIGHT + Constants.ABILITY_COST_GAP));
			g.drawImage(abilityImage, abilityXPosition, abilityY, null);

			i++;
			abilityPositionCenterY += Constants.ABILITY_HEIGHT;
		}

		// line between abilities
		if (pokemon.getAbilities().size() > 1) {
			int lineY = abilityOriginY + Constants.ABILITY_HEIGHT;
			int lineExtensionGap = 36;
			g.setColor(Color.BLACK);
			g.drawLine(lineExtensionGap, lineY, canvas.getWidth() - lineExtensionGap, lineY);
		}
	}

	private void drawHP(Pokemon pokemon, BufferedImage canvas, Graphics2D g, Font regularFont) {
		int hpXPosition = canvas.getWidth() - 156;
		int hpYPosition = 64;

		g.setFont(regularFont.deriveFont(28f));
		g.setColor(Color.RED);
		g.drawString(pokemon.getHp() + " HP", hpXPosition, hpYPosition);
	}

	private void drawPokemonName(Pokemon pokemon, Graphics2D g, Font boldFont) {
		int nameTextPositionX = 48;
		int nameTextPositionY = 64;

		g.setFont(boldFont.deriveFont(28f));
		g.setColor(Color.BLACK);
		g.drawString(pokemon.getName(), nameTextPositionX, nameTextPositionY);
	}

	private void drawPokemonImageInsideTemplateCard(BufferedImage pokemonImage, BufferedImage cardTemplateImage,
			Graphics2D g) {
		double rescaleFactor = Constants.IDEAL_CARD_WIDTH / pokemonImage.getWidth();

		int resizedPokemonImageWidth = (int) (pokemonImage.getWidth() * rescaleFactor);
		int resizedPokemonImageHeight = (int) (pokemonImage.getHeight() * rescaleFactor);

		pokemonImage = resize(pokemonImage, resizedPokemonImageWidth, resizedPokemonImageHeight);

		double cardCenterX = (double) cardTemplateImage.getWidth() / 2;
		double cardCenterY = 210;
		double pokemonImageX = cardCenterX - (double) pokemonImage.getWidth() / 2;
		double pokemonImageY = cardCenterY - (double) pokemonImage.getHeight() / 2;

		g.drawImage(pokemonImage, (int) pokemonImageX, (int) pokemonImageY, null);
		g.drawImage(cardTemplateImage, 0, 0, null);
	}

	private BufferedImage getCardTemplateImage(Pokemon pokemon) {
		String cardTemplateName = pokemon.getElement().getElementName().toLowerCase() + "_card.png";
		BufferedImage cardTemplateImage = null;
		try {
			cardTemplateImage = ImageIO.read(
					ResourceUtils.getFile("classpath:generator/cards/templates/" + cardTemplateName));
		} catch (IOException e) {
			CardRenderer.LOGGER.error("Error during reading pokemon template image", e);
		}
		return cardTemplateImage;
	}

	void drawString(Graphics2D g, String text, int x, int y) {
		for (String line : text.split("\\.")) {
			if (line.length() > 85 && line.contains(",")) {
				g.drawString(line.substring(0, line.lastIndexOf(",") + 1), x, y += g.getFontMetrics().getHeight());
				g.drawString(line.substring(line.lastIndexOf(",") + 1), x, y += g.getFontMetrics().getHeight());
			} else if (line.length() >= 85) {
				int newLineIndex = line.indexOf(" ", 75);
				g.drawString(line.substring(0, newLineIndex + 1), x, y += g.getFontMetrics().getHeight());
				g.drawString(line.substring(newLineIndex), x, y += g.getFontMetrics().getHeight());
			} else {
				g.drawString(line, x, y += g.getFontMetrics().getHeight());
			}
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
		try {
			elementImage = ImageIO.read(ResourceUtils.getFile(
					"classpath:generator/elements/" + element.getElementName().toLowerCase() + "_element.png"));
		} catch (IOException e) {
			CardRenderer.LOGGER.error("Error during getting ");
		}

		if (elementImage == null) {
			CardRenderer.LOGGER.info("Skipping element, because cannot process its image");
		} else {
			elementImage = resize(elementImage, Constants.STATUS_SIZE, Constants.STATUS_SIZE);
			g.drawImage(elementImage, positionX - Math.floorDiv(Constants.STATUS_SIZE, 2),
					Constants.STATUS_Y_POSITION - Math.floorDiv(Constants.STATUS_SIZE, 2), null);
		}
	}

	private BufferedImage drawAbility(Ability ability, Font boldFont, Font regularFont) {
		BufferedImage canvas = new BufferedImage(Constants.ABILITY_WIDTH, Constants.ABILITY_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = canvas.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		BufferedImage costImage = drawElementCost(ability.costsAsElements());
		g.drawImage(costImage, 0, 0, null);

		int nameTextPositionX = Math.floorDiv(Constants.ABILITY_WIDTH, 2) - 70;
		int nameTextPositionY = Math.floorDiv(Constants.ABILITY_HEIGHT, 2) + 10;

		g.setFont(boldFont.deriveFont(24f));
		g.setColor(Color.BLACK);
		g.drawString(ability.getName(), nameTextPositionX, nameTextPositionY);

		int powerTextPositionX = Constants.ABILITY_WIDTH - 45;
		int powerTextPositionY = Math.floorDiv(Constants.ABILITY_HEIGHT, 2) + 12;

		g.setFont(regularFont.deriveFont(32f));
		g.setColor(Color.BLACK);
		g.drawString(String.valueOf(ability.getPower()), powerTextPositionX, powerTextPositionY);

		g.dispose();

		return canvas;
	}

	private BufferedImage drawElementCost(List<String> elements) {
		BufferedImage canvas = new BufferedImage(Constants.ABILITY_COST_WIDTH, Constants.ABILITY_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = canvas.getGraphics();

		int cost = elements.size();
		int[][] positions = new int[cost][2];

		/*
		If there are two icons, they are centered and 20 pixels apart.
     	If there are three icons, they are in a triangle.
    	If there are four icons, they are in a square.
		 */

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

		int i = 0;
		for (String element : elements) {
			BufferedImage elementImage = null;
			try {
				elementImage = ImageIO.read(ResourceUtils.getFile(
						"classpath:generator/elements/" + element.toLowerCase() + "_element.png"));
			} catch (IOException e) {
				CardRenderer.LOGGER.error("Error during getting element image", e);
			}

			if (elementImage == null) {
				CardRenderer.LOGGER.info("Skipping element, because cannot process its image");
			} else {
				elementImage = resize(elementImage, Constants.ELEMENT_SIZE, Constants.ELEMENT_SIZE);
				g.drawImage(elementImage, positions[i][0] - Math.floorDiv(Constants.ELEMENT_SIZE, 2),
						positions[i][1] - Math.floorDiv(Constants.ELEMENT_SIZE, 2), null);
			}
			i++;
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
