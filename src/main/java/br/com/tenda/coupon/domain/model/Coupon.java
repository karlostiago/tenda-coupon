package br.com.tenda.coupon.domain.model;

import br.com.tenda.coupon.domain.exception.CouponAlreadyDeletedException;
import br.com.tenda.coupon.domain.vo.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Coupon {

    private final UUID id;
    private final CouponCode code;
    private final CouponDescription description;
    private final CouponDiscount discount;
    private final CouponExpirationDate expirationDate;
    private final boolean published;
    private final boolean redeemed;
    private String status;

    private Coupon(UUID id, CouponCode code, CouponDescription description, CouponDiscount discount, CouponExpirationDate expirationDate, boolean published, boolean redeemed, CouponStatus status) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.expirationDate = expirationDate;
        this.published = published;
        this.redeemed = redeemed;
        this.status = status.name();
    }

    public static Coupon create(String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published, boolean redeemed) {
        return new Coupon(UUID.randomUUID(), CouponCode.from(code), CouponDescription.from(description), CouponDiscount.from(discountValue), CouponExpirationDate.from(expirationDate), published, redeemed, CouponStatus.ACTIVE);
    }

    public static Coupon reconstruct(UUID id, String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published, boolean redeemed, CouponStatus status) {
        return new Coupon(id, CouponCode.reconstruct(code), CouponDescription.reconstruct(description), CouponDiscount.reconstruct(discountValue), CouponExpirationDate.reconstruct(expirationDate), published, redeemed, status);
    }

    public void delete() {
        if (CouponStatus.DELETED.name().equals(this.status)) {
            throw new CouponAlreadyDeletedException("Coupon with id " + this.id + " is already deleted");
        }
        this.status = CouponStatus.DELETED.name();
    }

    public String getCodeValue() {
        return code.getValue();
    }
}


