package Application.domain.services;

import Application.domain.model.DeviceInfo;
import Application.domain.model.Position;

import java.util.List;

// calcula la ubicacion del laboratorio de Alchemy a partir de las 3
// distancias reportadas por los dispositivos (cuyas posiciones son fijas
// y conocidas - ver DeviceId). es trilateracion 2D clasica: reduce el
// sistema de 2 circunferencias no lineales a una ecuacion lineal en x,y
// restando la ecuacion del circulo 1 a las de los circulos 2 y 3.
public class TrilaterationService {

    public Position locate(List<DeviceInfo> deviceInfos) {
        if (deviceInfos == null || deviceInfos.size() != 3) {
            throw new IllegalArgumentException("se necesitan exactamente 3 mediciones para triangular la posicion");
        }

        Position p1 = deviceInfos.get(0).getDeviceId().getPosition();
        Position p2 = deviceInfos.get(1).getDeviceId().getPosition();
        Position p3 = deviceInfos.get(2).getDeviceId().getPosition();

        double d1 = deviceInfos.get(0).getDistance();
        double d2 = deviceInfos.get(1).getDistance();
        double d3 = deviceInfos.get(2).getDistance();

        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();

        double a = 2 * (x2 - x1);
        double b = 2 * (y2 - y1);
        double c = (d1 * d1) - (d2 * d2) + (x2 * x2) - (x1 * x1) + (y2 * y2) - (y1 * y1);

        double d = 2 * (x3 - x1);
        double e = 2 * (y3 - y1);
        double f = (d1 * d1) - (d3 * d3) + (x3 * x3) - (x1 * x1) + (y3 * y3) - (y1 * y1);

        double denominator = (a * e) - (b * d);
        if (denominator == 0) {
            // no puede pasar con las posiciones fijas reales de los 3 dispositivos,
            // pero se deja la validacion por seguridad si algun dia cambian
            throw new IllegalStateException(
                    "los dispositivos de vigilancia estan alineados, no se puede calcular una ubicacion unica");
        }

        double x = ((c * e) - (f * b)) / denominator;
        double y = ((a * f) - (d * c)) / denominator;

        return new Position(x, y);
    }
}
