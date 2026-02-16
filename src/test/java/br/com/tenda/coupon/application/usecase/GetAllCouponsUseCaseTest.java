package br.com.tenda.coupon.application.usecase;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAllCouponsUseCase")
class GetAllCouponsUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private GetAllCouponsUseCase getAllCouponsUseCase;

    private List<Coupon> coupons;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(30);

        Coupon coupon1 = Coupon.create(
                "ABC123",
                "Desconto de verão",
                new BigDecimal("10.50"),
                futureDate,
                false,
                false
        );

        Coupon coupon2 = Coupon.create(
                "DEF456",
                "Desconto de inverno",
                new BigDecimal("20.00"),
                futureDate.plusDays(30),
                true,
                false
        );

        Coupon coupon3 = Coupon.create(
                "GHI789",
                "Desconto de primavera",
                new BigDecimal("15.75"),
                futureDate.plusDays(60),
                false,
                false
        );

        coupons = Arrays.asList(coupon1, coupon2, coupon3);
        pageable = PageRequest.of(0, 20);
    }

    @Nested
    @DisplayName("Quando buscar todos os cupons com sucesso")
    class WhenGettingAllCouponsSuccessfully {

        @Test
        @DisplayName("Deve retornar página com cupons")
        void shouldReturnPageWithCoupons() {
            Page<Coupon> couponsPage = new PageImpl<>(coupons, pageable, coupons.size());
            when(couponRepository.findAll(pageable)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(20);

            verify(couponRepository).findAll(pageable);
            verifyNoMoreInteractions(couponRepository);
        }

        @Test
        @DisplayName("Deve retornar cupons na ordem correta")
        void shouldReturnCouponsInCorrectOrder() {
            Page<Coupon> couponsPage = new PageImpl<>(coupons, pageable, coupons.size());
            when(couponRepository.findAll(pageable)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(pageable);

            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent().get(0).getCodeValue()).isEqualTo("ABC123");
            assertThat(result.getContent().get(1).getCodeValue()).isEqualTo("DEF456");
            assertThat(result.getContent().get(2).getCodeValue()).isEqualTo("GHI789");

            verify(couponRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar cupons publicados e não publicados")
        void shouldReturnPublishedAndUnpublishedCoupons() {
            Page<Coupon> couponsPage = new PageImpl<>(coupons, pageable, coupons.size());
            when(couponRepository.findAll(pageable)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(pageable);

            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent()).anyMatch(Coupon::isPublished);
            assertThat(result.getContent()).anyMatch(coupon -> !coupon.isPublished());

            verify(couponRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Deve incluir cupons deletados na listagem")
        void shouldIncludeDeletedCouponsInList() {
            Coupon deletedCoupon = coupons.get(0);
            deletedCoupon.delete();

            Page<Coupon> couponsPage = new PageImpl<>(coupons, pageable, coupons.size());
            when(couponRepository.findAll(pageable)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(pageable);

            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent()).anyMatch(coupon -> "DELETED".equalsIgnoreCase(coupon.getStatus()));

            verify(couponRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Quando buscar página vazia")
    class WhenGettingEmptyPage {

        @Test
        @DisplayName("Deve retornar página vazia quando não houver cupons")
        void shouldReturnEmptyPageWhenNoCoupons() {
            Page<Coupon> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(couponRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(0);

            verify(couponRepository).findAll(pageable);
            verifyNoMoreInteractions(couponRepository);
        }
    }

    @Nested
    @DisplayName("Quando trabalhar com paginação")
    class WhenWorkingWithPagination {

        @Test
        @DisplayName("Deve respeitar o tamanho da página solicitado")
        void shouldRespectRequestedPageSize() {
            Pageable customPageable = PageRequest.of(0, 2);
            List<Coupon> limitedCoupons = coupons.subList(0, 2);
            Page<Coupon> couponsPage = new PageImpl<>(limitedCoupons, customPageable, coupons.size());

            when(couponRepository.findAll(customPageable)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(customPageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getSize()).isEqualTo(2);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(2);

            verify(couponRepository).findAll(customPageable);
        }

        @Test
        @DisplayName("Deve retornar segunda página corretamente")
        void shouldReturnSecondPageCorrectly() {
            Pageable secondPage = PageRequest.of(1, 2);
            List<Coupon> secondPageCoupons = coupons.subList(2, 3);
            Page<Coupon> couponsPage = new PageImpl<>(secondPageCoupons, secondPage, coupons.size());

            when(couponRepository.findAll(secondPage)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(secondPage);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.isLast()).isTrue();

            verify(couponRepository).findAll(secondPage);
        }


        @Test
        @DisplayName("Deve usar tamanho padrão de 20 quando não especificado")
        void shouldUseDefaultPageSizeWhenNotSpecified() {
            Pageable defaultPageable = PageRequest.of(0, 20);
            Page<Coupon> couponsPage = new PageImpl<>(coupons, defaultPageable, coupons.size());

            when(couponRepository.findAll(defaultPageable)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(defaultPageable);

            assertThat(result.getSize()).isEqualTo(20);
            verify(couponRepository).findAll(defaultPageable);
        }
    }

    @Nested
    @DisplayName("Quando trabalhar com diferentes estados de cupons")
    class WhenWorkingWithDifferentCouponStates {

        @Test
        @DisplayName("Deve retornar cupons com diferentes datas de expiração")
        void shouldReturnCouponsWithDifferentExpirationDates() {
            // Create coupons with different expiration dates in the future
            Coupon shortTermCoupon = Coupon.create(
                    "SHORT1",
                    "Cupom de curto prazo",
                    new BigDecimal("5.00"),
                    LocalDateTime.now().plusDays(5),
                    true,
                    false
            );

            Coupon longTermCoupon = Coupon.create(
                    "LONG01",
                    "Cupom de longo prazo",
                    new BigDecimal("8.00"),
                    LocalDateTime.now().plusDays(90),
                    true,
                    false
            );

            List<Coupon> mixedCoupons = Arrays.asList(coupons.get(0), shortTermCoupon, longTermCoupon);
            Page<Coupon> couponsPage = new PageImpl<>(mixedCoupons, pageable, mixedCoupons.size());

            when(couponRepository.findAll(pageable)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(pageable);

            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent())
                    .extracting(c -> c.getExpirationDate().getValue())
                    .anyMatch(date -> date.isAfter(LocalDateTime.now().plusDays(80)));

            verify(couponRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar cupons com diferentes valores de desconto")
        void shouldReturnCouponsWithDifferentDiscountValues() {
            Page<Coupon> couponsPage = new PageImpl<>(coupons, pageable, coupons.size());
            when(couponRepository.findAll(pageable)).thenReturn(couponsPage);

            Page<Coupon> result = getAllCouponsUseCase.execute(pageable);

            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getContent())
                    .extracting(c -> c.getDiscount().getValue())
                    .containsExactly(
                            new BigDecimal("10.50"),
                            new BigDecimal("20.00"),
                            new BigDecimal("15.75")
                    );

            verify(couponRepository).findAll(pageable);
        }
    }
}

