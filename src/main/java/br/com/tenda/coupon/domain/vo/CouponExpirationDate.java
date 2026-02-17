package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.ExpirationDateException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
public class CouponExpirationDate {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private final LocalDateTime value;

    public CouponExpirationDate(LocalDateTime value) {
        this.value = value;
    }

    public static CouponExpirationDate from(LocalDateTime value) {
        if (value == null) {
            throw new ExpirationDateException("Expiration date is required");
        }

        LocalDateTime now = LocalDateTime.now(ZONE_ID);

        if (value.isBefore(now)) {
            throw new ExpirationDateException("Expiration date cannot be in the past");
        }

        return new CouponExpirationDate(value);
    }

    public static CouponExpirationDate reconstruct(LocalDateTime value) {
        return new CouponExpirationDate(value);
    }
}
