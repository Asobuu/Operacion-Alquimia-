package Application.adapters.rest.dto.response;

import Application.domain.model.DeviceInfo;

public class DeviceInfoResponse {

    private String deviceId;
    private String deviceLabel;
    private double distance;
    private String messageFragment;

    public DeviceInfoResponse(String deviceId, String deviceLabel, double distance, String messageFragment) {
        this.deviceId = deviceId;
        this.deviceLabel = deviceLabel;
        this.distance = distance;
        this.messageFragment = messageFragment;
    }

    public static DeviceInfoResponse from(DeviceInfo deviceInfo) {
        return new DeviceInfoResponse(
                deviceInfo.getDeviceId().name(),
                deviceInfo.getDeviceId().getLabel(),
                deviceInfo.getDistance(),
                deviceInfo.getMessageFragment());
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public double getDistance() {
        return distance;
    }

    public String getMessageFragment() {
        return messageFragment;
    }
}
