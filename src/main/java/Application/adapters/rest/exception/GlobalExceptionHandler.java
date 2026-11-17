package Application.adapters.rest.exception;

import Application.domain.exceptions.DeviceInfoNotFoundException;
import Application.domain.exceptions.InsufficientDataException;
import Application.domain.exceptions.InvalidDeviceDataException;
import Application.domain.exceptions.UnknownDeviceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// traduce las excepciones del dominio (que no saben nada de HTTP) a
// respuestas con el codigo de estado correcto - mismo patron que el
// GlobalExceptionHandler de NexusM
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnknownDeviceException.class)
    public ResponseEntity<ErrorResponse> handleUnknownDevice(UnknownDeviceException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DeviceInfoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeviceInfoNotFound(DeviceInfoNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidDeviceDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDeviceData(InvalidDeviceDataException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InsufficientDataException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientData(InsufficientDataException exception) {
        return build(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message.isBlank() ? "datos invalidos en la peticion" : message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "ocurrio un error inesperado: " + exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), message));
    }
}
