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

    private Coupon(UUID id, CouponCode code, String description, BigDecimal discount, LocalDateTime expirationDate, boolean published, boolean redeemed, CouponStatus status) {
        this.id = id;
        this.code = code;
        this.description = new CouponDescription(description);
        this.discount = new CouponDiscount(discount);
        this.expirationDate = new CouponExpirationDate(expirationDate);
        this.published = published;
        this.redeemed = redeemed;
        this.status = status.name();
    }

    public static Coupon create(String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published, boolean redeemed) {
        return new Coupon(UUID.randomUUID(), CouponCode.from(code), description, discountValue, expirationDate, published, redeemed, CouponStatus.ACTIVE);
    }

    public static Coupon reconstruct(UUID id, String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published, boolean redeemed, CouponStatus status) {
        return new Coupon(id, CouponCode.reconstruct(code), description, discountValue, expirationDate, published, redeemed, status);
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


