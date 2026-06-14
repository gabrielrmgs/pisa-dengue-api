package br.com.gemsbiotec.web.error;

import org.jboss.logging.Logger;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(UnhandledExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("Erro nao tratado na API", exception);

        ApiErrorResponse body = ApiErrorResponse.of(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Internal Server Error",
                "Erro interno inesperado.",
                path());

        return Response.serverError()
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    private String path() {
        return uriInfo != null ? uriInfo.getPath() : null;
    }
}
