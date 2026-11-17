package Application.domain.exceptions;

// el identificador recibido no corresponde a ninguno de los 3 dispositivos
// de vigilancia reales (STAR Labs, CCPD, S.T.A.R. Satellite)
public class UnknownDeviceException extends RuntimeException {
    public UnknownDeviceException(String message) {
        super(message);
    }
}
