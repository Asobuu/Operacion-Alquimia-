package Application.domain.ports.out;

import Application.domain.model.DeviceId;
import Application.domain.model.DeviceInfo;

import java.util.Collection;
import java.util.Optional;

// puerto de salida: como el dominio guarda/consulta la info de los
// dispositivos, sin saber que por debajo es solo un mapa en memoria
// (el enunciado exige que no haya base de datos)
public interface DeviceInfoRepositoryPort {

    void save(DeviceInfo deviceInfo);

    Optional<DeviceInfo> findById(DeviceId deviceId);

    Collection<DeviceInfo> findAll();

    boolean existsById(DeviceId deviceId);

    void deleteById(DeviceId deviceId);
}
