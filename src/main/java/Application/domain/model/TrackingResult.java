package Application.domain.model;

import lombok.Getter;

// el resultado combinado que pide el enunciado: donde esta el laboratorio
// de Alchemy y cual era el mensaje original completo (ver requerimientos
// 2.1 y 2.3: "retornar la ubicacion calculada y el mensaje reconstruido")
@Getter
public class TrackingResult {

    private final Position labLocation;
    private final String reconstructedMessage;

    public TrackingResult(Position labLocation, String reconstructedMessage) {
        this.labLocation = labLocation;
        this.reconstructedMessage = reconstructedMessage;
    }
}
