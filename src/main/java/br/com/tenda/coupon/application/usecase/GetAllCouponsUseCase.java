package br.com.tenda.coupon.application.usecase;

import br.com.tenda.coupon.domain.model.Coupon;
import br.com.tenda.coupon.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllCouponsUseCase {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public Page<Coupon> execute(Pageable pageable) {
        return couponRepository.findAll(pageable);
    }
}

