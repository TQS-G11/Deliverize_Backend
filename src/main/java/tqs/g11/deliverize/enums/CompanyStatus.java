package tqs.g11.deliverize.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static boolean validStatus(String value) {
        List<String> statusesNames = Stream.of(CompanyStatus.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
        return statusesNames.contains(value);
    }
}
