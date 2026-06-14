package br.com.gemsbiotec.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.context.ApplicationScoped;

@Readiness
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
