package Application.domain.ports.in;

import Application.domain.model.DeviceId;
import Application.domain.model.DeviceInfo;
import Application.domain.model.DeviceReport;
import Application.domain.model.TrackingResult;

import java.util.List;

// puerto de entrada: los 6 casos de uso del enunciado (2.1 a 2.6).
// los adaptadores (REST, tests, etc.) solo conocen esta interfaz, nunca
// la implementacion concreta
public interface AlchemyTrackingUseCase {

    // 2.1 procesar los 3 reportes a la vez y devolver ubicacion + mensaje
    TrackingResult processAll(List<DeviceReport> reports);

    // 2.2 registrar (o reemplazar, si ya existia) la info de un dispositivo
    DeviceInfo registerDeviceInfo(DeviceId deviceId, double distance, String messageFragment);

    // 2.3 obtener el resultado acumulado con lo que se ha registrado hasta ahora
    TrackingResult getAccumulatedResult();

    // 2.4 reemplazar por completo la info de un dispositivo que ya existia
    DeviceInfo replaceDeviceInfo(DeviceId deviceId, double distance, String messageFragment);

    // 2.5 actualizar parcialmente (distancia y/o fragmento) un dispositivo que ya existia
    DeviceInfo updatePartialDeviceInfo(DeviceId deviceId, Double distance, String messageFragment);

    // 2.6 eliminar la info registrada de un dispositivo
    void deleteDeviceInfo(DeviceId deviceId);
}
