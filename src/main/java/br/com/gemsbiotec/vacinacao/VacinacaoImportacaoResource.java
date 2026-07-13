package br.com.gemsbiotec.vacinacao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import br.com.gemsbiotec.vacinacao.dto.ImportarVacinacaoResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/vacinacao/importacoes")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Vacinacao", description = "Importacao de snapshots do SI-PNI")
public class VacinacaoImportacaoResource {

    private final VacinacaoService service;

    public VacinacaoImportacaoResource(VacinacaoService service) {
        this.service = service;
    }

    @POST
    @RolesAllowed("ADMIN")
    @Operation(summary = "Importa um novo snapshot de doses aplicadas (CSV: unidade_saude,doses_10_14,doses_18_59)")
    public ImportarVacinacaoResponse importar(@BeanParam ImportarVacinacaoForm form) throws IOException {
        if (form.arquivo == null) {
            throw new BadRequestException("Arquivo CSV e obrigatorio.");
        }
        if (form.dataReferencia == null || form.dataReferencia.isBlank()) {
            throw new BadRequestException("dataReferencia e obrigatoria (formato yyyy-MM-dd).");
        }
        LocalDate dataReferencia;
        try {
            dataReferencia = LocalDate.parse(form.dataReferencia.trim());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("dataReferencia invalida, use o formato yyyy-MM-dd.");
        }
        try (InputStream inputStream = Files.newInputStream(form.arquivo.uploadedFile())) {
            return service.importar(inputStream, dataReferencia);
        }
    }
}
