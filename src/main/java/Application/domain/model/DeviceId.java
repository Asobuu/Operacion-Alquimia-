package Application.domain.model;

import Application.domain.exceptions.UnknownDeviceException;

// los 3 dispositivos de vigilancia son fijos y conocidos de antemano (ver
// el enunciado: "el sistema recibira informacion de tres dispositivos de
// vigilancia ubicados en diferentes puntos de Central City"), cada uno con
// su posicion fija ya conocida. el cliente de la API nunca envia la
// posicion de un dispositivo, solo su identificador, la distancia que
// detecto y el fragmento de mensaje que intercepto - por eso la posicion
// vive aca, cableada, y no en el request
public enum DeviceId {

    STAR_LABS("STAR Labs", -500, -200),
    CCPD("CCPD", 100, -100),
    STAR_SATELLITE("S.T.A.R. Satellite", 500, 100);

    private final String label;
    private final Position position;

    DeviceId(String label, double x, double y) {
        this.label = label;
        this.position = new Position(x, y);
    }

    public String getLabel() {
        return label;
    }

    public Position getPosition() {
        return position;
    }

    // busca el dispositivo por su nombre tal como aparece en el enunciado
    // ("STAR Labs", "CCPD", "S.T.A.R. Satellite"), sin importar
    // mayusculas/minusculas ni espacios de mas al principio/final. si no
    // coincide con ninguno de los 3 dispositivos reales, es un dispositivo
    // desconocido (ver seccion 3 del enunciado: "manejar adecuadamente los
    // errores asociados a ... dispositivos desconocidos")
    public static DeviceId fromLabel(String rawLabel) {
        if (rawLabel == null || rawLabel.isBlank()) {
            throw new UnknownDeviceException("no se indico el identificador del dispositivo");
        }
        String normalized = rawLabel.trim();
        for (DeviceId deviceId : values()) {
            if (deviceId.label.equalsIgnoreCase(normalized) || deviceId.name().equalsIgnoreCase(normalized)) {
                return deviceId;
            }
        }
        throw new UnknownDeviceException("el dispositivo '" + rawLabel + "' no es un dispositivo de vigilancia conocido");
    }
}
