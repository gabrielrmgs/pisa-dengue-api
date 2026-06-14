package br.com.gemsbiotec.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    private final EntityManager entityManager;

    public DatabaseHealthCheck(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public HealthCheckResponse call() {
        try {
            Object result = entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return HealthCheckResponse.named("database")
                    .up()
                    .withData("result", String.valueOf(result))
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("database")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}
