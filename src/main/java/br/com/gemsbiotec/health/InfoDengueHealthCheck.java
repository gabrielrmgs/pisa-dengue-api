package br.com.gemsbiotec.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import io.smallrye.health.api.HealthGroup;

import jakarta.enterprise.context.ApplicationScoped;

@HealthGroup("external")
@ApplicationScoped
public class InfoDengueHealthCheck extends ExternalHttpHealthSupport implements HealthCheck {

    private final String baseUrl;

    public InfoDengueHealthCheck(@ConfigProperty(name = "quarkus.rest-client.infodengue.url") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public HealthCheckResponse call() {
        String url = trimTrailingSlash(baseUrl)
                + "/api/alertcity?geocode=2201903&disease=dengue&format=json"
                + "&ew_start=1&ew_end=1&ey_start=2024&ey_end=2024";
        return check("infodengue", url);
    }
}
