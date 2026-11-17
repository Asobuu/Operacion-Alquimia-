package Application.domain.exceptions;

// se pidio reemplazar, actualizar parcialmente o eliminar la informacion de
// un dispositivo (requerimientos 2.4, 2.5 y 2.6), pero ese dispositivo
// todavia no tiene ninguna informacion registrada
public class DeviceInfoNotFoundException extends RuntimeException {
    public DeviceInfoNotFoundException(String message) {
        super(message);
    }
}
