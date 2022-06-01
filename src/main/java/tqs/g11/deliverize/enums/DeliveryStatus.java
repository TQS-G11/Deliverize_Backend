package tqs.g11.deliverize.enums;

public enum DeliveryStatus {
    REQUESTED("REQUESTED"),
    FETCHING("FETCHING"),
    DELIVERING("DELIVERING"),
    DELIVERED("DELIVERED");

    private final String status;

    DeliveryStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
