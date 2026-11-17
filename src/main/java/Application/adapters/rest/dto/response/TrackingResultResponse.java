package Application.adapters.rest.dto.response;

import Application.domain.model.TrackingResult;

public class TrackingResultResponse {

    private PositionResponse labLocation;
    private String reconstructedMessage;

    public TrackingResultResponse(PositionResponse labLocation, String reconstructedMessage) {
        this.labLocation = labLocation;
        this.reconstructedMessage = reconstructedMessage;
    }

    public static TrackingResultResponse from(TrackingResult trackingResult) {
        PositionResponse position = new PositionResponse(
                trackingResult.getLabLocation().getX(),
                trackingResult.getLabLocation().getY());
        return new TrackingResultResponse(position, trackingResult.getReconstructedMessage());
    }

    public PositionResponse getLabLocation() {
        return labLocation;
    }

    public String getReconstructedMessage() {
        return reconstructedMessage;
    }
}
