package Application.adapters.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// un reporte individual dentro del procesamiento simultaneo (2.1): aqui si
// se indica el dispositivo, porque van los 3 en una sola lista
public class DeviceReportRequest {

    @NotBlank(message = "se debe indicar el dispositivo de vigilancia")
    private String deviceId;

    @NotNull(message = "la distancia es obligatoria")
    @PositiveOrZero(message = "la distancia no puede ser negativa")
    private Double distance;

    @NotNull(message = "el fragmento del mensaje es obligatorio")
    private String messageFragment;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

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
