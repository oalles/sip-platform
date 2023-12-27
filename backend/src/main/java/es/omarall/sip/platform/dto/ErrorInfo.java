package es.omarall.sip.platform.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * https://github.com/microsoft/api-guidelines/blob/vNext/Guidelines.md#7102-error-condition-responses
 */
@Builder
@Data
public class ErrorInfo implements Serializable {
    /**
     * Path of the request that ended in an error
     */
    private String path;

    /**
     * The value for the "code" name/value pair is a language-independent string. Its value is a service-defined error code that SHOULD be human-readable. This code serves as a more specific indicator of the error than the HTTP error code specified in the response. Services SHOULD have a relatively small number (about 20) of possible values for "code," and all clients MUST be capable of handling all of them.
     */
    private String code;
    /**
     * The value for the "message" name/value pair MUST be a human-readable representation of the error. It is intended as an aid to developers and is not suitable for exposure to end users.
     */
    private String message;

    /**
     * The value for the "details" name/value pair MUST be an array of JSON objects that MUST contain name/value pairs for "code" and "message," and MAY contain a name/value pair for "target," as described above. The objects in the "details" array usually represent distinct, related errors that occurred during the request.
     */
    private List<FieldErrorResource> details;
}
