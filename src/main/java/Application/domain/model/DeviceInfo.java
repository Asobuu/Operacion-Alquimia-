package Application.domain.model;

import Application.domain.exceptions.InvalidDeviceDataException;
import lombok.Getter;

// lo que un dispositivo de vigilancia reporto: la distancia que detecto
// hasta el laboratorio de Alchemy, y el fragmento del mensaje interceptado
// (el mensaje completo, pero con algunas palabras reemplazadas por "" por
// la interferencia de la Piedra Filosofal - ver seccion 1 y 3 del enunciado)
@Getter
public class DeviceInfo {

    private final DeviceId deviceId;
    private double distance;
    private String messageFragment;

    public DeviceInfo(DeviceId deviceId, double distance, String messageFragment) {
        this.deviceId = deviceId;
        validateDistance(distance);
        validateFragment(messageFragment);
        this.distance = distance;
        this.messageFragment = messageFragment;
    }

    // reemplazo completo (requerimiento 2.4): los dos datos cambian a la
    // vez, no queda nada del valor anterior
    public void replace(double newDistance, String newMessageFragment) {
        validateDistance(newDistance);
        validateFragment(newMessageFragment);
        this.distance = newDistance;
        this.messageFragment = newMessageFragment;
    }

    // actualizacion parcial (requerimiento 2.5): cada dato es opcional
    // (null = "no lo estoy cambiando, deja el que ya tenia"). al menos
    // uno de los dos debe venir, si no no hay nada que actualizar
    public void updatePartial(Double newDistance, String newMessageFragment) {
        if (newDistance == null && newMessageFragment == null) {
            throw new InvalidDeviceDataException(
                    "la actualizacion debe incluir al menos la distancia o el fragmento del mensaje");
        }
        if (newDistance != null) {
            validateDistance(newDistance);
            this.distance = newDistance;
        }
        if (newMessageFragment != null) {
            validateFragment(newMessageFragment);
            this.messageFragment = newMessageFragment;
        }
    }

    private static void validateDistance(double distance) {
        if (Double.isNaN(distance) || Double.isInfinite(distance)) {
            throw new InvalidDeviceDataException("la distancia reportada no es un numero valido");
        }
        if (distance < 0) {
            throw new InvalidDeviceDataException("la distancia reportada no puede ser negativa");
        }
    }

    private static void validateFragment(String messageFragment) {
        if (messageFragment == null) {
            throw new InvalidDeviceDataException("el fragmento del mensaje interceptado es obligatorio");
        }
    }
}
