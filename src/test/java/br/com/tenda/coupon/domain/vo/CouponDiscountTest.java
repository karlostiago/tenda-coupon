package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CouponDiscount")
class CouponDiscountTest {

    @Nested
    @DisplayName("Quando criar um desconto válido")
    class WhenCreatingValidDiscount {

        @Test
        @DisplayName("Deve aceitar valor mínimo de 0.5")
        void shouldAcceptMinimumValue() {
            CouponDiscount discount = new CouponDiscount(new BigDecimal("0.5"));

            assertThat(discount.getValue()).isEqualByComparingTo("0.5");
        }

        @Test
        @DisplayName("Deve aceitar valor exatamente 0.5")
        void shouldAcceptExactlyHalfValue() {
            CouponDiscount discount = new CouponDiscount(new BigDecimal("0.50"));

            assertThat(discount.getValue()).isEqualByComparingTo("0.5");
        }

        @Test
        @DisplayName("Deve aceitar valor intermediário")
        void shouldAcceptIntermediateValue() {
            CouponDiscount discount = new CouponDiscount(new BigDecimal("10.50"));

            assertThat(discount.getValue()).isEqualByComparingTo("10.50");
        }

        @Test
        @DisplayName("Deve aceitar valor alto")
        void shouldAcceptHighValue() {
            CouponDiscount discount = new CouponDiscount(new BigDecimal("999999.99"));

            assertThat(discount.getValue()).isEqualByComparingTo("999999.99");
        }

        @Test
        @DisplayName("Deve aceitar valor inteiro")
        void shouldAcceptWholeNumberValue() {
            CouponDiscount discount = new CouponDiscount(new BigDecimal("100"));

            assertThat(discount.getValue()).isEqualByComparingTo("100");
        }

        @Test
        @DisplayName("Deve aceitar valor com precisão alta")
        void shouldAcceptHighPrecisionValue() {
            CouponDiscount discount = new CouponDiscount(new BigDecimal("10.5555"));

            assertThat(discount.getValue()).isEqualByComparingTo("10.5555");
        }

        @Test
        @DisplayName("Deve aceitar valor com string")
        void shouldAcceptValueFromString() {
            CouponDiscount discount = new CouponDiscount(new BigDecimal("25.75"));

            assertThat(discount.getValue()).isEqualByComparingTo("25.75");
        }

        @Test
        @DisplayName("Deve aceitar valor logo acima do mínimo")
        void shouldAcceptValueJustAboveMinimum() {
            CouponDiscount discount = new CouponDiscount(new BigDecimal("0.51"));

            assertThat(discount.getValue()).isEqualByComparingTo("0.51");
        }
    }

    @Nested
    @DisplayName("Quando criar um desconto inválido")
    class WhenCreatingInvalidDiscount {

        @Test
        @DisplayName("Deve lançar exceção para valor nulo")
        void shouldThrowExceptionForNullValue() {
            assertThatThrownBy(() -> CouponDiscount.from(null))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value is required");
        }

        @Test
        @DisplayName("Deve lançar exceção para valor menor que 0.5")
        void shouldThrowExceptionForValueLessThanMinimum() {
            assertThatThrownBy(() -> CouponDiscount.from(new BigDecimal("0.49")))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value must be at least 0.5");
        }

        @Test
        @DisplayName("Deve lançar exceção para valor zero")
        void shouldThrowExceptionForZeroValue() {
            assertThatThrownBy(() -> CouponDiscount.from(BigDecimal.ZERO))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value must be at least 0.5");
        }

        @Test
        @DisplayName("Deve lançar exceção para valor negativo")
        void shouldThrowExceptionForNegativeValue() {
            assertThatThrownBy(() -> CouponDiscount.from(new BigDecimal("-10.50")))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value must be at least 0.5");
        }

        @Test
        @DisplayName("Deve lançar exceção para valor muito pequeno")
        void shouldThrowExceptionForVerySmallValue() {
            assertThatThrownBy(() -> CouponDiscount.from(new BigDecimal("0.001")))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value must be at least 0.5");
        }

        @Test
        @DisplayName("Deve lançar exceção para valor ligeiramente abaixo do mínimo")
        void shouldThrowExceptionForValueJustBelowMinimum() {
            assertThatThrownBy(() -> CouponDiscount.from(new BigDecimal("0.4999")))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Discount value must be at least 0.5");
        }
    }
}

