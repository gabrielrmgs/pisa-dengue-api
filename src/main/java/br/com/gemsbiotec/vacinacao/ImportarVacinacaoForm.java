package br.com.gemsbiotec.vacinacao;

import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class ImportarVacinacaoForm {

    @RestForm("arquivo")
    public FileUpload arquivo;

    @RestForm("dataReferencia")
    @PartType(MediaType.TEXT_PLAIN)
    public String dataReferencia;
}
