package br.com.tenda.coupon.presentation.controller;

import br.com.tenda.coupon.application.usecase.CreateCouponUseCase;
import br.com.tenda.coupon.application.usecase.DeleteCouponUseCase;
import br.com.tenda.coupon.application.usecase.GetAllCouponsUseCase;
import br.com.tenda.coupon.application.usecase.GetCouponByIdUseCase;
import br.com.tenda.coupon.domain.model.Coupon;
import br.com.tenda.coupon.presentation.dto.CreateCouponRequest;
import br.com.tenda.coupon.presentation.dto.CouponResponse;
import br.com.tenda.coupon.presentation.mapper.CouponMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController implements CouponApi {

    private final CreateCouponUseCase createCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;
    private final GetCouponByIdUseCase getCouponByIdUseCase;
    private final GetAllCouponsUseCase getAllCouponsUseCase;

    @PostMapping
    @Override
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        Coupon coupon = createCouponUseCase.execute(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.isPublished(),
                request.isRedeemed()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CouponMapper.toResponse(coupon));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteCoupon(@PathVariable("id") UUID id) {
        deleteCouponUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable("id") UUID id) {
        Coupon coupon = getCouponByIdUseCase.execute(id);
        return ResponseEntity.ok(CouponMapper.toResponse(coupon));
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<CouponResponse>> getAllCoupons(@PageableDefault(size = 20) Pageable pageable) {
        Page<Coupon> couponsPage = getAllCouponsUseCase.execute(pageable);
        Page<CouponResponse> responsePage = couponsPage.map(CouponMapper::toResponse);
        return ResponseEntity.ok(responsePage);
    }
}

