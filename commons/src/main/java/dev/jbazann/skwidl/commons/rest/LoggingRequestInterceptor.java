package dev.jbazann.skwidl.commons.rest;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.logging.Logger;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public @NonNull ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        logger.info(String.format(
                "REQUEST %s %s",
                request.getMethod(),
                request.getURI()
        ) + String.format( // Include attributes only if one or more exist.
                request.getAttributes().isEmpty() ? "" : " %s",
                request.getAttributes()
        ) + String.format( // Include body only for POST request.
                request.getMethod().equals(HttpMethod.POST) ? " BODY %s" : "",
                new String(body)
        ));

        ClientHttpResponse response = execution.execute(request, body);
        logger.info(String.format(
                "RESPONSE %s.",
                response.getStatusCode()
        ));
        return response;
    }
}
