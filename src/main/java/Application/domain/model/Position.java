package Application.domain.model;

import lombok.Getter;

// un punto (x,y) en el plano de Central City. se usa tanto para la posicion
// fija de cada dispositivo de vigilancia como para el resultado de la
// trilateracion (la posicion calculada del laboratorio de Alchemy)
@Getter
public class Position {

    private final double x;
    private final double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
