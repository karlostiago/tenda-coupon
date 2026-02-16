package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CouponDiscount {

    private final BigDecimal value;

    public CouponDiscount(BigDecimal value) {
        if (value == null) {
            throw new InvalidCouponException("Discount value is required");
        }

        if (value.compareTo(new BigDecimal("0.5")) < 0) {
            throw new InvalidCouponException("Discount value must be at least 0.5");
        }
        this.value = value;
    }
}
