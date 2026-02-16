package br.com.tenda.coupon.domain.model;

import br.com.tenda.coupon.domain.exception.CouponAlreadyDeletedException;
import br.com.tenda.coupon.domain.exception.CouponStatusException;
import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import br.com.tenda.coupon.domain.vo.CouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Coupon")
class CouponTest {

    @Nested
    @DisplayName("Quando criar um cupom válido")
    class WhenCreatingValidCoupon {

        @Test
        @DisplayName("Deve criar cupom com todos os campos obrigatórios")
        void shouldCreateCouponWithAllRequiredFields() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Desconto de verão",
                    new BigDecimal("10.50"),
                    futureDate,
                    false,
                    false
            );

            assertThat(coupon).isNotNull();
            assertThat(coupon.getId()).isNotNull();
            assertThat(coupon.getCodeValue()).isEqualTo("ABC123");
            assertThat(coupon.getDescription().getValue()).isEqualTo("Desconto de verão");
            assertThat(coupon.getDiscount().getValue()).isEqualByComparingTo("10.50");
            assertThat(coupon.getExpirationDate().getValue()).isEqualTo(futureDate);
            assertThat(coupon.isPublished()).isFalse();
            assertThat(coupon.isRedeemed()).isFalse();
        }

        @Test
        @DisplayName("Deve criar cupom já publicado")
        void shouldCreatePublishedCoupon() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Desconto de verão",
                    new BigDecimal("10.50"),
                    futureDate,
                    true,
                    false
            );

            assertThat(coupon.isPublished()).isTrue();
        }

        @Test
        @DisplayName("Deve criar cupom com valor de desconto mínimo permitido (0.5)")
        void shouldCreateCouponWithMinimumDiscountValue() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Desconto mínimo",
                    new BigDecimal("0.5"),
                    futureDate,
                    false,
                    false
            );

            assertThat(coupon.getDiscount().getValue()).isEqualByComparingTo("0.5");
        }

        @Test
        @DisplayName("Deve criar cupom com valor de desconto alto")
        void shouldCreateCouponWithHighDiscountValue() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Desconto alto",
                    new BigDecimal("999999.99"),
                    futureDate,
                    false,
                    false
            );

            assertThat(coupon.getDiscount().getValue()).isEqualByComparingTo("999999.99");
        }
    }

    @Nested
    @DisplayName("Quando criar um cupom inválido")
    class WhenCreatingInvalidCoupon {

        @Test
        @DisplayName("Deve lançar exceção quando descrição for nula")
        void shouldThrowExceptionForNullDescription() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            assertThatThrownBy(() -> Coupon.create(
                    "ABC123",
                    null,
                    new BigDecimal("10.50"),
                    futureDate,
                    false,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Description is required");
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição for vazia")
        void shouldThrowExceptionForEmptyDescription() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            assertThatThrownBy(() -> Coupon.create(
                    "ABC123",
                    "",
                    new BigDecimal("10.50"),
                    futureDate,
                    false,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Description is required");
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição for apenas espaços")
        void shouldThrowExceptionForBlankDescription() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            assertThatThrownBy(() -> Coupon.create(
                    "ABC123",
                    "   ",
                    new BigDecimal("10.50"),
                    futureDate,
                    false,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Description is required");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor de desconto for nulo")
        void shouldThrowExceptionForNullDiscountValue() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            assertThatThrownBy(() -> Coupon.create(
                    "ABC123",
                    "Desconto",
                    null,
                    futureDate,
                    false,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value is required");
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor de desconto for menor que 0.5")
        void shouldThrowExceptionForDiscountValueLessThanMinimum() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

            assertThatThrownBy(() -> Coupon.create(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("0.49"),
                    futureDate,
                    false,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value must be at least 0.5");
        }

        @Test
        @DisplayName("Deve lançar exceção quando data de expiração for nula")
        void shouldThrowExceptionForNullExpirationDate() {
            assertThatThrownBy(() -> Coupon.create(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("10.50"),
                    null,
                    false,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Expiration date is required");
        }

        @Test
        @DisplayName("Deve lançar exceção quando data de expiração for no passado")
        void shouldThrowExceptionForPastExpirationDate() {
            LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

            assertThatThrownBy(() -> Coupon.create(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("10.50"),
                    pastDate,
                    false,
                    false
            ))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Expiration date cannot be in the past");
        }
    }

    @Nested
    @DisplayName("Quando deletar um cupom")
    class WhenDeletingCoupon {

        @Test
        @DisplayName("Deve realizar soft delete do cupom")
        void shouldSoftDeleteCoupon() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("10.50"),
                    futureDate,
                    false,
                    false
            );

            coupon.delete();

            assertThat(coupon.getStatus()).isEqualTo("DELETED");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar deletar cupom já deletado")
        void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("10.50"),
                    futureDate,
                    false,
                    false
            );

            coupon.delete();

            assertThatThrownBy(coupon::delete)
                    .isInstanceOf(CouponAlreadyDeletedException.class)
                    .hasMessage(String.format("Coupon with id %s is already deleted", coupon.getId()));
        }

        @Test
        @DisplayName("Não deve alterar outros atributos ao deletar")
        void shouldNotChangeOtherAttributesWhenDeleting() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Desconto",
                    new BigDecimal("10.50"),
                    futureDate,
                    true,
                    false
            );

            String originalCode = coupon.getCodeValue();
            String originalDescription = coupon.getDescription().getValue();
            BigDecimal originalDiscount = coupon.getDiscount().getValue();
            LocalDateTime originalExpiration = coupon.getExpirationDate().getValue();
            boolean originalPublished = coupon.isPublished();

            coupon.delete();

            assertThat(coupon.getCodeValue()).isEqualTo(originalCode);
            assertThat(coupon.getDescription().getValue()).isEqualTo(originalDescription);
            assertThat(coupon.getDiscount().getValue()).isEqualTo(originalDiscount);
            assertThat(coupon.getExpirationDate().getValue()).isEqualTo(originalExpiration);
            assertThat(coupon.isPublished()).isEqualTo(originalPublished);
        }
    }

    @Nested
    @DisplayName("Quando validar status do cupom")
    class WhenValidatingCouponStatus {

        @Test
        @DisplayName("Deve lançar exceção ao criar cupom com status inválido")
        void shouldThrowExceptionForInvalidStatus() {
            assertThatThrownBy(() -> CouponStatus.from("INVALID_STATUS"))
                    .isInstanceOf(CouponStatusException.class)
                    .hasMessage("Invalid coupon status: INVALID_STATUS");
        }

        @Test
        @DisplayName("Deve lançar exceção para status vazio")
        void shouldThrowExceptionForEmptyStatus() {
            assertThatThrownBy(() -> CouponStatus.from(""))
                    .isInstanceOf(CouponStatusException.class)
                    .hasMessage("Invalid coupon status: ");
        }

        @Test
        @DisplayName("Deve lançar exceção para status com caracteres especiais")
        void shouldThrowExceptionForStatusWithSpecialCharacters() {
            assertThatThrownBy(() -> CouponStatus.from("ACTIVE@#$"))
                    .isInstanceOf(CouponStatusException.class)
                    .hasMessage("Invalid coupon status: ACTIVE@#$");
        }

        @Test
        @DisplayName("Deve aceitar status válido ACTIVE em diferentes casos")
        void shouldAcceptValidActiveStatus() {
            assertThat(CouponStatus.from("ACTIVE")).isEqualTo(CouponStatus.ACTIVE);
            assertThat(CouponStatus.from("active")).isEqualTo(CouponStatus.ACTIVE);
            assertThat(CouponStatus.from("Active")).isEqualTo(CouponStatus.ACTIVE);
        }

        @Test
        @DisplayName("Deve aceitar status válido INACTIVE em diferentes casos")
        void shouldAcceptValidInactiveStatus() {
            assertThat(CouponStatus.from("INACTIVE")).isEqualTo(CouponStatus.INACTIVE);
            assertThat(CouponStatus.from("inactive")).isEqualTo(CouponStatus.INACTIVE);
            assertThat(CouponStatus.from("Inactive")).isEqualTo(CouponStatus.INACTIVE);
        }

        @Test
        @DisplayName("Deve aceitar status válido DELETED em diferentes casos")
        void shouldAcceptValidDeletedStatus() {
            assertThat(CouponStatus.from("DELETED")).isEqualTo(CouponStatus.DELETED);
            assertThat(CouponStatus.from("deleted")).isEqualTo(CouponStatus.DELETED);
            assertThat(CouponStatus.from("Deleted")).isEqualTo(CouponStatus.DELETED);
        }
    }
}

