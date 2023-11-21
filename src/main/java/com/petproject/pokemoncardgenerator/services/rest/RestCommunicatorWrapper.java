package com.petproject.pokemoncardgenerator.services.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Wrapper For Rest Communicator in order @Retryable to be invoked
 */
@Service
public class RestCommunicatorWrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestCommunicatorWrapper.class);
	private final RestCommunicator restCommunicator;

	@Value("${huggingface.api.tocken}")
	private String apiToken;
	@Value("${generativeai:false}")
	private Boolean isAIAllowed;

	public RestCommunicatorWrapper(RestCommunicator restCommunicator) {
		this.restCommunicator = restCommunicator;
	}

	public String executeTextGenerationRestCall(String model, String prompt) {
		ResponseEntity<String> response = restCommunicator.executeTextToTextRestCall(prompt, model);

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

	public BufferedImage executeImageGenerationRestCall(String model, String prompt) {
		ResponseEntity<byte[]> response = restCommunicator.executeTextToImageRestCall(prompt, model);

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
	public boolean isApiEnabled() {
		return apiToken != null && isAIAllowed;
	}


}
