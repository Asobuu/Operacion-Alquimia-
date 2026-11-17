package Application.adapters.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// cuerpo para registrar (2.2) o reemplazar por completo (2.4) la info de
// un dispositivo: ambos casos exigen los dos datos completos
public class DeviceDataRequest {

    @NotNull(message = "la distancia es obligatoria")
    @PositiveOrZero(message = "la distancia no puede ser negativa")
    private Double distance;

    @NotNull(message = "el fragmento del mensaje es obligatorio")
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
