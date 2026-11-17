package Application.domain.exceptions;

// todavia no ha llegado la informacion de los 3 dispositivos, asi que no
// se puede calcular la trilateracion ni reconstruir el mensaje (ver
// requerimiento 2.3: "si la informacion disponible no permite realizar las
// operaciones, informar que no existen datos suficientes")
public class InsufficientDataException extends RuntimeException {
    public InsufficientDataException(String message) {
        super(message);
    }
}
