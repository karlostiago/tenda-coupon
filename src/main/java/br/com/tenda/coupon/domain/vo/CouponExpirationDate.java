package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponExpirationDate {

    private final LocalDateTime value;

    public CouponExpirationDate(LocalDateTime value) {
        if (value == null) {
            throw new InvalidCouponException("Expiration date is required");
        }

        if (value.isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Expiration date cannot be in the past");
        }
        this.value = value;
    }
}
