package Application.adapters.rest.dto.response;

public class PositionResponse {

    private double x;
    private double y;

    public PositionResponse(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
