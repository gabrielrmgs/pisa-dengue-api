package br.com.gemsbiotec.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import io.smallrye.health.api.HealthGroup;

import jakarta.enterprise.context.ApplicationScoped;

@HealthGroup("external")
@ApplicationScoped
public class IbgeHealthCheck extends ExternalHttpHealthSupport implements HealthCheck {

    private final String baseUrl;

    public IbgeHealthCheck(@ConfigProperty(name = "quarkus.rest-client.ibge-dados.url") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public HealthCheckResponse call() {
        String url = trimTrailingSlash(baseUrl) + "/api/v1/localidades/municipios/2201903";
        return check("ibge", url);
    }
}
