package br.com.gemsbiotec.web.error;

import java.util.Comparator;
import java.util.List;

import br.com.gemsbiotec.web.error.ApiErrorResponse.ApiErrorDetail;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ApiErrorDetail> details = exception.getConstraintViolations().stream()
                .map(violation -> new ApiErrorDetail(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .sorted(Comparator.comparing(ApiErrorDetail::field))
                .toList();

        ApiErrorResponse body = ApiErrorResponse.of(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Bad Request",
                "Payload invalido.",
                path(),
                details);

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    private String path() {
        return uriInfo != null ? uriInfo.getPath() : null;
    }
}
