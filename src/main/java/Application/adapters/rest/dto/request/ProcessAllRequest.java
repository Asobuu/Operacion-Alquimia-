package Application.adapters.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// cuerpo del endpoint de procesamiento simultaneo (2.1): los reportes de
// los 3 dispositivos llegan juntos en una sola peticion
public class ProcessAllRequest {

    @NotNull(message = "se deben enviar los reportes de los dispositivos")
    @Size(min = 3, max = 3, message = "se deben enviar exactamente 3 reportes, uno por cada dispositivo de vigilancia")
    @Valid
    private List<DeviceReportRequest> reports;

    public List<DeviceReportRequest> getReports() {
        return reports;
    }

    public void setReports(List<DeviceReportRequest> reports) {
        this.reports = reports;
    }
}
