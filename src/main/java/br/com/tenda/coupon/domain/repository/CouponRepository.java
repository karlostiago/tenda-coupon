package br.com.tenda.coupon.domain.repository;

import br.com.tenda.coupon.domain.model.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(UUID id);

    boolean existsByCode(String code);

    Page<Coupon> findAll(Pageable pageable);
}

