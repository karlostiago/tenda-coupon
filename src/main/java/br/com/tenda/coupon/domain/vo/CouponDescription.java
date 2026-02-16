package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import lombok.Getter;

@Getter
public class CouponDescription {

    private final String value;

    public CouponDescription(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidCouponException("Description is required");
        }
        this.value = value;
    }
}
