package br.com.tenda.coupon.application.usecase;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import br.com.tenda.coupon.domain.model.Coupon;
import br.com.tenda.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateCouponUseCase {

    private final CouponRepository couponRepository;

    @Transactional
    public Coupon execute(String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published) {

        if (couponRepository.existsByCode(code.replaceAll("[^a-zA-Z0-9]", "").toUpperCase())) {
            throw new InvalidCouponException("A coupon with this code already exists");
        }

        Coupon coupon = Coupon.create(code, description, discountValue, expirationDate, published, false);
        return couponRepository.save(coupon);
    }
}

