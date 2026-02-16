package br.com.tenda.coupon.infrastructure.persistence;

import br.com.tenda.coupon.infrastructure.persistence.entity.CouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataCouponRepository extends JpaRepository<CouponEntity, UUID> {

    boolean existsByCode(String code);

    Page<CouponEntity> findAllByStatusNot(String status, Pageable pageable);
}

