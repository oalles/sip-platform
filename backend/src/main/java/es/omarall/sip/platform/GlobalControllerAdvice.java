package es.omarall.sip.platform;

import es.omarall.sip.platform.dto.ErrorInfo;
import es.omarall.sip.platform.exceptions.RequestValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RequestValidationException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> handleRequestValidationException(HttpServletRequest req, RequestValidationException ex) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .path(req.getRequestURL().toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfo);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> handleNoSuchElementException(HttpServletRequest req, NoSuchElementException e) {
        ErrorInfo errorInfo = ErrorInfo.builder()
                .code("NoSuchElement")
                .message("Requested resource does not exist")
                .path(req.getRequestURL().toString())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorInfo);
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorInfo> handleThrowable(HttpServletRequest req, Throwable t) {
        log.error("API temporarily unavailable due to an unexpected error", t);
        ErrorInfo error = ErrorInfo.builder()
                .code("ServerUnavailable")
                .path(req.getRequestURL().toString())
                .message("API temporarily unavailable due to an unexpected error")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
