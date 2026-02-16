package br.com.tenda.coupon.application.usecase;

import br.com.tenda.coupon.domain.exception.CouponNotFoundException;
import br.com.tenda.coupon.domain.model.Coupon;
import br.com.tenda.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCouponByIdUseCase {

    private final CouponRepository couponRepository;

    @Transactional
    public Coupon execute(UUID couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + couponId));
    }
}

