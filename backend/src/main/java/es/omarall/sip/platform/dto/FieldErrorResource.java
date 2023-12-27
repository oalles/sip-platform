package es.omarall.sip.platform.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Builder
@Data
public class FieldErrorResource implements Serializable {
    /**
     * Field related to the error
     */
    private String target;
    /**
     * A more specific error code than was provided by the containing error.
     */
    private String code;

    /**
     * A human-readable representation of the error.
     */
    private String message;
}
