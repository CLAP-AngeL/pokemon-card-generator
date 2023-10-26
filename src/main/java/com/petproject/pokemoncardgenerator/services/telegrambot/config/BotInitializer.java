package com.petproject.pokemoncardgenerator.services.telegrambot.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.petproject.pokemoncardgenerator.services.telegrambot.TelegramBot;

@Component
public class BotInitializer {

	private final TelegramBot telegramBot;

	public BotInitializer(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	@EventListener({ ContextRefreshedEvent.class })
	public void init() {
		TelegramBotsApi telegramBotsApi = null;
		try {
			telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(this.telegramBot);
		} catch (final TelegramApiException e) {
			throw new RuntimeException(e);
		}

	}
}
