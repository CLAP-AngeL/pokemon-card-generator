package com.petproject.pokemoncardgenerator.services.telegrambot;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.petproject.pokemoncardgenerator.model.PokemonParameters;
import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;
import com.petproject.pokemoncardgenerator.services.generator.CardProcessor;

@Service
public class ServiceForTelegramBotCommunication {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceForTelegramBotCommunication.class);
	private TelegramLongPollingBot bot;
	private final CardProcessor cardProcessor;

	private PokemonElement element;
	private String pokemonConcept;

	public ServiceForTelegramBotCommunication(CardProcessor cardProcessor) {
		this.cardProcessor = cardProcessor;
	}

	public void setBot(TelegramLongPollingBot bot) {
		this.bot = bot;
	}

	public void sendMultiplePhotoMessage(long chatId, List<BufferedImage> contents) {

		if (contents.isEmpty()) {
			return;
		}

		int contentSize = contents.size();

		if (contentSize == 1) {
			sendPhotoMessage(chatId, contents.get(0));
		} else if (contentSize <= 10) {
			sendMediaGroup(chatId, contents);
		} else {
			ServiceForTelegramBotCommunication.LOGGER.error("Cant send more than 10 images");
		}
	}

	public void sendPhotoMessage(long chatId, BufferedImage content) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(content, "png", os);
		} catch (IOException e) {
			LOGGER.error("Error while preprocessing card images for telegram bot", e);
		}
		InputStream is = new ByteArrayInputStream(os.toByteArray());

		SendPhoto sendPhoto = SendPhoto.builder().chatId(chatId).photo(new InputFile(is, "pokemon"))
				.caption("Please enjoy your own custom pokemon cards!^^").parseMode(ParseMode.HTML).build();

		sendMessage(sendPhoto);
	}

	private void sendMessage(SendPhoto sendPhoto) {
		try {
			bot.execute(sendPhoto);
		} catch (TelegramApiException e) {
			ServiceForTelegramBotCommunication.LOGGER.error("Can't send photo message", e);
		}
	}

	//If we have 2-10 photos, send them with SendMediaGroup. In code below we are loading files from our server.
	private void sendMediaGroup(long chatId, List<BufferedImage> contents) {
		List<InputMedia> medias = contents.stream().map(userContent -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				ImageIO.write(userContent, "png", os);
			} catch (IOException e) {
				LOGGER.error("Error occurred during processing the result images to telegram");
			}
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			String mediaName = UUID.randomUUID().toString();

			InputMedia inputMedia = InputMediaPhoto.builder().media("attach://" + mediaName).mediaName(mediaName)
					.isNewMedia(true).newMediaStream(is).caption("Please enjoy your own custom pokemon cards!^^")
					.parseMode(ParseMode.HTML).build();
			return inputMedia;
		}).toList();

		SendMediaGroup sendMediaGroup = SendMediaGroup.builder().chatId(chatId).medias(medias).build();

		sendMessage(sendMediaGroup);
	}

	private void sendMessage(SendMediaGroup sendMediaGroup) {
		try {
			bot.execute(sendMediaGroup);
		} catch (TelegramApiException e) {
			ServiceForTelegramBotCommunication.LOGGER.error("Can't send photos with media group", e);
		}
	}

	public void elementCommandReceived(long chatId) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText("Please choose element for your pokemon:");

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> rows = new ArrayList<>();

		PokemonElement[] elements = PokemonElement.values();
		int i = 0;
		for (int j = 0; j < 3; j++) {
			List<InlineKeyboardButton> row = new ArrayList<>();
			rows.add(row);

			while (i < elements.length - 1) {
				InlineKeyboardButton button = new InlineKeyboardButton(elements[i].getElementName());
				button.setCallbackData(elements[i].getElementName().toUpperCase());
				row.add(button);
				i++;

				if (i % 3 == 0) {
					break;
				}
			}
		}

		InlineKeyboardButton randomButton = new InlineKeyboardButton("Random");
		randomButton.setCallbackData("RANDOM");
		rows.get(2).add(randomButton);

		inlineKeyboardMarkup.setKeyboard(rows);
		message.setReplyMarkup(inlineKeyboardMarkup);

		sendMessage(message);
	}

	public void subjectCommandReceived(long chatId, String pokemonSubject) {
		pokemonConcept = pokemonSubject;
		String messageText = String.format("You have chosen %s as a subject for your pokemon.", pokemonSubject);
		if (element != null && pokemonConcept != null) {
			messageText += "\nPlease use \"/generate\" to generate the pokemon cards.";
		} else if (element == null && (pokemonConcept == null || pokemonConcept.isBlank())) {
			messageText += "\nPlease use \"/element\" to choose the desired element. \nPlease use \"/subject (for e.g. cat)\" to choose how your pokemon would look like.";
		} else if (element == null) {
			messageText += "\nPlease use \"/element\" to choose the desired element.";
		} else {
			messageText += "\nPlease use \"/subject (for e.g. cat)\" to choose how your pokemon would look like.";
		}

		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(messageText);

		sendMessage(message);
	}

	public void helpCommandReceived(long chatId) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(
				"\\This tool generates custom pokemon cards, by using your prompts. Refer to https://github.com/bratzzie/pokemon-card-generator\n\n"
						+ "You need to select element, subject and then, you can finally generate the images via \\generate command.\n\n"
						+ "\\element command give you an opportunity to choose from the given elements the element of your pokemon\n\n"
						+ "\\subject command should be given like this: \\subject mushroom fluffy cow, so command [detailed description how you pokemon looks like]\n\n"
						+ "\\generate command sends you either series or one card");
		sendMessage(message);
	}

	public void startCommandReceived(long chatId) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText("Welcome to the Pokemon Card Generator!\n"
				+ " Feel yourself free and let's create some new cards! Choose help, if you don't know how to use this tool.");
		sendMessage(message);
	}

	public void generateCommandReceived(long chatId) {
		PokemonParameters parameters = PokemonParameters.builder().element(element).pokemonConcept(pokemonConcept)
				.build();

		List<BufferedImage> images = cardProcessor.generateCards(parameters);
		sendMultiplePhotoMessage(chatId, images);

		pokemonConcept = null;
		element = null;
	}

	public void callbackQueryReceived(int messageId, long chatId, String callbackData) {
		String messageText;

		switch (callbackData) {
			case "FIRE" -> element = PokemonElement.FIRE;
			case "WATER" -> element = PokemonElement.WATER;
			case "GRASS" -> element = PokemonElement.GRASS;
			case "FIGHTING" -> element = PokemonElement.FIGHTING;
			case "ELECTRIC" -> element = PokemonElement.ELECTRIC;
			case "PSYCHIC" -> element = PokemonElement.PSYCHIC;
			case "RANDOM" -> {
				int randomNum = ThreadLocalRandom.current().nextInt(PokemonElement.values().length - 1);
				if (randomNum == 7) {
					randomNum--;
				}
				element = PokemonElement.values()[randomNum];
			}
			default -> element = PokemonElement.NEUTRAL;
		}

		messageText = String.format("You have chosen %s type pokemon", element.getElementName());
		EditMessageText message = new EditMessageText();
		message.setChatId(chatId);
		message.setText(messageText);
		message.setMessageId(messageId);

		sendMessage(message);
	}

	private void sendMessage(SendMessage message) {
		try {
			bot.execute(message);
		} catch (final TelegramApiException e) {
			LOGGER.error("Error occurred during sending message to bot", e);
		}
	}

	private void sendMessage(EditMessageText message) {
		try {
			bot.execute(message);
		} catch (final TelegramApiException e) {
			LOGGER.error("Error occurred during sending message to bot", e);
		}
	}
}
