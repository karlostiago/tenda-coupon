package br.com.tenda.coupon.infrastructure.persistence;

import br.com.tenda.coupon.domain.model.Coupon;
import br.com.tenda.coupon.domain.repository.CouponRepository;
import br.com.tenda.coupon.domain.vo.CouponStatus;
import br.com.tenda.coupon.infrastructure.persistence.entity.CouponEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponH2DatabaseAdapter implements CouponRepository {

    private final SpringDataCouponRepository springDataRepository;

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = toEntity(coupon);
        CouponEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return springDataRepository.existsByCode(code);
    }

    @Override
    public Page<Coupon> findAll(Pageable pageable) {
        return springDataRepository.findAll(pageable)
                .map(this::toDomain);
    }

    private Coupon toDomain(CouponEntity entity) {
        return Coupon.reconstruct(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.isPublished(),
                entity.isRedeemed(),
                CouponStatus.from(entity.getStatus())
        );
    }

    private CouponEntity toEntity(Coupon coupon) {
        return new CouponEntity(
                coupon.getId(),
                coupon.getCodeValue(),
                coupon.getDescription().getValue(),
                coupon.getDiscount().getValue(),
                coupon.getExpirationDate().getValue(),
                coupon.isPublished(),
                coupon.isRedeemed(),
                coupon.getStatus()
        );
    }
}

