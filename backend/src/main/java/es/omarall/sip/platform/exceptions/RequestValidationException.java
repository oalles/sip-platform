package es.omarall.sip.platform.exceptions;

import es.omarall.sip.platform.dto.FieldErrorResource;
import lombok.Data;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RequestValidationException extends RuntimeException {

    private String code;
    private List<FieldErrorResource> details;

    public RequestValidationException(String code, String message, List<FieldErrorResource> fieldErrorResources) {
        super(message);
        this.code = code;
        this.details = fieldErrorResources;
    }

    public RequestValidationException(String code, String message) {
        this(code, message, List.of());
    }

    public RequestValidationException(String code, String message, Errors errors) {
        this(code, message);
        this.setDetails(errors.getFieldErrors().stream().map(fieldError ->
                FieldErrorResource.builder()
                        .code(fieldError.getCode())
                        .target(fieldError.getField())
                        .message(fieldError.getDefaultMessage()).build()).collect(Collectors.toList()));
    }

    /**
     * Just one invalid fields
     */
    public RequestValidationException(String code, String message, String field) {
        this(code, message, Collections.singletonList(FieldErrorResource.builder()
                .code(code)
                .message(message)
                .target(field)
                .build()));
    }
}
