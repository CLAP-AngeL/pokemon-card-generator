package com.petproject.pokemoncardgenerator.services.renderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
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
            LOGGER.error("Card template is absent, cannot proceed further");
            return null;
        }

        BufferedImage canvas = new BufferedImage(cardTemplateImage.getWidth(), cardTemplateImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Font boldFont = loadFont("generator/font/Cabin-Bold.ttf", Font.BOLD);
        Font regularFont = loadFont("generator/font/Cabin_Condensed-Regular.ttf", Font.PLAIN);
        Font symbolFont = loadFont("generator/font/NotoSansSymbols2-Regular.ttf", Font.PLAIN);

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

    private Font loadFont(String path, int style) {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
			if (is == null) {
				LOGGER.error("❌ Font resource NOT found at: {}", path);
				return new Font("SansSerif", style, 14);
			}
			LOGGER.info("✅ Font resource loaded successfully: {}", path);
			return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(style, 14f);
		} catch (Exception e) {
			LOGGER.error("⚠️ Failed to load or parse font: {}", path, e);
			return new Font("SansSerif", style, 14);
		}
	}

    private BufferedImage getCardTemplateImage(Pokemon pokemon) {
        String name = pokemon.getElement().getElementName().toLowerCase() + "_card.png";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("generator/cards/templates/" + name)) {
            return ImageIO.read(is);
        } catch (IOException e) {
            LOGGER.error("Error loading card template image: {}", name, e);
            return null;
        }
    }

    private void drawPokemonImageInsideTemplateCard(BufferedImage pokemonImage, BufferedImage cardTemplateImage, Graphics2D g) {
        double scale = Constants.IDEAL_CARD_WIDTH / (double) pokemonImage.getWidth();
        int newW = (int) (pokemonImage.getWidth() * scale);
        int newH = (int) (pokemonImage.getHeight() * scale);

        pokemonImage = resize(pokemonImage, newW, newH);

        int x = (cardTemplateImage.getWidth() - pokemonImage.getWidth()) / 2;
        int y = 210 - (pokemonImage.getHeight() / 2);

        g.drawImage(pokemonImage, x, y, null);
        g.drawImage(cardTemplateImage, 0, 0, null);
    }

    private void drawPokemonName(Pokemon pokemon, Graphics2D g, Font font) {
        g.setFont(font.deriveFont(28f));
        g.setColor(Color.BLACK);
        g.drawString(pokemon.getName(), 48, 64);
    }

    private void drawHP(Pokemon pokemon, BufferedImage canvas, Graphics2D g, Font font) {
        g.setFont(font.deriveFont(28f));
        g.setColor(Color.RED);
        g.drawString(pokemon.getHp() + " HP", canvas.getWidth() - 156, 64);
    }

    private void drawAbilities(Pokemon pokemon, BufferedImage canvas, Graphics2D g, Font boldFont, Font regularFont) {
        int x = (canvas.getWidth() - Constants.ABILITY_WIDTH) / 2;
        int centerY = 450;
        int originY = (pokemon.getAbilities().size() == 1) ? centerY - Constants.ABILITY_HEIGHT / 2 :
                     (pokemon.getAbilities().size() == 2) ? centerY - Constants.ABILITY_HEIGHT - Constants.ABILITY_COST_GAP / 2 : 0;

        List<Ability> reversed = IntStream.range(0, pokemon.getAbilities().size())
                .map(i -> pokemon.getAbilities().size() - 1 - i)
                .mapToObj(pokemon.getAbilities()::get).toList();

        for (int i = 0; i < reversed.size(); i++) {
            BufferedImage abilityImage = drawAbility(reversed.get(i), boldFont, regularFont);
            int y = originY + i * (Constants.ABILITY_HEIGHT + Constants.ABILITY_COST_GAP);
            g.drawImage(abilityImage, x, y, null);
        }

        if (reversed.size() > 1) {
            int y = originY + Constants.ABILITY_HEIGHT;
            g.setColor(Color.BLACK);
            g.drawLine(36, y, canvas.getWidth() - 36, y);
        }
    }

    private void drawRarity(Pokemon pokemon, BufferedImage canvas, Graphics2D g, Font regularFont, Font symbolFont) {
        g.setFont(regularFont.deriveFont(Font.ITALIC, 12f));
        g.setColor(Color.BLACK);
        drawString(g, pokemon.getDescription(), 58, 580);

        String[] symbols = { "\u2B24", "\u25C6", "\u2605" };
        float[] sizes = { 12f, 12f, 14f };
        g.setFont(symbolFont.deriveFont(sizes[pokemon.getRarity().ordinal()]));

        g.drawString(symbols[pokemon.getRarity().ordinal()], canvas.getWidth() - 74, 634);
    }

    private void drawWeaknessAndResist(Pokemon pokemon, BufferedImage canvas, Graphics2D g) {
        PokemonElement resist = PokemonElement.getResist(pokemon.getElement());
        PokemonElement weak = PokemonElement.getWeakness(pokemon.getElement());

        if (weak != null) drawElement(g, weak, Constants.STATUS_X_GAP);
        if (resist != null) drawElement(g, resist, canvas.getWidth() / 2);
        drawElement(g, PokemonElement.NEUTRAL, canvas.getWidth() - Constants.STATUS_X_GAP);
    }

    private void drawElement(Graphics2D g, PokemonElement element, int x) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("generator/elements/" + element.getElementName().toLowerCase() + "_element.png")) {
            BufferedImage img = ImageIO.read(is);
            img = resize(img, Constants.STATUS_SIZE, Constants.STATUS_SIZE);
            g.drawImage(img, x - Constants.STATUS_SIZE / 2, Constants.STATUS_Y_POSITION - Constants.STATUS_SIZE / 2, null);
        } catch (IOException e) {
            LOGGER.error("Missing or unreadable element icon: " + element.getElementName(), e);
        }
    }

    private BufferedImage drawAbility(Ability ability, Font boldFont, Font regularFont) {
        BufferedImage canvas = new BufferedImage(Constants.ABILITY_WIDTH, Constants.ABILITY_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.drawImage(drawElementCost(ability.costsAsElements()), 0, 0, null);

        g.setFont(boldFont.deriveFont(24f));
        g.setColor(Color.BLACK);
        g.drawString(ability.getName(), Constants.ABILITY_WIDTH / 2 - 70, Constants.ABILITY_HEIGHT / 2 + 10);

        g.setFont(regularFont.deriveFont(32f));
        g.drawString(String.valueOf(ability.getPower()), Constants.ABILITY_WIDTH - 45, Constants.ABILITY_HEIGHT / 2 + 12);

        g.dispose();
        return canvas;
    }

    private BufferedImage drawElementCost(List<String> elements) {
        BufferedImage canvas = new BufferedImage(Constants.ABILITY_COST_WIDTH, Constants.ABILITY_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics g = canvas.getGraphics();

        int[][] positions = new int[elements.size()][2];
        int cx = Constants.ABILITY_COST_WIDTH / 2;
        int cy = Constants.ABILITY_HEIGHT / 2;
        int dx = Constants.ELEMENT_SIZE + Constants.ABILITY_GAP;

        if (elements.size() == 1) positions[0] = new int[]{cx, cy};
        else if (elements.size() == 2) {
            positions[0] = new int[]{cx - dx / 2, cy};
            positions[1] = new int[]{cx + dx / 2, cy};
        } else if (elements.size() == 3) {
            positions[0] = new int[]{cx - dx / 2, cy - dx / 2};
            positions[1] = new int[]{cx + dx / 2, cy - dx / 2};
            positions[2] = new int[]{cx, cy + dx / 2};
        } else if (elements.size() == 4) {
            positions[0] = new int[]{cx - dx / 2, cy - dx / 2};
            positions[1] = new int[]{cx + dx / 2, cy - dx / 2};
            positions[2] = new int[]{cx - dx / 2, cy + dx / 2};
            positions[3] = new int[]{cx + dx / 2, cy + dx / 2};
        }

        for (int i = 0; i < elements.size(); i++) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("generator/elements/" + elements.get(i).toLowerCase() + "_element.png")) {
                BufferedImage icon = ImageIO.read(is);
                icon = resize(icon, Constants.ELEMENT_SIZE, Constants.ELEMENT_SIZE);
                g.drawImage(icon, positions[i][0] - Constants.ELEMENT_SIZE / 2, positions[i][1] - Constants.ELEMENT_SIZE / 2, null);
            } catch (IOException e) {
                LOGGER.error("Unable to load cost element icon for: " + elements.get(i), e);
            }
        }

        g.dispose();
        return canvas;
    }

    public void drawString(Graphics2D g, String text, int x, int y) {
        for (String line : text.split("\\.")) {
            if (line.length() > 85 && line.contains(",")) {
                g.drawString(line.substring(0, line.lastIndexOf(",") + 1), x, y += g.getFontMetrics().getHeight());
                g.drawString(line.substring(line.lastIndexOf(",") + 1), x, y += g.getFontMetrics().getHeight());
            } else if (line.length() >= 85) {
                int idx = line.indexOf(" ", 75);
                if (idx != -1) {
                    g.drawString(line.substring(0, idx + 1), x, y += g.getFontMetrics().getHeight());
                    g.drawString(line.substring(idx), x, y += g.getFontMetrics().getHeight());
                } else {
                    g.drawString(line, x, y += g.getFontMetrics().getHeight());
                }
            } else {
                g.drawString(line, x, y += g.getFontMetrics().getHeight());
            }
        }
    }

    public BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
