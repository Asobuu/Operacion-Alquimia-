package Application.adapters.rest;

import Application.adapters.rest.dto.request.DeviceDataRequest;
import Application.adapters.rest.dto.request.DeviceReportRequest;
import Application.adapters.rest.dto.request.PartialDeviceDataRequest;
import Application.adapters.rest.dto.request.ProcessAllRequest;
import Application.adapters.rest.dto.response.DeviceInfoResponse;
import Application.adapters.rest.dto.response.TrackingResultResponse;
import Application.domain.model.DeviceId;
import Application.domain.model.DeviceInfo;
import Application.domain.model.DeviceReport;
import Application.domain.model.TrackingResult;
import Application.domain.ports.in.AlchemyTrackingUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

// adaptador REST: traduce HTTP <-> casos de uso. toda la logica de negocio
// vive en AlchemyTrackingService (dominio); este controlador solo arma los
// DTOs de entrada/salida y traduce los identificadores de dispositivo.
//
// el path variable {deviceId} acepta el nombre del enum (sin espacios ni
// puntos, para no tener que codificar la URL): STAR_LABS, CCPD o
// STAR_SATELLITE. tambien acepta la etiqueta exacta (ej. "CCPD") si se
// prefiere, gracias a DeviceId.fromLabel.
@RestController
@RequestMapping("/api/devices")
public class AlchemyTrackingController {

    private final AlchemyTrackingUseCase alchemyTrackingUseCase;

    public AlchemyTrackingController(AlchemyTrackingUseCase alchemyTrackingUseCase) {
        this.alchemyTrackingUseCase = alchemyTrackingUseCase;
    }

    // 2.1 - procesar los 3 reportes a la vez
    @PostMapping("/process")
    public ResponseEntity<TrackingResultResponse> processAll(@Valid @RequestBody ProcessAllRequest request) {
        List<DeviceReport> reports = request.getReports().stream()
                .map(this::toDeviceReport)
                .collect(Collectors.toList());
        TrackingResult result = alchemyTrackingUseCase.processAll(reports);
        return ResponseEntity.ok(TrackingResultResponse.from(result));
    }

    // 2.2 - registrar (o refrescar) la info de un dispositivo individual
    @PostMapping("/{deviceId}")
    public ResponseEntity<DeviceInfoResponse> register(@PathVariable String deviceId,
                                                         @Valid @RequestBody DeviceDataRequest request) {
        DeviceInfo deviceInfo = alchemyTrackingUseCase.registerDeviceInfo(
                DeviceId.fromLabel(deviceId), request.getDistance(), request.getMessageFragment());
        return ResponseEntity.status(HttpStatus.CREATED).body(DeviceInfoResponse.from(deviceInfo));
    }

    // 2.3 - resultado acumulado con lo registrado hasta el momento
    @GetMapping("/result")
    public ResponseEntity<TrackingResultResponse> getAccumulatedResult() {
        TrackingResult result = alchemyTrackingUseCase.getAccumulatedResult();
        return ResponseEntity.ok(TrackingResultResponse.from(result));
    }

    // 2.3 (complementario) - consultar la informacion cruda recibida de cada
    // dispositivo hasta el momento, sin ejecutar la trilateracion/reconstruccion
    @GetMapping
    public ResponseEntity<List<DeviceInfoResponse>> listRegisteredDeviceInfo() {
        List<DeviceInfoResponse> response = alchemyTrackingUseCase.listRegisteredDeviceInfo().stream()
                .map(DeviceInfoResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 2.4 - reemplazar por completo la info de un dispositivo que ya existia
    @PutMapping("/{deviceId}")
    public ResponseEntity<DeviceInfoResponse> replace(@PathVariable String deviceId,
                                                        @Valid @RequestBody DeviceDataRequest request) {
        DeviceInfo deviceInfo = alchemyTrackingUseCase.replaceDeviceInfo(
                DeviceId.fromLabel(deviceId), request.getDistance(), request.getMessageFragment());
        return ResponseEntity.ok(DeviceInfoResponse.from(deviceInfo));
    }

    // 2.5 - actualizar parcialmente (distancia y/o fragmento) un dispositivo existente
    @PatchMapping("/{deviceId}")
    public ResponseEntity<DeviceInfoResponse> updatePartial(@PathVariable String deviceId,
                                                              @Valid @RequestBody PartialDeviceDataRequest request) {
        DeviceInfo deviceInfo = alchemyTrackingUseCase.updatePartialDeviceInfo(
                DeviceId.fromLabel(deviceId), request.getDistance(), request.getMessageFragment());
        return ResponseEntity.ok(DeviceInfoResponse.from(deviceInfo));
    }

    // 2.6 - eliminar la info registrada de un dispositivo
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> delete(@PathVariable String deviceId) {
        alchemyTrackingUseCase.deleteDeviceInfo(DeviceId.fromLabel(deviceId));
        return ResponseEntity.noContent().build();
    }

    private DeviceReport toDeviceReport(DeviceReportRequest request) {
        return new DeviceReport(
                DeviceId.fromLabel(request.getDeviceId()), request.getDistance(), request.getMessageFragment());
    }
}
