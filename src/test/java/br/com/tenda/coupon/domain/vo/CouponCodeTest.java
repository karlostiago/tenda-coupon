package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CouponCode")
class CouponCodeTest {

    @Nested
    @DisplayName("Quando criar um código válido")
    class WhenCreatingValidCode {

        @Test
        @DisplayName("Deve aceitar código alfanumérico com 6 caracteres")
        void shouldAcceptSixAlphanumericCharacters() {
            CouponCode code = CouponCode.from("ABC123");

            assertThat(code.getValue()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("Deve remover caracteres especiais e manter 6 alfanuméricos")
        void shouldRemoveSpecialCharactersAndKeepSixAlphanumeric() {
            CouponCode code = CouponCode.from("AB-C12-3");

            assertThat(code.getValue()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("Deve remover caracteres especiais complexos")
        void shouldRemoveComplexSpecialCharacters() {
            CouponCode code = CouponCode.from("A@B#C$1%2&3");

            assertThat(code.getValue()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("Deve converter código para maiúsculas")
        void shouldConvertToUpperCase() {
            CouponCode code = CouponCode.from("abc123");

            assertThat(code.getValue()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("Deve aceitar apenas números")
        void shouldAcceptOnlyNumbers() {
            CouponCode code = CouponCode.from("123456");

            assertThat(code.getValue()).isEqualTo("123456");
        }

        @Test
        @DisplayName("Deve aceitar apenas letras")
        void shouldAcceptOnlyLetters() {
            CouponCode code = CouponCode.from("ABCDEF");

            assertThat(code.getValue()).isEqualTo("ABCDEF");
        }
    }

    @Nested
    @DisplayName("Quando criar um código inválido")
    class WhenCreatingInvalidCode {

        @Test
        @DisplayName("Deve lançar exceção para código nulo")
        void shouldThrowExceptionForNullCode() {
            assertThatThrownBy(() -> CouponCode.from(null))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Coupon code is required");
        }

        @Test
        @DisplayName("Deve lançar exceção para código vazio")
        void shouldThrowExceptionForEmptyCode() {
            assertThatThrownBy(() -> CouponCode.from(""))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Coupon code is required");
        }

        @Test
        @DisplayName("Deve lançar exceção para código com apenas espaços")
        void shouldThrowExceptionForBlankCode() {
            assertThatThrownBy(() -> CouponCode.from("   "))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Coupon code is required");
        }

        @Test
        @DisplayName("Deve lançar exceção quando resultar em menos de 6 caracteres")
        void shouldThrowExceptionForLessThanSixCharacters() {
            assertThatThrownBy(() -> CouponCode.from("ABC12"))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessageContaining("must have exactly 6 alphanumeric characters");
        }

        @Test
        @DisplayName("Deve lançar exceção quando resultar em mais de 6 caracteres")
        void shouldThrowExceptionForMoreThanSixCharacters() {
            assertThatThrownBy(() -> CouponCode.from("ABC1234"))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessageContaining("must have exactly 6 alphanumeric characters");
        }

        @Test
        @DisplayName("Deve lançar exceção quando apenas caracteres especiais forem fornecidos")
        void shouldThrowExceptionForOnlySpecialCharacters() {
            assertThatThrownBy(() -> CouponCode.from("@#$%&*"))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessageContaining("must have exactly 6 alphanumeric characters");
        }
    }

    @Nested
    @DisplayName("Quando comparar códigos")
    class WhenComparingCodes {

        @Test
        @DisplayName("Códigos com mesmo valor devem ser iguais")
        void codesWithSameValueShouldBeEqual() {
            CouponCode code1 = CouponCode.from("ABC123");
            CouponCode code2 = CouponCode.from("ABC123");

            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("Códigos com valores diferentes não devem ser iguais")
        void codesWithDifferentValuesShouldNotBeEqual() {
            CouponCode code1 = CouponCode.from("ABC123");
            CouponCode code2 = CouponCode.from("DEF456");

            assertThat(code1).isNotEqualTo(code2);
        }
    }
}

