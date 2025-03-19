package dev.jbazann.skwidl.commons.rest;

import dev.jbazann.skwidl.commons.exceptions.CommonsInternalException;
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
    public @NonNull ClientHttpResponse intercept(HttpRequest request, @NonNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String threadName = Thread.currentThread().getName();
        logger.info(String.format(
                "%s: REQUEST %s %s",
                threadName,
                request.getMethod(),
                request.getURI()
        ) + String.format( // Include attributes only if one or more exist.
                request.getAttributes().isEmpty() ? "" : " %s",
                request.getAttributes()
        ) + String.format( // Include body only for POST request.
                request.getMethod().equals(HttpMethod.POST) ? " BODY %s" : "",
                new String(body)
        ));

        try (ClientHttpResponse response = execution.execute(request, body)) {
            logger.info(String.format(
                    "%s: RESPONSE %s",
                    threadName,
                    response.getStatusCode()
            ));
            return response;
        } catch (IOException e) {
            throw new CommonsInternalException("An error occurred while trying to execute the request.",e);
        }
    }
}
