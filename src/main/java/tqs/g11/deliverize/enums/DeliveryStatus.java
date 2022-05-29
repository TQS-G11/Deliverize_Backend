package tqs.g11.deliverize.enums;

public enum DeliveryStatus {
    REQUESTED("Requested"),
    IN_PROGRESS("In progress"),
    DELIVERED("Delivered");

    private final String status;

    DeliveryStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
