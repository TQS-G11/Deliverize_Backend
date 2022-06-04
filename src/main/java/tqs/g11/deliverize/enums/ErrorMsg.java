package tqs.g11.deliverize.enums;

public enum ErrorMsg {
    COMPANY_ID_NOT_FOUND("Company with provided ID not found."),
    RIDER_ID_NOT_FOUND("Rider with provided ID not found."),
    COMPANY_NOT_APPROVED("Company not approved."),
    BUYER_NULL_OR_BLANK("Buyer cannot be null or blank."),
    DESTINATION_NULL_OR_BLANK("Destination cannot be null or blank."),
    ORIGIN_NULL_OR_BLANK("Origin cannot be null or blank."),
    RIDER_NOT_FREE("Rider is not free."),
    ORDER_ID_NOT_FOUND("Order with the provided ID does not exist."),
    ORDER_ALREADY_ACCEPTED("Order has already been accepted by another rider."),
    RIDER_NO_CURRENT_ORDER("Rider does not have a current order."),
    DRIVER_ALREADY_RATED_FOR_DELIVERY("Driver has already been rated for this delivery."),
    INVALID_COMPANY_STATUS("Invalid company status."),
    INVALID_RATING("Invalid rating (must be between 0 and 5).");

    private final String text;

    ErrorMsg(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
