package br.com.gemsbiotec.web.error;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<ApiErrorDetail> details) {

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(OffsetDateTime.now(), status, error, message, path, List.of());
    }

    public static ApiErrorResponse of(
            int status,
            String error,
            String message,
            String path,
            List<ApiErrorDetail> details) {
        return new ApiErrorResponse(OffsetDateTime.now(), status, error, message, path, details);
    }

    public record ApiErrorDetail(String field, String message) {
    }
}
