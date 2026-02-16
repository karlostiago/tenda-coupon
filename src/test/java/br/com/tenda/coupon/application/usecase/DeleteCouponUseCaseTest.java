package br.com.tenda.coupon.application.usecase;

import br.com.tenda.coupon.domain.exception.CouponAlreadyDeletedException;
import br.com.tenda.coupon.domain.exception.CouponNotFoundException;
import br.com.tenda.coupon.domain.model.Coupon;
import br.com.tenda.coupon.domain.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCouponUseCase")
class DeleteCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private DeleteCouponUseCase deleteCouponUseCase;

    private UUID couponId;
    private Coupon activeCoupon;

    @BeforeEach
    void setUp() {
        couponId = UUID.randomUUID();
        LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
        activeCoupon = Coupon.create("ABC123", "Desconto", new BigDecimal("10.50"), futureDate, false, false);
    }

    @Nested
    @DisplayName("Quando deletar cupom com sucesso")
    class WhenDeletingCouponSuccessfully {

        @Test
        @DisplayName("Deve deletar cupom ativo")
        void shouldDeleteActiveCoupon() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(activeCoupon));
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertThatCode(() -> deleteCouponUseCase.execute(couponId))
                    .doesNotThrowAnyException();

            assertThat(activeCoupon.getStatus()).isEqualTo("DELETED");

            verify(couponRepository).findById(couponId);
            verify(couponRepository).save(activeCoupon);
        }
    }

    @Nested
    @DisplayName("Quando cupom não existe")
    class WhenCouponDoesNotExist {

        @Test
        @DisplayName("Deve lançar exceção se cupom não for encontrado")
        void shouldThrowExceptionIfCouponNotFound() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> deleteCouponUseCase.execute(couponId))
                    .isInstanceOf(CouponNotFoundException.class)
                    .hasMessage("Coupon not found with id: " + couponId);

            verify(couponRepository).findById(couponId);
            verify(couponRepository, never()).save(any(Coupon.class));
        }
    }

    @Nested
    @DisplayName("Quando tentar deletar cupom já deletado")
    class WhenDeletingAlreadyDeletedCoupon {

        @Test
        @DisplayName("Deve lançar exceção ao tentar deletar cupom já deletado")
        void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
            activeCoupon.delete();
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(activeCoupon));

            assertThatThrownBy(() -> deleteCouponUseCase.execute(couponId))
                    .isInstanceOf(CouponAlreadyDeletedException.class)
                    .hasMessage(String.format("Coupon with id %s is already deleted", activeCoupon.getId()));

            verify(couponRepository).findById(couponId);
            verify(couponRepository, never()).save(any(Coupon.class));
        }
    }
}

