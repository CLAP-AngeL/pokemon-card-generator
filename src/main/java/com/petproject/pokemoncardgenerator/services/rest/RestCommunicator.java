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
import java.util.Collections;

import com.petproject.pokemoncardgenerator.services.rest.error.ModelIsLoadingException;

/**
 * Service for making API requests to Hugging Face Generative Models via API Inferences
 */
@Service
public class RestCommunicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestCommunicator.class);
	private static final String MAX_AMOUNT_TOKENS = "70";

	@Value("${huggingface.api.token}")
	private String apiToken;

	@Value("${model.image.url}")
	private String imageModelUrl;

	@Value("${model.text.url}")
	private String textModelUrl;

	private final RestTemplate restTemplate;

	public RestCommunicator(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Retryable(retryFor= ModelIsLoadingException.class, maxAttempts = 4, backoff = @Backoff(delay = 20000, maxDelay = 160000, multiplier = 2))
	public ResponseEntity<String> executeTextToTextRestCall(String prompt) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(apiToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		String requestBody = String.format(
			"{\"inputs\":\"%s\", \"parameters\":{\"max_new_tokens\":%s}}",
			prompt, MAX_AMOUNT_TOKENS
		);

		HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				textModelUrl,
				HttpMethod.POST,
				httpEntity,
				String.class
			);

			return response;

		} catch (RestClientResponseException exception) {
			LOGGER.error("REST Client Exception: {}", exception.getResponseBodyAsString());
			throw new RuntimeException("Error from model: " + exception.getRawStatusCode());
		}
	}

	@Recover
	public ResponseEntity<String> executeTextToTextRestCallRecover(ModelIsLoadingException exception, String prompt, String model) {
		LOGGER.error("An error occurred during rest request for the text generation. Proceed without requested text.");
		return null;
	}

	@Retryable(retryFor= ModelIsLoadingException.class, maxAttempts = 4, backoff = @Backoff(delay = 500, maxDelay = 160000, multiplier = 2))
	public ResponseEntity<byte[]> executeTextToImageRestCall(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_PNG)); // or IMAGE_JPEG, etc.

        String requestBody = String.format("{\"inputs\":\"%s\"}", prompt);
        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

        // Ensure image (byte[]) responses are handled
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        try {
            return restTemplate.exchange(
                imageModelUrl,
                HttpMethod.POST,
                httpEntity,
                byte[].class
            );
        } catch (RestClientResponseException exception) {
            System.err.println("REST Client Exception: " + exception.getResponseBodyAsString());
            throw new RuntimeException("Image generation error: " + exception.getRawStatusCode());
        }
    }

	@Recover
	public ResponseEntity<byte[]> executeTextToImageRestCall(ModelIsLoadingException exception, String prompt, String model) {
		LOGGER.error("An error occurred during rest request for the image generation. Proceed without requested image.");
		return null;
	}
}
