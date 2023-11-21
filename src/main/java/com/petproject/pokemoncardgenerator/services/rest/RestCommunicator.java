package com.petproject.pokemoncardgenerator.services.rest;

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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.petproject.pokemoncardgenerator.services.rest.error.ModelIsLoadingException;

/**
 * Service for making API requests to Hugging Face Generative Models via API Inferences
 */
@Service
public class RestCommunicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestCommunicator.class);
	private static final String MAX_AMOUNT_TOKENS = "70";

	@Value("${huggingface.api.tocken}")
	private String apiToken;

	private final RestTemplate restTemplate;

	public RestCommunicator(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Retryable(retryFor= ModelIsLoadingException.class, maxAttempts = 4, backoff = @Backoff(delay = 20000, maxDelay = 160000, multiplier = 2))
	public ResponseEntity<String> executeTextToTextRestCall(String prompt, String model) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = String.format("{\"inputs\":\"%s\", \"parameters\":{\"max_new_tokens\":%s}}", prompt,
				MAX_AMOUNT_TOKENS);
		HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange("https://api-inference.huggingface.co/models/".concat(model),
					HttpMethod.POST, httpEntity, String.class);

		} catch (RestClientResponseException exception) {
			if (exception.getStatusCode().is5xxServerError()){
				LOGGER.error("Text generation rest exception", exception);
				throw new ModelIsLoadingException(exception.getResponseBodyAsString());
			}
		}

		return response;
	}

	@Recover
	public ResponseEntity<String> executeTextToTextRestCallRecover(ModelIsLoadingException exception, String prompt, String model) {
		LOGGER.error("An error occurred during rest request for the text generation. Proceed without requested text.");
		return null;
	}

	@Retryable(retryFor= ModelIsLoadingException.class, maxAttempts = 4, backoff = @Backoff(delay = 500, maxDelay = 160000, multiplier = 2))
	public ResponseEntity<byte[]> executeTextToImageRestCall(String prompt, String model) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = String.format("{\"inputs\":\"%s\"}", prompt);
		HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<byte[]> response = null;
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
		try {
			response = restTemplate.exchange("https://api-inference.huggingface.co/models/".concat(model),
					HttpMethod.POST, httpEntity, byte[].class);
		} catch (RestClientResponseException exception) {
			if (exception.getStatusCode().is5xxServerError()){
				LOGGER.error("Image generation rest exception", exception);
				throw new ModelIsLoadingException(exception.getResponseBodyAsString());
			}
		}

		return response;
	}

	@Recover
	public ResponseEntity<byte[]> executeTextToImageRestCall(ModelIsLoadingException exception, String prompt, String model) {
		LOGGER.error("An error occurred during rest request for the image generation. Proceed without requested image.");
		return null;
	}
}
