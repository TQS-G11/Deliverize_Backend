package tqs.g11.deliverize.enums;

public enum CompanyStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    BLACKLISTED("BLACKLISTED"),
    NOT_COMPANY("NOT_COMPANY");

    private final String status;

    CompanyStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }

    public boolean isApproved() {
        return this.equals(CompanyStatus.APPROVED);
    }
}
