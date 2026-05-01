package br.com.gemsbiotec.auth;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class TenantContext {

    private Long usuarioId;
    private Long municipioId;

    public Long getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getMunicipioId() {
        return this.municipioId;
    }

    public void setMunicipioId(Long municipioId) {
        this.municipioId = municipioId;
    }

}
