package br.com.tenda.coupon.infrastructure.persistence;

import br.com.tenda.coupon.domain.model.Coupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(CouponH2DatabaseAdapter.class)
@ActiveProfiles("test")
@DisplayName("CouponRepositoryAdapter - Integração com H2")
class CouponRepositoryAdapterTest {

    @Autowired
    private CouponH2DatabaseAdapter couponRepository;

    @Nested
    @DisplayName("Quando salvar cupom")
    class WhenSavingCoupon {

        @Test
        @DisplayName("Deve salvar e recuperar cupom completo")
        void shouldSaveAndRetrieveCompleteCoupon() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "ABC123",
                    "Desconto de verão",
                    new BigDecimal("10.50"),
                    futureDate,
                    true,
                    false
            );

            Coupon savedCoupon = couponRepository.save(coupon);

            assertThat(savedCoupon).isNotNull();
            assertThat(savedCoupon.getId()).isNotNull();

            Optional<Coupon> retrievedCoupon = couponRepository.findById(savedCoupon.getId());

            assertThat(retrievedCoupon).isPresent();
            assertThat(retrievedCoupon.get().getCodeValue()).isEqualTo("ABC123");
            assertThat(retrievedCoupon.get().getDescription().getValue()).isEqualTo("Desconto de verão");
            assertThat(retrievedCoupon.get().getDiscount().getValue()).isEqualByComparingTo("10.50");
            assertThat(retrievedCoupon.get().getExpirationDate().getValue()).isEqualTo(futureDate);
            assertThat(retrievedCoupon.get().isPublished()).isTrue();
            assertThat(retrievedCoupon.get().isRedeemed()).isFalse();
        }

        @Test
        @DisplayName("Deve salvar cupom com soft delete")
        void shouldSaveCouponWithSoftDelete() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "DEF456",
                    "Desconto",
                    new BigDecimal("5.00"),
                    futureDate,
                    false,
                    false
            );

            Coupon savedCoupon = couponRepository.save(coupon);
            savedCoupon.delete();

            Coupon deletedCoupon = couponRepository.save(savedCoupon);

            assertThat(deletedCoupon.getStatus()).isEqualTo("DELETED");

            Optional<Coupon> retrievedCoupon = couponRepository.findById(deletedCoupon.getId());

            assertThat(retrievedCoupon).isPresent();
            assertThat(retrievedCoupon.get().getStatus()).isEqualTo("DELETED");
        }

        @Test
        @DisplayName("Deve manter dados originais após soft delete")
        void shouldKeepOriginalDataAfterSoftDelete() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "GHI789",
                    "Desconto especial",
                    new BigDecimal("25.75"),
                    futureDate,
                    true,
                    false
            );

            Coupon savedCoupon = couponRepository.save(coupon);
            UUID originalId = savedCoupon.getId();

            savedCoupon.delete();
            couponRepository.save(savedCoupon);

            Optional<Coupon> retrievedCoupon = couponRepository.findById(originalId);

            assertThat(retrievedCoupon).isPresent();
            assertThat(retrievedCoupon.get().getCodeValue()).isEqualTo("GHI789");
            assertThat(retrievedCoupon.get().getDescription().getValue()).isEqualTo("Desconto especial");
            assertThat(retrievedCoupon.get().getDiscount().getValue()).isEqualByComparingTo("25.75");
            assertThat(retrievedCoupon.get().isPublished()).isTrue();
            assertThat(retrievedCoupon.get().isRedeemed()).isFalse();
            assertThat(retrievedCoupon.get().getStatus()).isEqualTo("DELETED");
        }
    }

    @Nested
    @DisplayName("Quando buscar cupom")
    class WhenFindingCoupon {

        @Test
        @DisplayName("Deve retornar empty para ID inexistente")
        void shouldReturnEmptyForNonExistentId() {
            UUID randomId = UUID.randomUUID();

            Optional<Coupon> result = couponRepository.findById(randomId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve encontrar cupom por ID")
        void shouldFindCouponById() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "JKL012",
                    "Desconto",
                    new BigDecimal("10.00"),
                    futureDate,
                    false,
                    false
            );

            Coupon savedCoupon = couponRepository.save(coupon);

            Optional<Coupon> foundCoupon = couponRepository.findById(savedCoupon.getId());

            assertThat(foundCoupon).isPresent();
            assertThat(foundCoupon.get().getId()).isEqualTo(savedCoupon.getId());
        }
    }

    @Nested
    @DisplayName("Quando verificar existência por código")
    class WhenCheckingExistenceByCode {

        @Test
        @DisplayName("Deve retornar true se código existe")
        void shouldReturnTrueIfCodeExists() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "MNO345",
                    "Desconto",
                    new BigDecimal("10.00"),
                    futureDate,
                    false,
                    false
            );

            couponRepository.save(coupon);

            boolean exists = couponRepository.existsByCode("MNO345");

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false se código não existe")
        void shouldReturnFalseIfCodeDoesNotExist() {
            boolean exists = couponRepository.existsByCode("XXXXXX");

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("Deve retornar true mesmo para cupom deletado")
        void shouldReturnTrueEvenForDeletedCoupon() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon coupon = Coupon.create(
                    "PQR678",
                    "Desconto",
                    new BigDecimal("10.00"),
                    futureDate,
                    false,
                    false
            );

            Coupon savedCoupon = couponRepository.save(coupon);
            savedCoupon.delete();
            couponRepository.save(savedCoupon);

            boolean exists = couponRepository.existsByCode("PQR678");

            assertThat(exists).isTrue();
        }
    }

    @Nested
    @DisplayName("Quando testar conversões de domínio")
    class WhenTestingDomainConversions {

        @Test
        @DisplayName("Deve converter corretamente entre domínio e entidade JPA")
        void shouldConvertCorrectlyBetweenDomainAndEntity() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
            Coupon originalCoupon = Coupon.create(
                    "STU901",
                    "Desconto de inverno",
                    new BigDecimal("15.99"),
                    futureDate,
                    true,
                    false
            );

            Coupon savedCoupon = couponRepository.save(originalCoupon);
            Optional<Coupon> retrievedCoupon = couponRepository.findById(savedCoupon.getId());

            assertThat(retrievedCoupon).isPresent();
            Coupon coupon = retrievedCoupon.get();

            assertThat(coupon.getId()).isEqualTo(savedCoupon.getId());
            assertThat(coupon.getCodeValue()).isEqualTo(originalCoupon.getCodeValue());
            assertThat(coupon.getDescription().getValue()).isEqualTo(originalCoupon.getDescription().getValue());
            assertThat(coupon.getDiscount().getValue()).isEqualByComparingTo(originalCoupon.getDiscount().getValue());
            assertThat(coupon.getExpirationDate().getValue()).isEqualTo(originalCoupon.getExpirationDate().getValue());
            assertThat(coupon.isPublished()).isEqualTo(originalCoupon.isPublished());
            assertThat(coupon.getStatus()).isEqualTo(originalCoupon.getStatus());
        }
    }
}

