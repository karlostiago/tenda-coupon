package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.CouponStatusException;

public enum CouponStatus {
    ACTIVE,
    INACTIVE,
    DELETED;

    public static CouponStatus from(String status) {
        for (CouponStatus cs : values()) {
            if (cs.name().equalsIgnoreCase(status)) {
                return cs;
            }
        }
        throw new CouponStatusException("Invalid coupon status: " + status);
    }
}
