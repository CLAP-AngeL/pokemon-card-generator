package com.petproject.pokemoncardgenerator.services.telegrambot;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
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

	public TelegramBot(BotConfig botConfig, ServiceForTelegramBotCommunication serviceForTelegramBotCommunication) {
		super(botConfig.getToken());
		this.config = botConfig;
		this.serviceForTelegramBotCommunication = serviceForTelegramBotCommunication;

		serviceForTelegramBotCommunication.setBot(this);

		final List<BotCommand> listOfCommands = new ArrayList<>();
		listOfCommands.add(new BotCommand("/start", "start bot for generating your custom pokemon cards!"));
		listOfCommands.add(new BotCommand("/help", "display all possible commands and documentation"));
		listOfCommands.add(new BotCommand("/element", "choose element for your pokemon"));
		listOfCommands.add(
				new BotCommand("/subject", "choose how your pokemon looks like, e.g. \\subject wolf with blue fur"));
		listOfCommands.add(new BotCommand("/generate", "generate random card series of your pokemon!"));

		try {
			this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
		} catch (TelegramApiException e) {
			LOGGER.error("Error while setting telegram commands", e);
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String messageText = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();

			switch (messageText) {
				case "/start" -> serviceForTelegramBotCommunication.startCommandReceived(chatId);
				case "/help" -> serviceForTelegramBotCommunication.helpCommandReceived(chatId);
				case "/element" -> serviceForTelegramBotCommunication.elementCommandReceived(chatId);
				case "/generate" -> serviceForTelegramBotCommunication.generateCommandReceived(chatId);
				default -> {
					if (messageText.contains("/subject ")) {
						serviceForTelegramBotCommunication.subjectCommandReceived(chatId,
								messageText.replace("/subject ", ""));
					}
				}
			}
		} else if (update.hasCallbackQuery()) {
			int messageId = update.getCallbackQuery().getMessage().getMessageId();
			long chatId = update.getCallbackQuery().getMessage().getChatId();
			String callbackData = update.getCallbackQuery().getData();
			serviceForTelegramBotCommunication.callbackQueryReceived(messageId, chatId, callbackData);
		}
	}

	@Override
	public String getBotUsername() {
		return this.config.getBotName();
	}

}
