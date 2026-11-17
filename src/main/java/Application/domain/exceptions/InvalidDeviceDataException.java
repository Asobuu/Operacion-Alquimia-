package Application.domain.exceptions;

// la informacion recibida de un dispositivo no es valida (distancia
// negativa o no numerica, fragmento de mensaje ausente, lista de
// dispositivos incompleta o con duplicados en el procesamiento simultaneo, etc.)
public class InvalidDeviceDataException extends RuntimeException {
    public InvalidDeviceDataException(String message) {
        super(message);
    }
}
