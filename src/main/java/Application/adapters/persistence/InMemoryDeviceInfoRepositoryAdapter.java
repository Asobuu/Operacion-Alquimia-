package Application.adapters.persistence;

import Application.domain.model.DeviceId;
import Application.domain.model.DeviceInfo;
import Application.domain.ports.out.DeviceInfoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// adaptador de persistencia en memoria (el enunciado pide explicitamente
// que NO haya base de datos: "la informacion debe mantenerse temporalmente
// en memoria"). un ConcurrentHashMap alcanza porque solo hay 3 llaves
// posibles (los 3 DeviceId) y puede haber llamadas concurrentes.
@Component
public class InMemoryDeviceInfoRepositoryAdapter implements DeviceInfoRepositoryPort {

    private final Map<DeviceId, DeviceInfo> storage = new ConcurrentHashMap<>();

    @Override
    public void save(DeviceInfo deviceInfo) {
        storage.put(deviceInfo.getDeviceId(), deviceInfo);
    }

    @Override
    public Optional<DeviceInfo> findById(DeviceId deviceId) {
        return Optional.ofNullable(storage.get(deviceId));
    }

    @Override
    public Collection<DeviceInfo> findAll() {
        return storage.values();
    }

    @Override
    public boolean existsById(DeviceId deviceId) {
        return storage.containsKey(deviceId);
    }

    @Override
    public void deleteById(DeviceId deviceId) {
        storage.remove(deviceId);
    }
}
