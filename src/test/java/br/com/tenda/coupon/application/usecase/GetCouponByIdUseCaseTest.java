package br.com.tenda.coupon.application.usecase;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCouponByIdUseCase")
class GetCouponByIdUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private GetCouponByIdUseCase getCouponByIdUseCase;

    private UUID couponId;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        couponId = UUID.randomUUID();
        LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
        coupon = Coupon.create(
                "ABC123",
                "Desconto de verão",
                new BigDecimal("10.50"),
                futureDate,
                false,
                false
        );
    }

    @Nested
    @DisplayName("Quando buscar cupom com sucesso")
    class WhenGettingCouponSuccessfully {

        @Test
        @DisplayName("Deve retornar cupom existente")
        void shouldReturnExistingCoupon() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

            Coupon result = getCouponByIdUseCase.execute(couponId);

            assertThat(result).isNotNull();
            assertThat(result.getCodeValue()).isEqualTo("ABC123");
            assertThat(result.getDescription().getValue()).isEqualTo("Desconto de verão");
            assertThat(result.getDiscount().getValue()).isEqualByComparingTo("10.50");
            assertThat(result.isPublished()).isFalse();
            assertThat(result.getStatus()).isEqualTo("ACTIVE");

            verify(couponRepository).findById(couponId);
            verifyNoMoreInteractions(couponRepository);
        }

        @Test
        @DisplayName("Deve retornar cupom publicado")
        void shouldReturnPublishedCoupon() {
            Coupon publishedCoupon = Coupon.create(
                    "PUB001",
                    "Cupom publicado",
                    new BigDecimal("20.00"),
                    LocalDateTime.now().plusDays(30),
                    true,
                    false
            );
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(publishedCoupon));

            Coupon result = getCouponByIdUseCase.execute(couponId);

            assertThat(result).isNotNull();
            assertThat(result.isPublished()).isTrue();
            verify(couponRepository).findById(couponId);
        }

        @Test
        @DisplayName("Deve retornar cupom deletado")
        void shouldReturnDeletedCoupon() {
            coupon.delete();
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

            Coupon result = getCouponByIdUseCase.execute(couponId);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo("DELETED");
            verify(couponRepository).findById(couponId);
        }

        @Test
        @DisplayName("Deve retornar cupom com valor mínimo de desconto")
        void shouldReturnCouponWithMinimumDiscount() {
            Coupon minDiscountCoupon = Coupon.create(
                    "MIN050",
                    "Desconto mínimo",
                    new BigDecimal("0.50"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(minDiscountCoupon));

            Coupon result = getCouponByIdUseCase.execute(couponId);

            assertThat(result).isNotNull();
            assertThat(result.getDiscount().getValue()).isEqualByComparingTo("0.50");
            verify(couponRepository).findById(couponId);
        }

        @Test
        @DisplayName("Deve retornar cupom com data de expiração próxima")
        void shouldReturnCouponWithNearExpirationDate() {
            LocalDateTime nearFuture = LocalDateTime.now().plusHours(1);
            Coupon nearExpiryCoupon = Coupon.create(
                    "NEAR01",
                    "Expira em breve",
                    new BigDecimal("15.00"),
                    nearFuture,
                    false,
                    false
            );
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(nearExpiryCoupon));

            Coupon result = getCouponByIdUseCase.execute(couponId);

            assertThat(result).isNotNull();
            assertThat(result.getExpirationDate().getValue()).isEqualTo(nearFuture);
            verify(couponRepository).findById(couponId);
        }
    }

    @Nested
    @DisplayName("Quando cupom não existe")
    class WhenCouponDoesNotExist {

        @Test
        @DisplayName("Deve lançar exceção se cupom não for encontrado")
        void shouldThrowExceptionIfCouponNotFound() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> getCouponByIdUseCase.execute(couponId))
                    .isInstanceOf(CouponNotFoundException.class)
                    .hasMessageContaining("Coupon not found with id: " + couponId);

            verify(couponRepository).findById(couponId);
            verifyNoMoreInteractions(couponRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção para ID não existente")
        void shouldThrowExceptionForNonExistentId() {
            UUID nonExistentId = UUID.randomUUID();
            when(couponRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> getCouponByIdUseCase.execute(nonExistentId))
                    .isInstanceOf(CouponNotFoundException.class);

            verify(couponRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("Deve incluir ID do cupom na mensagem de erro")
        void shouldIncludeCouponIdInErrorMessage() {
            UUID specificId = UUID.randomUUID();
            when(couponRepository.findById(specificId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> getCouponByIdUseCase.execute(specificId))
                    .isInstanceOf(CouponNotFoundException.class)
                    .hasMessageContaining(specificId.toString());
        }
    }

    @Nested
    @DisplayName("Quando validar comportamento transacional")
    class WhenValidatingTransactionalBehavior {

        @Test
        @DisplayName("Deve chamar repository apenas uma vez")
        void shouldCallRepositoryOnlyOnce() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

            getCouponByIdUseCase.execute(couponId);

            verify(couponRepository, times(1)).findById(couponId);
            verifyNoMoreInteractions(couponRepository);
        }

        @Test
        @DisplayName("Não deve salvar nada no repository")
        void shouldNotSaveAnythingToRepository() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

            getCouponByIdUseCase.execute(couponId);

            verify(couponRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Quando validar integridade dos dados retornados")
    class WhenValidatingReturnedDataIntegrity {

        @Test
        @DisplayName("Deve retornar o mesmo objeto encontrado no repository")
        void shouldReturnSameObjectFoundInRepository() {
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

            Coupon result = getCouponByIdUseCase.execute(couponId);

            assertThat(result).isSameAs(coupon);
        }

        @Test
        @DisplayName("Deve preservar todos os campos do cupom")
        void shouldPreserveAllCouponFields() {
            LocalDateTime specificDate = LocalDateTime.of(2026, 12, 31, 23, 59);
            Coupon fullCoupon = Coupon.create(
                    "FULL01",
                    "Cupom completo",
                    new BigDecimal("99.99"),
                    specificDate,
                    true,
                    false
            );
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(fullCoupon));

            Coupon result = getCouponByIdUseCase.execute(couponId);

            assertThat(result.getCodeValue()).isEqualTo("FULL01");
            assertThat(result.getDescription().getValue()).isEqualTo("Cupom completo");
            assertThat(result.getDiscount().getValue()).isEqualByComparingTo("99.99");
            assertThat(result.getExpirationDate().getValue()).isEqualTo(specificDate);
            assertThat(result.isPublished()).isTrue();
            assertThat(result.isRedeemed()).isFalse();
        }
    }
}

