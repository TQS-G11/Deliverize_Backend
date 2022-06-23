package tqs.g11.deliverize.enums;

public enum RiderStatus {
    FREE("FREE"),
    FETCHING("FETCHING"),
    DELIVERING("DELIVERING"),
    NOT_RIDER("NOT_RIDER");

    private final String status;

    RiderStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
