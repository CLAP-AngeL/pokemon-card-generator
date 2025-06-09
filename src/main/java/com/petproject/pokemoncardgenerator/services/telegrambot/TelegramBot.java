package com.petproject.pokemoncardgenerator.services.telegrambot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.petproject.pokemoncardgenerator.services.telegrambot.config.BotConfig;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBot.class);
    private final BotConfig config;
    private final ServiceForTelegramBotCommunication serviceForTelegramBotCommunication;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10, 20, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private final ConcurrentHashMap<Long, Boolean> activeUsers = new ConcurrentHashMap<>();

    public TelegramBot(BotConfig botConfig, ServiceForTelegramBotCommunication serviceForTelegramBotCommunication) {
        super(botConfig.getToken());
        this.config = botConfig;
        this.serviceForTelegramBotCommunication = serviceForTelegramBotCommunication;

        serviceForTelegramBotCommunication.setBot(this);

        final List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/create_pokemon", "Generate your Pokémon: /create_pokemon element-subject"));
        listOfCommands.add(new BotCommand("/help", "List element categories and usage example"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            LOGGER.error("Error while setting telegram commands", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            executor.submit(() -> processUpdate(update));
        } catch (RejectedExecutionException e) {
            LOGGER.warn("Bot is under heavy load. Update rejected: " + e.getMessage());
        }
    }

    private void processUpdate(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText().trim();
                long chatId = update.getMessage().getChatId();
                long userId = update.getMessage().getFrom().getId();

                // Ignore all messages that are not commands
                if (!messageText.startsWith("/")) return;

                // Remove @botname suffix if present (in group chats)
                String baseCommand = messageText.split(" ")[0].split("@")[0];

                switch (baseCommand) {
                    case "/help" -> serviceForTelegramBotCommunication.helpCommandReceived(chatId);
                    case "/create_pokemon" -> {
                        if (activeUsers.putIfAbsent(userId, true) != null) {
                            sendTextMessage(chatId, "⏳ You already have a request being processed. Please wait.");
                            return;
                        }

                        sendTextMessage(chatId, "✅ Your request has been received and is being processed...");

                        executor.submit(() -> {
                            try {
                                String payload = messageText.replaceFirst("/create_pokemon(@\\w+)?", "").trim();
                                String[] parts = payload.split("-", 2);
                                if (parts.length < 2) {
                                    serviceForTelegramBotCommunication.helpCommandReceived(chatId);
                                } else {
                                    String element = parts[0].trim();
                                    String subject = parts[1].trim();
                                    serviceForTelegramBotCommunication.combinedGenerateCommand(chatId, userId, element, subject);
                                }
                            } catch (Exception e) {
                                LOGGER.error("Error in user task", e);
                            } finally {
                                activeUsers.remove(userId);
                            }
                        });
                    }
                    default -> {
                        // Do nothing for any unknown command
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while processing update", e);
        }
    }

    private void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send message to user", e);
        }
    }

    @Override
    public String getBotUsername() {
        return this.config.getBotName();
    }
}