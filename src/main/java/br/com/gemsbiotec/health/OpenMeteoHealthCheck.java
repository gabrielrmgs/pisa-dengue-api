package br.com.gemsbiotec.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import io.smallrye.health.api.HealthGroup;

import jakarta.enterprise.context.ApplicationScoped;

@HealthGroup("external")
@ApplicationScoped
public class OpenMeteoHealthCheck extends ExternalHttpHealthSupport implements HealthCheck {

    private final String baseUrl;

    public OpenMeteoHealthCheck(@ConfigProperty(name = "quarkus.rest-client.open-meteo-archive.url") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public HealthCheckResponse call() {
        String url = trimTrailingSlash(baseUrl)
                + "/v1/archive?latitude=-9.0713&longitude=-44.3591"
                + "&start_date=2024-01-01&end_date=2024-01-01"
                + "&hourly=temperature_2m&timezone=America%2FSao_Paulo";
        return check("open-meteo", url);
    }
}
