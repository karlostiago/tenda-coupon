package br.com.tenda.coupon.application.usecase;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CreateCouponUseCase.
 * Foca em testar o comportamento do caso de uso com mocks.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCouponUseCase")
class CreateCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CreateCouponUseCase createCouponUseCase;

    private LocalDateTime futureDate;

    @BeforeEach
    void setUp() {
        futureDate = LocalDateTime.now().plusDays(30);
    }

    @Nested
    @DisplayName("Quando criar cupom com sucesso")
    class WhenCreatingCouponSuccessfully {

        @Test
        @DisplayName("Deve criar e salvar cupom válido")
        void shouldCreateAndSaveValidCoupon() {
            when(couponRepository.existsByCode(anyString())).thenReturn(false);
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Coupon result = createCouponUseCase.execute(
                    "ABC123",
                    "Desconto de verão",
                    new BigDecimal("10.50"),
                    futureDate,
                    false
            );

            assertThat(result).isNotNull();
            assertThat(result.getCodeValue()).isEqualTo("ABC123");
            assertThat(result.getDescription().getValue()).isEqualTo("Desconto de verão");
            assertThat(result.getDiscount().getValue()).isEqualByComparingTo("10.50");

            verify(couponRepository).existsByCode("ABC123");
            verify(couponRepository).save(any(Coupon.class));
        }

        @Test
        @DisplayName("Deve criar cupom com código contendo caracteres especiais")
        void shouldCreateCouponWithSpecialCharactersInCode() {
            when(couponRepository.existsByCode("ABC123")).thenReturn(false);
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Coupon result = createCouponUseCase.execute(
                    "AB-C12-3",
                    "Desconto",
                    new BigDecimal("10.50"),
                    futureDate,
                    false
            );

            assertThat(result.getCodeValue()).isEqualTo("ABC123");
            verify(couponRepository).existsByCode("ABC123");
        }

        @Test
        @DisplayName("Deve criar cupom já publicado")
        void shouldCreatePublishedCoupon() {
            when(couponRepository.existsByCode(anyString())).thenReturn(false);
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Coupon result = createCouponUseCase.execute(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("10.50"),
                    futureDate,
                    true
            );

            assertThat(result.isPublished()).isTrue();
        }
    }

    @Nested
    @DisplayName("Quando tentar criar cupom duplicado")
    class WhenCreatingDuplicateCoupon {

        @Test
        @DisplayName("Deve lançar exceção se código já existe")
        void shouldThrowExceptionIfCodeAlreadyExists() {
            when(couponRepository.existsByCode("ABC123")).thenReturn(true);

            assertThatThrownBy(() -> createCouponUseCase.execute(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("10.50"),
                    futureDate,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("A coupon with this code already exists");

            verify(couponRepository).existsByCode("ABC123");
            verify(couponRepository, never()).save(any(Coupon.class));
        }

        @Test
        @DisplayName("Deve lançar exceção se código com caracteres especiais já existe")
        void shouldThrowExceptionIfCodeWithSpecialCharactersAlreadyExists() {
            when(couponRepository.existsByCode("ABC123")).thenReturn(true);

            assertThatThrownBy(() -> createCouponUseCase.execute(
                    "AB-C12-3",
                    "Desconto",
                    new BigDecimal("10.50"),
                    futureDate,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("A coupon with this code already exists");

            verify(couponRepository).existsByCode("ABC123");
            verify(couponRepository, never()).save(any(Coupon.class));
        }
    }

    @Nested
    @DisplayName("Quando validar regras de negócio")
    class WhenValidatingBusinessRules {

        @Test
        @DisplayName("Deve lançar exceção para valor de desconto inválido")
        void shouldThrowExceptionForInvalidDiscountValue() {
            when(couponRepository.existsByCode(anyString())).thenReturn(false);

            assertThatThrownBy(() -> createCouponUseCase.execute(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("0.4"),
                    futureDate,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value must be at least 0.5");

            verify(couponRepository, never()).save(any(Coupon.class));
        }

        @Test
        @DisplayName("Deve lançar exceção para data de expiração no passado")
        void shouldThrowExceptionForPastExpirationDate() {
            when(couponRepository.existsByCode(anyString())).thenReturn(false);
            LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

            assertThatThrownBy(() -> createCouponUseCase.execute(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("10.50"),
                    pastDate,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Expiration date cannot be in the past");

            verify(couponRepository, never()).save(any(Coupon.class));
        }
    }
}

