package br.com.tenda.coupon.presentation.controller;

import br.com.tenda.coupon.presentation.dto.CreateCouponRequest;
import br.com.tenda.coupon.presentation.dto.CouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Coupons", description = "API para gerenciamento de cupons de desconto")
public interface CouponApi {

    @Operation(
        summary = "Criar novo cupom",
        description = "Cria um novo cupom de desconto aplicando todas as regras de negócio"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Cupom criado com sucesso",
            content = @Content(schema = @Schema(implementation = CouponResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos ou violação de regra de negócio"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Cupom com este código já existe"
        )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            schema = @Schema(implementation = CreateCouponRequest.class),
            examples = @ExampleObject(
                name = "Exemplo de criação de cupom",
                value = """
                    {
                      "code": "ABC-123",
                      "description": "Desconto de primavera",
                      "discountValue": 10.5,
                      "expirationDate": "2026-02-16T20:32:17",
                      "published": true
                    }
                    """
            )
        )
    )
    ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request);

    @Operation(
        summary = "Buscar cupom por ID",
        description = "Retorna os detalhes de um cupom específico pelo seu identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cupom encontrado com sucesso",
            content = @Content(schema = @Schema(implementation = CouponResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cupom não encontrado"
        )
    })
    ResponseEntity<CouponResponse> getCouponById(@PathVariable("id") UUID id);

    @Operation(
        summary = "Deletar cupom",
        description = "Realiza soft delete de um cupom. Não permite deletar cupons já deletados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Cupom deletado com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cupom não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Cupom já está deletado"
        )
    })
    ResponseEntity<Void> deleteCoupon(@PathVariable("id") UUID id);

    @Operation(
        summary = "Listar todos os cupons",
        description = "Retorna uma lista paginada de todos os cupons cadastrados no sistema. " +
                      "Utilize os parâmetros 'page' (número da página, começando em 0) e " +
                      "'size' (quantidade de itens por página, padrão 20)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de cupons retornada com sucesso"
        )
    })
    ResponseEntity<Page<CouponResponse>> getAllCoupons(
        @Parameter(
            in = ParameterIn.QUERY,
            description = "Número da página (começa em 0)",
            schema = @Schema(type = "integer", defaultValue = "0")
        )
        @PageableDefault(size = 20) Pageable pageable
    );
}

