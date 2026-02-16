package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import lombok.Getter;

@Getter
public class CouponCode {

    private static final int REQUIRED_LENGTH = 6;
    private final String value;

    private CouponCode(String value) {
        this.value = value;
    }

    public static CouponCode from(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new InvalidCouponException("Coupon code is required");
        }

        String cleanedCode = rawCode.replaceAll("[^a-zA-Z0-9]", "");

        if (cleanedCode.length() != REQUIRED_LENGTH) {
            throw new InvalidCouponException(
                String.format("Coupon code must have exactly %d alphanumeric characters (after removing special characters)",
                REQUIRED_LENGTH)
            );
        }

        return new CouponCode(cleanedCode.toUpperCase());
    }

    public static CouponCode reconstruct(String code) {
        return new CouponCode(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponCode that = (CouponCode) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}

