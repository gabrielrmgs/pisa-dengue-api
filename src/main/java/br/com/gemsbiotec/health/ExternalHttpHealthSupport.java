package br.com.gemsbiotec.health;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

abstract class ExternalHttpHealthSupport {

    private static final Duration TIMEOUT = Duration.ofSeconds(2);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    protected HealthCheckResponse check(String name, String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            boolean up = response.statusCode() >= 200 && response.statusCode() < 400;

            HealthCheckResponseBuilder builder = HealthCheckResponse.named(name)
                    .withData("statusCode", response.statusCode());

            return (up ? builder.up() : builder.down()).build();
        } catch (Exception e) {
            return HealthCheckResponse.named(name)
                    .down()
                    .withData("error", "Falha ao consultar dependencia externa")
                    .build();
        }
    }

    protected String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
