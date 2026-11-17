package Application.domain.services;

import Application.domain.exceptions.DeviceInfoNotFoundException;
import Application.domain.exceptions.InsufficientDataException;
import Application.domain.exceptions.InvalidDeviceDataException;
import Application.domain.model.DeviceId;
import Application.domain.model.DeviceInfo;
import Application.domain.model.DeviceReport;
import Application.domain.model.Position;
import Application.domain.model.TrackingResult;
import Application.domain.ports.in.AlchemyTrackingUseCase;
import Application.domain.ports.out.DeviceInfoRepositoryPort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// servicio de dominio que orquesta los 6 casos de uso (requerimientos
// 2.1 a 2.6). no depende de Spring ni de HTTP: solo del puerto de salida
// (persistencia) y de los dos servicios de calculo (trilateracion y
// reconstruccion del mensaje).
public class AlchemyTrackingService implements AlchemyTrackingUseCase {

    private final DeviceInfoRepositoryPort repository;
    private final TrilaterationService trilaterationService;
    private final MessageReconstructionService messageReconstructionService;

    public AlchemyTrackingService(DeviceInfoRepositoryPort repository,
                                   TrilaterationService trilaterationService,
                                   MessageReconstructionService messageReconstructionService) {
        this.repository = repository;
        this.trilaterationService = trilaterationService;
        this.messageReconstructionService = messageReconstructionService;
    }

    @Override
    public TrackingResult processAll(List<DeviceReport> reports) {
        int expected = DeviceId.values().length;
        if (reports == null || reports.size() != expected) {
            throw new InvalidDeviceDataException(
                    "se deben reportar los " + expected + " dispositivos de vigilancia al mismo tiempo");
        }

        Set<DeviceId> seen = new HashSet<>();
        for (DeviceReport report : reports) {
            if (report == null || report.getDeviceId() == null) {
                throw new InvalidDeviceDataException("cada reporte debe indicar un dispositivo de vigilancia valido");
            }
            if (!seen.add(report.getDeviceId())) {
                throw new InvalidDeviceDataException(
                        "el dispositivo '" + report.getDeviceId().getLabel() + "' viene repetido en el reporte");
            }
        }
        for (DeviceId deviceId : DeviceId.values()) {
            if (!seen.contains(deviceId)) {
                throw new InvalidDeviceDataException(
                        "falta el reporte del dispositivo '" + deviceId.getLabel() + "'");
            }
        }

        for (DeviceReport report : reports) {
            upsert(report.getDeviceId(), report.getDistance(), report.getMessageFragment());
        }

        return computeResult();
    }

    @Override
    public DeviceInfo registerDeviceInfo(DeviceId deviceId, double distance, String messageFragment) {
        requireDeviceId(deviceId);
        return upsert(deviceId, distance, messageFragment);
    }

    @Override
    public TrackingResult getAccumulatedResult() {
        return computeResult();
    }

    @Override
    public List<DeviceInfo> listRegisteredDeviceInfo() {
        List<DeviceInfo> result = new ArrayList<>();
        for (DeviceId deviceId : DeviceId.values()) {
            repository.findById(deviceId).ifPresent(result::add);
        }
        return result;
    }

    @Override
    public DeviceInfo replaceDeviceInfo(DeviceId deviceId, double distance, String messageFragment) {
        requireDeviceId(deviceId);
        DeviceInfo existing = findExistingOrThrow(deviceId);
        existing.replace(distance, messageFragment);
        repository.save(existing);
        return existing;
    }

    @Override
    public DeviceInfo updatePartialDeviceInfo(DeviceId deviceId, Double distance, String messageFragment) {
        requireDeviceId(deviceId);
        DeviceInfo existing = findExistingOrThrow(deviceId);
        existing.updatePartial(distance, messageFragment);
        repository.save(existing);
        return existing;
    }

    @Override
    public void deleteDeviceInfo(DeviceId deviceId) {
        requireDeviceId(deviceId);
        if (!repository.existsById(deviceId)) {
            throw new DeviceInfoNotFoundException(
                    "el dispositivo '" + deviceId.getLabel() + "' todavia no tiene informacion registrada");
        }
        repository.deleteById(deviceId);
    }

    private DeviceInfo upsert(DeviceId deviceId, double distance, String messageFragment) {
        DeviceInfo deviceInfo = new DeviceInfo(deviceId, distance, messageFragment);
        repository.save(deviceInfo);
        return deviceInfo;
    }

    private DeviceInfo findExistingOrThrow(DeviceId deviceId) {
        return repository.findById(deviceId)
                .orElseThrow(() -> new DeviceInfoNotFoundException(
                        "el dispositivo '" + deviceId.getLabel() + "' todavia no tiene informacion registrada"));
    }

    private TrackingResult computeResult() {
        int expected = DeviceId.values().length;
        Collection<DeviceInfo> all = repository.findAll();
        if (all.size() < expected) {
            throw new InsufficientDataException(
                    "no hay informacion suficiente: se necesitan los " + expected
                            + " dispositivos de vigilancia registrados para calcular el resultado");
        }

        List<DeviceInfo> ordered = new ArrayList<>();
        for (DeviceId deviceId : DeviceId.values()) {
            ordered.add(repository.findById(deviceId)
                    .orElseThrow(() -> new InsufficientDataException(
                            "no hay informacion suficiente: falta el dispositivo '" + deviceId.getLabel() + "'")));
        }

        Position labLocation = trilaterationService.locate(ordered);
        String reconstructedMessage = messageReconstructionService.reconstruct(ordered);
        return new TrackingResult(labLocation, reconstructedMessage);
    }

    private static void requireDeviceId(DeviceId deviceId) {
        if (deviceId == null) {
            throw new InvalidDeviceDataException("se debe indicar un dispositivo de vigilancia valido");
        }
    }
}
