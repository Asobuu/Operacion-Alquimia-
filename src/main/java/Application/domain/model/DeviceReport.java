package Application.domain.model;

import lombok.Getter;

// objeto de entrada para el procesamiento simultaneo (requerimiento 2.1):
// lo que un dispositivo reporta en una sola llamada. no es un DTO de HTTP,
// es un objeto de dominio simple que usan los puertos de entrada
@Getter
public class DeviceReport {

    private final DeviceId deviceId;
    private final double distance;
    private final String messageFragment;

    public DeviceReport(DeviceId deviceId, double distance, String messageFragment) {
        this.deviceId = deviceId;
        this.distance = distance;
        this.messageFragment = messageFragment;
    }
}
