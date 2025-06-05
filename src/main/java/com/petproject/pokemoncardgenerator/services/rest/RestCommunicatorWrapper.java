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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

/**
 * Wrapper For Rest Communicator in order @Retryable to be invoked
 */
@Service
public class RestCommunicatorWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestCommunicatorWrapper.class);
    private final RestCommunicator restCommunicator;

    @Value("${huggingface.api.token}")  // Note: possible typo in property key (tocken → token)
    private String apiToken;

    @Value("${generativeai:false}")
    private Boolean isAIAllowed;

    public RestCommunicatorWrapper(RestCommunicator restCommunicator) {
        this.restCommunicator = restCommunicator;
    }

    public String executeTextGenerationRestCall(String model, String prompt) {
        try {
            ResponseEntity<String> response = restCommunicator.executeTextToTextRestCall(prompt);

            if (response != null && response.getBody() != null) {
                String result = response.getBody();
                String str = "\\n\\n";
                int indexOfAnswer = result.indexOf(str);

                if (indexOfAnswer >= 0) {
                    result = result.substring(indexOfAnswer);
                }

                result = result.replace(str, "").replace("\"}]", "");

                return result;
            }

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            LOGGER.error("HTTP error from Hugging Face API: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            LOGGER.error("RestClientException during text generation call", ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error during text generation call", ex);
        }

        return null;
    }

    public BufferedImage executeImageGenerationRestCall(String model, String prompt) {
        try {
            ResponseEntity<byte[]> response = restCommunicator.executeTextToImageRestCall(prompt);

            if (response != null && response.getBody() != null) {
                byte[] result = response.getBody();
                return ImageIO.read(new ByteArrayInputStream(result));
            }

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            LOGGER.error("HTTP error from Hugging Face API (image gen): {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            LOGGER.error("RestClientException during image generation call", ex);
        } catch (IOException ex) {
            LOGGER.error("IOException while transforming image response", ex);
        } catch (Exception ex) {
            LOGGER.error("Unexpected error during image generation call", ex);
        }

        return null;
    }

    public boolean isApiEnabled() {
        return apiToken != null && isAIAllowed;
    }
}
