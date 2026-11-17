package Application.adapters.rest.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

// cuerpo para actualizar parcialmente (2.5) la info de un dispositivo:
// los dos campos son opcionales (null = "no cambies esto"), pero debe
// venir al menos uno (esa regla la valida el dominio, en DeviceInfo)
public class PartialDeviceDataRequest {

    @PositiveOrZero(message = "la distancia no puede ser negativa")
    private Double distance;

    private String messageFragment;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getMessageFragment() {
        return messageFragment;
    }

    public void setMessageFragment(String messageFragment) {
        this.messageFragment = messageFragment;
    }
}
