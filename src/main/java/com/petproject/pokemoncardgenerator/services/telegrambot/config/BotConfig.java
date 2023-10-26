package com.petproject.pokemoncardgenerator.services.telegrambot.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class BotConfig {

	@Value("${bot.name}")
	private String botName;

	@Value("${bot.token}")
	private String token;

	public BotConfig() {
	}

	public BotConfig(String botName, String token) {
		this.botName = botName;
		this.token = token;
	}

	public String getBotName() {
		return this.botName;
	}

	public String getToken() {
		return this.token;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		final BotConfig botConfig = (BotConfig) o;

		if (!Objects.equals(this.botName, botConfig.botName)) {
			return false;
		}
		return Objects.equals(this.token, botConfig.token);
	}

	@Override
	public int hashCode() {
		int result = this.botName != null ? this.botName.hashCode() : 0;
		result = 31 * result + (this.token != null ? this.token.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "BotConfig{" + "botName='" + this.botName + '\'' + ", token='" + this.token + '\'' + '}';
	}
}
