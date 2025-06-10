package com.petproject.pokemoncardgenerator.services.telegrambot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import com.petproject.pokemoncardgenerator.model.PokemonParameters;
import com.petproject.pokemoncardgenerator.model.details.enums.PokemonElement;
import com.petproject.pokemoncardgenerator.services.generator.CardProcessor;

@Service
public class ServiceForTelegramBotCommunication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceForTelegramBotCommunication.class);
    private TelegramLongPollingBot bot;
    private final CardProcessor cardProcessor;

    private final Map<Long, PokemonElement> userElementMap = new ConcurrentHashMap<>();
    private final Map<Long, String> userConceptMap = new ConcurrentHashMap<>();

    public ServiceForTelegramBotCommunication(CardProcessor cardProcessor) {
        this.cardProcessor = cardProcessor;
    }

    public void setBot(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendMultiplePhotoMessage(long chatId, List<BufferedImage> contents, int replyToMessageId) {
        if (contents.isEmpty()) return;

        if (contents.size() == 1) {
            sendPhotoMessage(chatId, contents.get(0), replyToMessageId);
        } else if (contents.size() <= 10) {
            sendMediaGroup(chatId, contents, replyToMessageId);
        } else {
            LOGGER.error("Cannot send more than 10 images");
        }
    }

    public void sendPhotoMessage(long chatId, BufferedImage content, int replyToMessageId) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(content, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(is, "pokemon"))
                .caption("")
                .replyToMessageId(replyToMessageId)
                .parseMode("HTML")
                .build();
            sendMessage(sendPhoto);
        } catch (IOException e) {
            LOGGER.error("Error while preprocessing card image", e);
        }
    }

    private void sendMediaGroup(long chatId, List<BufferedImage> contents, int replyToMessageId) {
        List<InputMedia> medias = contents.stream().map(image -> {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                String name = UUID.randomUUID().toString();
                return InputMediaPhoto.builder()
                        .media("attach://" + name)
                        .mediaName(name)
                        .isNewMedia(true)
                        .newMediaStream(is)
                        .caption("Your custom Pokemon card is ready!!!^^")
                        .parseMode("HTML")
                        .build();
            } catch (IOException e) {
                LOGGER.error("Error generating media group", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                .chatId(chatId)
                .medias(medias)
                .replyToMessageId(replyToMessageId)
                .build();
        sendMessage(sendMediaGroup);
    }

    private void sendMessage(SendPhoto message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending photo message", e);
        }
    }

    private void sendMessage(SendMediaGroup message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending media group", e);
        }
    }

    public void elementCommandReceived(long chatId, long userId, String element) {
        PokemonElement selectedElement = switch (element.toUpperCase()) {
            case "FIRE" -> PokemonElement.FIRE;
            case "WATER" -> PokemonElement.WATER;
            case "GRASS" -> PokemonElement.GRASS;
            case "FIGHTING" -> PokemonElement.FIGHTING;
            case "ELECTRIC" -> PokemonElement.ELECTRIC;
            case "PSYCHIC" -> PokemonElement.PSYCHIC;
            case "RANDOM" -> PokemonElement.values()[ThreadLocalRandom.current().nextInt(PokemonElement.values().length - 1)];
            default -> PokemonElement.NEUTRAL;
        };

        userElementMap.put(userId, selectedElement);
    }

    public void generateCommandReceived(long chatId, long userId, int replyToMessageId) {
        PokemonElement element = userElementMap.get(userId);
        String concept = userConceptMap.get(userId);

        if (element == null || concept == null || concept.isBlank()) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setReplyToMessageId(replyToMessageId);
            message.setText("⚠️ Please provide both an element and a subject before generating!");
            sendMessage(message);
            return;
        }

        PokemonParameters parameters = PokemonParameters.builder()
                .element(element)
                .pokemonConcept(concept)
                .build();
        List<BufferedImage> images = cardProcessor.generateCards(parameters);
        sendMultiplePhotoMessage(chatId, images, replyToMessageId);

        userElementMap.remove(userId);
        userConceptMap.remove(userId);
    }

    public void helpCommandReceived(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(
                "🔥 Create your custom Pokémon card!\n\n" +
                "🧪 Usage:\n" +
                "/create_pokemon element-subject\n\n" +
                "💡 Example:\n" +
                "/create_pokemon fire-wolf with blue fur\n\n" +
                "🌈 Elements:\n" +
                "FIRE, WATER, GRASS, FIGHTING, ELECTRIC, PSYCHIC, RANDOM"
        );
        sendMessage(message);
    }

    public void startCommandReceived(long chatId, long userId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Welcome to the Pokemon Card Generator!\nUse /help for instructions.");
        sendMessage(message);
    }

    public void subjectCommandReceived(long chatId, long userId, String subject) {
        userConceptMap.put(userId, subject);
    }


    public void combinedGenerateCommand(long chatId, long userId, String element, String subject, int replyToMessageId) {
        elementCommandReceived(chatId, userId, element);
        userConceptMap.put(userId, subject);
        generateCommandReceived(chatId, userId, replyToMessageId);
    }

    private void sendMessage(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending message", e);
        }
    }
}
