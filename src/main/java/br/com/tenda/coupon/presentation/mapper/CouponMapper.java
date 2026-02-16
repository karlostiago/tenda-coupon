package br.com.tenda.coupon.presentation.mapper;

import br.com.tenda.coupon.domain.model.Coupon;
import br.com.tenda.coupon.domain.vo.CouponStatus;
import br.com.tenda.coupon.presentation.dto.CouponResponse;

public class CouponMapper {

    private CouponMapper() { }

    public static CouponResponse toResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCodeValue())
                .description(coupon.getDescription().getValue())
                .discountValue(coupon.getDiscount().getValue())
                .expirationDate(coupon.getExpirationDate().getValue())
                .published(coupon.isPublished())
                .redeemed(coupon.isRedeemed())
                .status(CouponStatus.from(coupon.getStatus()))
                .build();
    }
}


