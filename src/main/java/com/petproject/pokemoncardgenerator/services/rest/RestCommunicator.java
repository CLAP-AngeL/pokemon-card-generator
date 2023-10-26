package com.petproject.pokemoncardgenerator.services.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

/**
 * Service for making API requests to Hugging Face Generative Models via API Inferences
 */
@Service
public class RestCommunicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestCommunicator.class);
	private static final String MAX_AMOUNT_TOKENS = "70";

	@Value("${huggingface.api.tocken}")
	private String apiToken;
	@Value("${generativeai:false}")
	private Boolean isAIAllowed;

	private final RestTemplate restTemplate;

	public RestCommunicator(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	public String executeTextToTextRestCall(String model, String prompt) {
		ResponseEntity<String> response = executeTextToTextRestCall(prompt, model, 0);

		if (response != null && response.getBody() != null) {
			String result = response.getBody();
			String str = "\\n\\n";
			int indexOfAnswer = result.indexOf(str);
			result = result.substring(indexOfAnswer);
			result = result.replace(str, "");
			result = result.replace("\"}]", "");

			return result;
		}

		return null;
	}

	public BufferedImage executeTextToImageRestCall(String model, String prompt) {
		ResponseEntity<byte[]> response = executeTextToImageRestCall(prompt, model, 0);

		if (response != null && response.getBody() != null) {
			byte[] result = response.getBody();
			try {
				return ImageIO.read(new ByteArrayInputStream(result));
			} catch (IOException e) {
				LOGGER.error("Error during transforming into BufferedImage image from the response", e);
			}
		}

		return null;
	}

	private ResponseEntity<String> executeTextToTextRestCall(String prompt, String model, int times) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = String.format("{\"inputs\":\"%s\", \"parameters\":{\"max_new_tokens\":%s}}", prompt,
				MAX_AMOUNT_TOKENS);
		HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response;
		try {
			response = restTemplate.exchange("https://api-inference.huggingface.co/models/".concat(model),
					HttpMethod.POST, httpEntity, String.class);

		} catch (RestClientResponseException exception) {
			LOGGER.error("Text generation rest exception", exception);
			LOGGER.info("Making repeat request for {} time(s)...", times + 1);

			if (times >= 5) {
				return null;
			}

			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			times++;
			response = executeTextToTextRestCall(prompt, model, times);
		}

		return response;
	}

	private ResponseEntity<byte[]> executeTextToImageRestCall(String prompt, String model, int times) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = String.format("{\"inputs\":\"%s\"}", prompt);
		HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<byte[]> response;
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
		try {
			response = restTemplate.exchange("https://api-inference.huggingface.co/models/".concat(model),
					HttpMethod.POST, httpEntity, byte[].class);
		} catch (RestClientResponseException exception) {
			LOGGER.error("Image generation rest exception", exception);
			LOGGER.info("Making repeat request for {} time(s)...", times + 1);

			if (times >= 5) {
				return null;
			}

			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			times++;
			response = executeTextToImageRestCall(prompt, model, times);
		}

		return response;
	}

	public boolean isApiEnabled() {
		return apiToken != null && isAIAllowed;
	}
}
