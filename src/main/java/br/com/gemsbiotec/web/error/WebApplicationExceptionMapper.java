package br.com.gemsbiotec.web.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response original = exception.getResponse();
        int status = original != null ? original.getStatus() : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        Response.Status statusInfo = Response.Status.fromStatusCode(status);
        String reason = statusInfo != null ? statusInfo.getReasonPhrase() : "HTTP Error";

        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = reason;
        }

        ApiErrorResponse body = ApiErrorResponse.of(status, reason, message, path());

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    private String path() {
        return uriInfo != null ? uriInfo.getPath() : null;
    }
}
