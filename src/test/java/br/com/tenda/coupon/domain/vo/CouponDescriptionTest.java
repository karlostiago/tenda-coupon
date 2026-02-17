package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CouponDescription")
class CouponDescriptionTest {

    @Nested
    @DisplayName("Quando criar uma descrição válida")
    class WhenCreatingValidDescription {

        @Test
        @DisplayName("Deve aceitar descrição simples")
        void shouldAcceptSimpleDescription() {
            CouponDescription description = new CouponDescription("Desconto de verão");

            assertThat(description.getValue()).isEqualTo("Desconto de verão");
        }

        @Test
        @DisplayName("Deve aceitar descrição com caracteres especiais")
        void shouldAcceptDescriptionWithSpecialCharacters() {
            CouponDescription description = new CouponDescription("Desconto de 50% - Promoção!");

            assertThat(description.getValue()).isEqualTo("Desconto de 50% - Promoção!");
        }

        @Test
        @DisplayName("Deve aceitar descrição longa")
        void shouldAcceptLongDescription() {
            String longDescription = "Este é um cupom de desconto especial para a promoção de verão " +
                    "que oferece grandes descontos em diversos produtos selecionados da loja";
            CouponDescription description = new CouponDescription(longDescription);

            assertThat(description.getValue()).isEqualTo(longDescription);
        }

        @Test
        @DisplayName("Deve aceitar descrição com números")
        void shouldAcceptDescriptionWithNumbers() {
            CouponDescription description = new CouponDescription("Desconto de R$ 100 reais");

            assertThat(description.getValue()).isEqualTo("Desconto de R$ 100 reais");
        }

        @Test
        @DisplayName("Deve aceitar descrição com um único caractere")
        void shouldAcceptSingleCharacterDescription() {
            CouponDescription description = new CouponDescription("A");

            assertThat(description.getValue()).isEqualTo("A");
        }

        @Test
        @DisplayName("Deve aceitar descrição com quebras de linha")
        void shouldAcceptDescriptionWithLineBreaks() {
            CouponDescription description = new CouponDescription("Desconto\nde verão");

            assertThat(description.getValue()).isEqualTo("Desconto\nde verão");
        }
    }

    @Nested
    @DisplayName("Quando criar uma descrição inválida")
    class WhenCreatingInvalidDescription {

        @Test
        @DisplayName("Deve lançar exceção para descrição nula")
        void shouldThrowExceptionForNullDescription() {
            assertThatThrownBy(() -> CouponDescription.from(null))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Description is required");
        }

        @Test
        @DisplayName("Deve lançar exceção para descrição vazia")
        void shouldThrowExceptionForEmptyDescription() {
            assertThatThrownBy(() -> CouponDescription.from(""))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Description is required");
        }

        @Test
        @DisplayName("Deve lançar exceção para descrição com apenas espaços")
        void shouldThrowExceptionForBlankDescription() {
            assertThatThrownBy(() -> CouponDescription.from("   "))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Description is required");
        }

        @Test
        @DisplayName("Deve lançar exceção para descrição com apenas tabs")
        void shouldThrowExceptionForTabsOnly() {
            assertThatThrownBy(() -> CouponDescription.from("\t\t\t"))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Description is required");
        }

        @Test
        @DisplayName("Deve lançar exceção para descrição com espaços e quebras de linha")
        void shouldThrowExceptionForWhitespaceOnly() {
            assertThatThrownBy(() -> CouponDescription.from("  \n  \t  "))
                    .isInstanceOf(InvalidCouponException.class)
                    .hasMessage("Description is required");
        }
    }
}
