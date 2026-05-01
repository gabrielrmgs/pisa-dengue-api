package br.com.gemsbiotec.auth;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class TenantFilter implements ContainerRequestFilter {

    private JsonWebToken jwt;
    private TenantContext tenantContext;

    @Inject
    public TenantFilter(JsonWebToken jwt, TenantContext tenantContext) {
        this.jwt = jwt;
        this.tenantContext = tenantContext;
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {

        if (jwt == null || jwt.getSubject() == null) {
            System.out.println("JWT VAZIOOOO");
            return;
        }

        Long usuarioId = Long.valueOf(jwt.getSubject());
        Long municipioId = Long.valueOf(jwt.getClaim("municipio_id"));

        tenantContext.setUsuarioId(usuarioId);
        tenantContext.setMunicipioId(municipioId);

    }
}
