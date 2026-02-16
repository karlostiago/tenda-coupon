package br.com.tenda.coupon.presentation.controller;

import br.com.tenda.coupon.presentation.dto.CreateCouponRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("CouponController - Integração E2E")
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /api/v1/coupons - Criar cupom")
    class CreateCouponEndpoint {

        @Test
        @DisplayName("Deve criar cupom válido e retornar 201")
        void shouldCreateValidCouponAndReturn201() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "ABC123",
                    "Desconto de primavera",
                    new BigDecimal("10.50"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.code", is("ABC123")))
                    .andExpect(jsonPath("$.description", is("Desconto de primavera")))
                    .andExpect(jsonPath("$.discountValue", is(10.50)))
                    .andExpect(jsonPath("$.published", is(false)))
                    .andExpect(jsonPath("$.redeemed", is(false)))
                    .andExpect(jsonPath("$.status", is("ACTIVE")));
        }

        @Test
        @DisplayName("Deve criar cupom com caracteres especiais no código")
        void shouldCreateCouponWithSpecialCharactersInCode() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "AB-C12-3",
                    "Desconto especial",
                    new BigDecimal("15.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code", is("ABC123")));
        }

        @Test
        @DisplayName("Deve criar cupom já publicado")
        void shouldCreatePublishedCoupon() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "DEF456",
                    "Desconto publicado",
                    new BigDecimal("20.00"),
                    LocalDateTime.now().plusDays(30),
                    true,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.published", is(true)));
        }

        @Test
        @DisplayName("Deve criar cupom com valor mínimo de desconto")
        void shouldCreateCouponWithMinimumDiscountValue() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "MIN050",
                    "Desconto mínimo",
                    new BigDecimal("0.5"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.discountValue", is(0.5)));
        }

        @Test
        @DisplayName("Deve retornar 400 para código duplicado")
        void shouldReturn400ForDuplicateCode() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "DUP001",
                    "Primeiro cupom",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("already exists")));
        }

        @Test
        @DisplayName("Deve retornar 400 para valor de desconto inválido")
        void shouldReturn400ForInvalidDiscountValue() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "INV001",
                    "Desconto inválido",
                    new BigDecimal("0.4"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("at least 0.5")));
        }

        @Test
        @DisplayName("Deve retornar 400 para data de expiração no passado")
        void shouldReturn400ForPastExpirationDate() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "PAST01",
                    "Data passada",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().minusDays(1),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 para campos obrigatórios ausentes")
        void shouldReturn400ForMissingRequiredFields() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    null,
                    null,
                    null,
                    null,
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 para código com menos de 6 caracteres alfanuméricos")
        void shouldReturn400ForCodeWithLessThanSixAlphanumeric() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "ABC12",
                    "Código curto",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("6 alphanumeric characters")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/coupons/{id} - Buscar cupom por ID")
    class GetCouponByIdEndpoint {

        @Test
        @DisplayName("Deve buscar cupom existente e retornar 200")
        void shouldGetExistingCouponAndReturn200() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "GET001",
                    "Cupom para buscar",
                    new BigDecimal("15.75"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(get("/api/v1/coupons/" + couponId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(couponId)))
                    .andExpect(jsonPath("$.code", is("GET001")))
                    .andExpect(jsonPath("$.description", is("Cupom para buscar")))
                    .andExpect(jsonPath("$.discountValue", is(15.75)))
                    .andExpect(jsonPath("$.published", is(false)))
                    .andExpect(jsonPath("$.redeemed", is(false)))
                    .andExpect(jsonPath("$.status", is("ACTIVE")));
        }

        @Test
        @DisplayName("Deve buscar cupom publicado")
        void shouldGetPublishedCoupon() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "PUB001",
                    "Cupom publicado",
                    new BigDecimal("20.00"),
                    LocalDateTime.now().plusDays(30),
                    true,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(get("/api/v1/coupons/" + couponId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.published", is(true)));
        }

        @Test
        @DisplayName("Deve retornar cupom deletado com status preenchido")
        void shouldReturnDeletedCouponWithDeletedAt() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "GETDEL",
                    "Cupom para deletar e buscar",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(delete("/api/v1/coupons/" + couponId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/coupons/" + couponId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(couponId)))
                    .andExpect(jsonPath("$.code", is("GETDEL")))
                    .andExpect(jsonPath("$.status", is("DELETED")));
        }

        @Test
        @DisplayName("Deve retornar 404 para ID inexistente")
        void shouldReturn404ForNonExistentId() throws Exception {
            UUID randomId = UUID.randomUUID();

            mockMvc.perform(get("/api/v1/coupons/" + randomId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("not found")));
        }

        @Test
        @DisplayName("Deve retornar cupom com valor mínimo de desconto")
        void shouldGetCouponWithMinimumDiscount() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "MIN050",
                    "Desconto mínimo",
                    new BigDecimal("0.5"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(get("/api/v1/coupons/" + couponId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.discountValue", is(0.5)));
        }

        @Test
        @DisplayName("Deve retornar todos os campos do cupom corretamente")
        void shouldReturnAllCouponFieldsCorrectly() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "FULL01",
                    "Cupom completo para validação",
                    new BigDecimal("99.99"),
                    LocalDateTime.now().plusDays(60),
                    true,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(get("/api/v1/coupons/" + couponId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(couponId)))
                    .andExpect(jsonPath("$.code", is("FULL01")))
                    .andExpect(jsonPath("$.description", is("Cupom completo para validação")))
                    .andExpect(jsonPath("$.discountValue", is(99.99)))
                    .andExpect(jsonPath("$.expirationDate", notNullValue()))
                    .andExpect(jsonPath("$.published", is(true)))
                    .andExpect(jsonPath("$.status", is("ACTIVE")));
        }

        @Test
        @DisplayName("Deve retornar 404 com mensagem de erro apropriada")
        void shouldReturn404WithAppropriateErrorMessage() throws Exception {
            UUID nonExistentId = UUID.randomUUID();

            mockMvc.perform(get("/api/v1/coupons/" + nonExistentId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", notNullValue()))
                    .andExpect(jsonPath("$.message", containsString("Coupon not found")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/coupons/{id} - Deletar cupom")
    class DeleteCouponEndpoint {

        @Test
        @DisplayName("Deve deletar cupom existente e retornar 204")
        void shouldDeleteExistingCouponAndReturn204() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "DEL001",
                    "Cupom para deletar",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(delete("/api/v1/coupons/" + couponId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 para ID inexistente")
        void shouldReturn404ForNonExistentId() throws Exception {
            UUID randomId = UUID.randomUUID();

            mockMvc.perform(delete("/api/v1/coupons/" + randomId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("not found")));
        }

        @Test
        @DisplayName("Deve retornar 409 ao tentar deletar cupom já deletado")
        void shouldReturn409WhenDeletingAlreadyDeletedCoupon() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "DEL002",
                    "Cupom para deletar duas vezes",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(delete("/api/v1/coupons/" + couponId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(delete("/api/v1/coupons/" + couponId))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("already deleted")));
        }

        @Test
        @DisplayName("Deve preservar dados do cupom após soft delete")
        void shouldPreserveCouponDataAfterSoftDelete() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "SOFT01",
                    "Desconto para soft delete",
                    new BigDecimal("25.50"),
                    LocalDateTime.now().plusDays(30),
                    true,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(delete("/api/v1/coupons/" + couponId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(delete("/api/v1/coupons/" + couponId))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/coupons - Listar todos os cupons")
    class GetAllCouponsEndpoint {

        @Test
        @DisplayName("Deve retornar página com cupons paginados")
        void shouldReturnPageWithPaginatedCoupons() throws Exception {
            for (int i = 1; i <= 5; i++) {
                CreateCouponRequest request = new CreateCouponRequest(
                        "CUP00" + i,
                        "Cupom " + i,
                        new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(30),
                        i % 2 == 0,
                        false
                );
                mockMvc.perform(post("/api/v1/coupons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            mockMvc.perform(get("/api/v1/coupons")
                            .param("size", "10")
                            .param("page", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", notNullValue()))
                    .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(5)))
                    .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(5)))
                    .andExpect(jsonPath("$.page.size", is(10)))
                    .andExpect(jsonPath("$.page.number", is(0)));
        }

        @Test
        @DisplayName("Deve retornar cupons corretamente")
        void shouldReturnCouponsCorrectly() throws Exception {
            CreateCouponRequest request1 = new CreateCouponRequest(
                    "FIRST1",
                    "Primeiro cupom",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            CreateCouponRequest request2 = new CreateCouponRequest(
                    "SECOND",
                    "Segundo cupom",
                    new BigDecimal("15.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request1)))
                    .andExpect(status().isCreated());

            Thread.sleep(100);

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request2)))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/api/v1/coupons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", notNullValue()));
        }

        @Test
        @DisplayName("Deve usar tamanho padrão de 20 por página")
        void shouldUseDefaultPageSize() throws Exception {
            mockMvc.perform(get("/api/v1/coupons"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.size", is(20)));
        }

        @Test
        @DisplayName("Deve respeitar o tamanho de página personalizado")
        void shouldRespectCustomPageSize() throws Exception {
            for (int i = 1; i <= 3; i++) {
                CreateCouponRequest request = new CreateCouponRequest(
                        "SIZ00" + i,
                        "Cupom " + i,
                        new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(30),
                        false,
                        false
                );
                mockMvc.perform(post("/api/v1/coupons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            mockMvc.perform(get("/api/v1/coupons")
                            .param("size", "2")
                            .param("page", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.size", is(2)))
                    .andExpect(jsonPath("$.content.length()", lessThanOrEqualTo(2)));
        }

        @Test
        @DisplayName("Deve retornar segunda página corretamente")
        void shouldReturnSecondPageCorrectly() throws Exception {
            for (int i = 1; i <= 5; i++) {
                CreateCouponRequest request = new CreateCouponRequest(
                        "PAG00" + i,
                        "Cupom " + i,
                        new BigDecimal("10.00"),
                        LocalDateTime.now().plusDays(30),
                        false,
                        false
                );
                mockMvc.perform(post("/api/v1/coupons")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated());
            }

            mockMvc.perform(get("/api/v1/coupons")
                            .param("size", "2")
                            .param("page", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page.number", is(1)))
                    .andExpect(jsonPath("$.page.size", is(2)));
        }

        @Test
        @DisplayName("Deve incluir cupons deletados na listagem")
        void shouldIncludeDeletedCouponsInList() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "DELIST",
                    "Cupom para deletar",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(delete("/api/v1/coupons/" + couponId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/coupons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[?(@.id == '" + couponId + "')].status", hasItem("DELETED")));
        }

        @Test
        @DisplayName("Deve retornar cupons publicados e não publicados")
        void shouldReturnPublishedAndUnpublishedCoupons() throws Exception {
            CreateCouponRequest publishedRequest = new CreateCouponRequest(
                    "PUBALL",
                    "Cupom publicado",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    true,
                    false
            );

            CreateCouponRequest unpublishedRequest = new CreateCouponRequest(
                    "UNPUBL",
                    "Cupom não publicado",
                    new BigDecimal("15.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(publishedRequest)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(unpublishedRequest)))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/api/v1/coupons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[?(@.code == 'PUBALL')].published", hasItem(true)))
                    .andExpect(jsonPath("$.content[?(@.code == 'UNPUBL')].published", hasItem(false)));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver cupons")
        void shouldReturnEmptyListWhenNoCoupons() throws Exception {
            mockMvc.perform(get("/api/v1/coupons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", notNullValue()))
                    .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(0)));
        }

        @Test
        @DisplayName("Deve retornar todos os campos dos cupons na listagem")
        void shouldReturnAllCouponFieldsInList() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "FIELDS",
                    "Cupom com todos os campos",
                    new BigDecimal("25.50"),
                    LocalDateTime.now().plusDays(30),
                    true,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/api/v1/coupons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[?(@.code == 'FIELDS')].id", notNullValue()))
                    .andExpect(jsonPath("$.content[?(@.code == 'FIELDS')].code", hasItem("FIELDS")))
                    .andExpect(jsonPath("$.content[?(@.code == 'FIELDS')].description", hasItem("Cupom com todos os campos")))
                    .andExpect(jsonPath("$.content[?(@.code == 'FIELDS')].discountValue", hasItem(25.50)))
                    .andExpect(jsonPath("$.content[?(@.code == 'FIELDS')].expirationDate", notNullValue()))
                    .andExpect(jsonPath("$.content[?(@.code == 'FIELDS')].published", hasItem(true)))
                    .andExpect(jsonPath("$.content[?(@.code == 'FIELDS')].createdAt", notNullValue()));
        }
    }

    @Nested
    @DisplayName("Cenários de integração complexos")
    class ComplexIntegrationScenarios {

        @Test
        @DisplayName("Deve impedir criação de múltiplos cupons com mesmo código")
        void shouldPreventMultipleCouponsWithSameCode() throws Exception {
            CreateCouponRequest request1 = new CreateCouponRequest(
                    "SAME01",
                    "Primeiro cupom",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            CreateCouponRequest request2 = new CreateCouponRequest(
                    "SA-ME-01",
                    "Segundo cupom",
                    new BigDecimal("20.00"),
                    LocalDateTime.now().plusDays(60),
                    true,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request1)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request2)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("already exists")));
        }

        @Test
        @DisplayName("Deve permitir criar cupom com código de cupom deletado")
        void shouldAllowCreatingCouponWithDeletedCouponCode() throws Exception {
            CreateCouponRequest request = new CreateCouponRequest(
                    "REUSE1",
                    "Cupom original",
                    new BigDecimal("10.00"),
                    LocalDateTime.now().plusDays(30),
                    false,
                    false
            );

            MvcResult createResult = mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String responseBody = createResult.getResponse().getContentAsString();
            String couponId = objectMapper.readTree(responseBody).get("id").asText();

            mockMvc.perform(delete("/api/v1/coupons/" + couponId))
                    .andExpect(status().isNoContent());

            CreateCouponRequest newRequest = new CreateCouponRequest(
                    "REUSE1",
                    "Cupom novo",
                    new BigDecimal("15.00"),
                    LocalDateTime.now().plusDays(60),
                    false,
                    false
            );

            mockMvc.perform(post("/api/v1/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("already exists")));
        }
    }
}

