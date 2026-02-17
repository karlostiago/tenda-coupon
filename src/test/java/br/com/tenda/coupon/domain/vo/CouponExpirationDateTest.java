package br.com.tenda.coupon.domain.vo;

import br.com.tenda.coupon.domain.exception.ExpirationDateException;
import br.com.tenda.coupon.domain.exception.InvalidCouponException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CouponExpirationDate")
class CouponExpirationDateTest {

    @Nested
    @DisplayName("Quando criar uma data de expiração válida")
    class WhenCreatingValidExpirationDate {

        @Test
        @DisplayName("Deve aceitar data futura próxima")
        void shouldAcceptNearFutureDate() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
            CouponExpirationDate expirationDate = new CouponExpirationDate(futureDate);

            assertThat(expirationDate.getValue()).isEqualTo(futureDate);
        }

        @Test
        @DisplayName("Deve aceitar data futura distante")
        void shouldAcceptFarFutureDate() {
            LocalDateTime futureDate = LocalDateTime.now().plusYears(10);
            CouponExpirationDate expirationDate = new CouponExpirationDate(futureDate);

            assertThat(expirationDate.getValue()).isEqualTo(futureDate);
        }

        @Test
        @DisplayName("Deve aceitar data futura em horas")
        void shouldAcceptFutureDateInHours() {
            LocalDateTime futureDate = LocalDateTime.now().plusHours(5);
            CouponExpirationDate expirationDate = new CouponExpirationDate(futureDate);

            assertThat(expirationDate.getValue()).isEqualTo(futureDate);
        }

        @Test
        @DisplayName("Deve aceitar data futura em minutos")
        void shouldAcceptFutureDateInMinutes() {
            LocalDateTime futureDate = LocalDateTime.now().plusMinutes(30);
            CouponExpirationDate expirationDate = new CouponExpirationDate(futureDate);

            assertThat(expirationDate.getValue()).isEqualTo(futureDate);
        }

        @Test
        @DisplayName("Deve aceitar data futura em segundos")
        void shouldAcceptFutureDateInSeconds() {
            LocalDateTime futureDate = LocalDateTime.now().plusSeconds(10);
            CouponExpirationDate expirationDate = new CouponExpirationDate(futureDate);

            assertThat(expirationDate.getValue()).isEqualTo(futureDate);
        }

        @Test
        @DisplayName("Deve aceitar data futura em meses")
        void shouldAcceptFutureDateInMonths() {
            LocalDateTime futureDate = LocalDateTime.now().plusMonths(6);
            CouponExpirationDate expirationDate = new CouponExpirationDate(futureDate);

            assertThat(expirationDate.getValue()).isEqualTo(futureDate);
        }

        @Test
        @DisplayName("Deve aceitar data futura específica")
        void shouldAcceptSpecificFutureDate() {
            LocalDateTime futureDate = LocalDateTime.of(2030, 12, 31, 23, 59, 59);
            CouponExpirationDate expirationDate = new CouponExpirationDate(futureDate);

            assertThat(expirationDate.getValue()).isEqualTo(futureDate);
        }
    }

    @Nested
    @DisplayName("Quando criar uma data de expiração inválida")
    class WhenCreatingInvalidExpirationDate {

        @Test
        @DisplayName("Deve lançar exceção para data nula")
        void shouldThrowExceptionForNullDate() {
            assertThatThrownBy(() -> CouponExpirationDate.from(null))
                    .isInstanceOf(ExpirationDateException.class)
                    .hasMessage("Expiration date is required");
        }

        @Test
        @DisplayName("Deve lançar exceção para data no passado")
        void shouldThrowExceptionForPastDate() {
            LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

            assertThatThrownBy(() -> CouponExpirationDate.from(pastDate))
                    .isInstanceOf(ExpirationDateException.class)
                    .hasMessage("Expiration date cannot be in the past");
        }

        @Test
        @DisplayName("Deve lançar exceção para data muito antiga")
        void shouldThrowExceptionForVeryOldDate() {
            LocalDateTime pastDate = LocalDateTime.now().minusYears(10);

            assertThatThrownBy(() -> CouponExpirationDate.from(pastDate))
                    .isInstanceOf(ExpirationDateException.class)
                    .hasMessage("Expiration date cannot be in the past");
        }

        @Test
        @DisplayName("Deve lançar exceção para data algumas horas no passado")
        void shouldThrowExceptionForHoursAgo() {
            LocalDateTime pastDate = LocalDateTime.now().minusHours(5);

            assertThatThrownBy(() -> CouponExpirationDate.from(pastDate))
                    .isInstanceOf(ExpirationDateException.class)
                    .hasMessage("Expiration date cannot be in the past");
        }

        @Test
        @DisplayName("Deve lançar exceção para data alguns minutos no passado")
        void shouldThrowExceptionForMinutesAgo() {
            LocalDateTime pastDate = LocalDateTime.now().minusMinutes(30);

            assertThatThrownBy(() -> CouponExpirationDate.from(pastDate))
                    .isInstanceOf(ExpirationDateException.class)
                    .hasMessage("Expiration date cannot be in the past");
        }

        @Test
        @DisplayName("Deve lançar exceção para data alguns segundos no passado")
        void shouldThrowExceptionForSecondsAgo() {
            LocalDateTime pastDate = LocalDateTime.now().minusSeconds(10);

            assertThatThrownBy(() -> CouponExpirationDate.from(pastDate))
                    .isInstanceOf(ExpirationDateException.class)
                    .hasMessage("Expiration date cannot be in the past");
        }

        @Test
        @DisplayName("Deve lançar exceção para data específica no passado")
        void shouldThrowExceptionForSpecificPastDate() {
            LocalDateTime pastDate = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

            assertThatThrownBy(() -> CouponExpirationDate.from(pastDate))
                    .isInstanceOf(ExpirationDateException.class)
                    .hasMessage("Expiration date cannot be in the past");
        }
    }
}

